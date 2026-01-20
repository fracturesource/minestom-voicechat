package dev.lu15.voicechat.network.minecraft.packets.clientbound

import dev.lu15.voicechat.VoiceChat
import dev.lu15.voicechat.VoiceCodec
import dev.lu15.voicechat.network.NetworkTypes
import dev.lu15.voicechat.network.minecraft.Packet
import net.kyori.adventure.key.Key
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate
import java.util.UUID

data class SecretPacket(
    val secret: UUID,
    val port: Int,
    val player: UUID,
    val codec: VoiceCodec,
    val mtu: Int,
    val distance: Double,
    val keepAlive: Int,
    val groups: Boolean,
    val host: String,
    val recording: Boolean
) : Packet<SecretPacket> {
    override fun id(): Key {
        return IDENTIFIER
    }

    override fun serializer(): NetworkBuffer.Type<SecretPacket> {
        return SERIALIZER
    }

    companion object {
        val IDENTIFIER: Key = Key.key(VoiceChat.NAMESPACE, "secret")
        val SERIALIZER: NetworkBuffer.Type<SecretPacket> = NetworkBufferTemplate.template(
            NetworkBuffer.UUID, SecretPacket::secret,
            NetworkBuffer.INT, SecretPacket::port,
            NetworkBuffer.UUID, SecretPacket::player,
            NetworkTypes.byteEnum(VoiceCodec::class.java), SecretPacket::codec,
            NetworkBuffer.INT, SecretPacket::mtu,
            NetworkBuffer.DOUBLE, SecretPacket::distance,
            NetworkBuffer.INT, SecretPacket::keepAlive,
            NetworkBuffer.BOOLEAN, SecretPacket::groups,
            NetworkBuffer.STRING, SecretPacket::host,
            NetworkBuffer.BOOLEAN, SecretPacket::recording,
            ::SecretPacket,
        )
    }
}
