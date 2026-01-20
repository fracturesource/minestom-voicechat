@file:Suppress("UnstableApiUsage")

package dev.lu15.voicechat.network.voice

import dev.lu15.voicechat.network.voice.encryption.AES
import dev.lu15.voicechat.network.voice.encryption.SecretUtilities.getSecret
import dev.lu15.voicechat.network.voice.packets.AuthenticatePacket
import dev.lu15.voicechat.network.voice.packets.AuthenticationAckPacket
import dev.lu15.voicechat.network.voice.packets.ConnectionCheckAckPacket
import dev.lu15.voicechat.network.voice.packets.ConnectionCheckPacket
import dev.lu15.voicechat.network.voice.packets.GroupSoundPacket
import dev.lu15.voicechat.network.voice.packets.KeepAlivePacket
import dev.lu15.voicechat.network.voice.packets.LocationSoundPacket
import dev.lu15.voicechat.network.voice.packets.MicPacket
import dev.lu15.voicechat.network.voice.packets.PingPacket
import dev.lu15.voicechat.network.voice.packets.PlayerSoundPacket
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.utils.collection.ObjectArray

object VoicePacketHandler {
    private const val MAGIC_BYTE: Byte = 255.toByte()
    private val suppliers: ObjectArray<NetworkBuffer.Type<VoicePacket<*>>> = ObjectArray.singleThread(0xA)

    init {
        register(0x1, MicPacket.SERIALIZER)
        register(0x2, PlayerSoundPacket.SERIALIZER)
        register(0x3, GroupSoundPacket.SERIALIZER)
        register(0x4, LocationSoundPacket.SERIALIZER)
        register(0x5, AuthenticatePacket.SERIALIZER)
        register(0x6, AuthenticationAckPacket.SERIALIZER)
        register(0x7, PingPacket.SERIALIZER)
        register(0x8, KeepAlivePacket.SERIALIZER)
        register(0x9, ConnectionCheckPacket.SERIALIZER)
        register(0xA, ConnectionCheckAckPacket.SERIALIZER)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : VoicePacket<T>> register(id: Int, supplier: NetworkBuffer.Type<T>) {
        suppliers.set(id, supplier as NetworkBuffer.Type<VoicePacket<*>>)
    }

    @Throws(Exception::class)
    fun read(packet: RawPacket): VoicePacket<*>? {
        val data = packet.data
        val outer = NetworkBuffer.wrap(data, 0, data.size)

        if (outer.read(NetworkBuffer.BYTE) != MAGIC_BYTE) error("Invalid magic byte")

        val player = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(outer.read(NetworkBuffer.UUID))
        if (player == null || !player.isOnline) return null // player has disconnected

        val secret = getSecret(player.uuid) ?: error("No secret for player")

        val decrypted = AES.decrypt(secret, outer.read(NetworkBuffer.BYTE_ARRAY)!!)
        val buffer = NetworkBuffer.wrap(decrypted, 0, decrypted.size)

        val id = buffer.read(NetworkBuffer.BYTE)!!.toInt()
        val supplier = suppliers.get(id) ?: error("Invalid packet id")

        return supplier.read(buffer)
    }

    fun <T : VoicePacket<T>> write(player: Player, packet: T): ByteArray {
        try {
            return write0(player, packet)
        } catch (e: Exception) {
            // the code running this method should be from simple-voice-chat-minestom itself,
            // so it should be safe to throw a runtime exception here - it's a b_ug in the code
            throw RuntimeException("Failed to write packet", e)
        }
    }

    @Throws(Exception::class)
    private fun <T : VoicePacket<T>> write0(player: Player, packet: T): ByteArray {
        val buffer = NetworkBuffer.resizableBuffer()
        buffer.write(NetworkBuffer.BYTE, MAGIC_BYTE)

        val secret = getSecret(player) ?: error("No secret for player: ${player.uuid}")

        val inner = NetworkBuffer.resizableBuffer()
        inner.write(NetworkBuffer.BYTE, packet.id().toByte())
        packet.serializer().write(inner, packet)

        val data = ByteArray(inner.writeIndex().toInt())
        inner.copyTo(0, data, 0, data.size.toLong())

        val encrypted = AES.encrypt(secret, data)
        buffer.write(NetworkBuffer.BYTE_ARRAY, encrypted)

        val result = ByteArray(buffer.writeIndex().toInt())
        buffer.copyTo(0, result, 0, result.size.toLong())

        return result
    }
}
