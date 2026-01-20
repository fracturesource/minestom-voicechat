package dev.lu15.voicechat.network.minecraft.packets

import dev.lu15.voicechat.VoiceChat
import dev.lu15.voicechat.network.minecraft.Packet
import dev.lu15.voicechat.network.minecraft.packets.clientbound.CategoryAddedPacket
import dev.lu15.voicechat.network.minecraft.packets.clientbound.GroupChangedPacket
import dev.lu15.voicechat.network.minecraft.packets.clientbound.GroupCreatedPacket
import dev.lu15.voicechat.network.minecraft.packets.clientbound.GroupRemovedPacket
import dev.lu15.voicechat.network.minecraft.packets.clientbound.SecretPacket
import dev.lu15.voicechat.network.minecraft.packets.clientbound.VoiceStateRemovedPacket
import dev.lu15.voicechat.network.minecraft.packets.clientbound.VoiceStateUpdatedPacket
import dev.lu15.voicechat.network.minecraft.packets.clientbound.VoiceStatesUpdatedPacket
import dev.lu15.voicechat.network.minecraft.packets.serverbound.CreateGroupPacket
import dev.lu15.voicechat.network.minecraft.packets.serverbound.HandshakePacket
import dev.lu15.voicechat.network.minecraft.packets.serverbound.JoinGroupPacket
import dev.lu15.voicechat.network.minecraft.packets.serverbound.LeaveGroupPacket
import dev.lu15.voicechat.network.minecraft.packets.serverbound.UpdateStatePacket
import net.kyori.adventure.key.Key
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.packet.server.common.PluginMessagePacket

object VoiceChatPacketSerializer {
    private val serializers: MutableMap<Key, NetworkBuffer.Type<Packet<*>>> = mutableMapOf()

    init {
        // clientbound
        register(SecretPacket.IDENTIFIER, SecretPacket.SERIALIZER)
        register(VoiceStateRemovedPacket.IDENTIFIER, VoiceStateRemovedPacket.SERIALIZER)
        register(VoiceStateUpdatedPacket.IDENTIFIER, VoiceStateUpdatedPacket.SERIALIZER)
        register(VoiceStatesUpdatedPacket.IDENTIFIER, VoiceStatesUpdatedPacket.SERIALIZER)
        register(GroupCreatedPacket.IDENTIFIER, GroupCreatedPacket.SERIALIZER)
        register(GroupChangedPacket.IDENTIFIER, GroupChangedPacket.SERIALIZER)
        register(GroupRemovedPacket.IDENTIFIER, GroupRemovedPacket.SERIALIZER)
        register(CategoryAddedPacket.IDENTIFIER, CategoryAddedPacket.SERIALIZER)
        register(CategoryAddedPacket.IDENTIFIER, CategoryAddedPacket.SERIALIZER)

        // serverbound
        register(HandshakePacket.IDENTIFIER, HandshakePacket.SERIALIZER)
        register(UpdateStatePacket.IDENTIFIER, UpdateStatePacket.SERIALIZER)
        register(JoinGroupPacket.IDENTIFIER, JoinGroupPacket.SERIALIZER)
        register(LeaveGroupPacket.IDENTIFIER, LeaveGroupPacket.SERIALIZER)
        register(CreateGroupPacket.IDENTIFIER, CreateGroupPacket.SERIALIZER)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Packet<T>> register(id: Key, serializer: NetworkBuffer.Type<T>) {
        if (id.namespace() != VoiceChat.NAMESPACE) error("ID with incorrect namespace used")
        serializers[id] = serializer as NetworkBuffer.Type<Packet<*>>
    }

    fun read(identifier: String, data: ByteArray): Packet<*>? {
        val key = Key.key(identifier)
        val serializer = serializers[key] ?: return null
        val buffer = NetworkBuffer.wrap(data, 0, data.size)
        return serializer.read(buffer)
    }

    fun <T : Packet<T>> write(packet: T): PluginMessagePacket {
        val serializer = packet.serializer()
        val buffer = NetworkBuffer.resizableBuffer()
        buffer.write(serializer, packet)

        val data = ByteArray(buffer.writeIndex().toInt())
        buffer.copyTo(0, data, 0, data.size.toLong())

        return PluginMessagePacket(packet.id().asString(), data)
    }
}
