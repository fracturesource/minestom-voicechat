package dev.lu15.voicechat.network.minecraft.packets.clientbound

import dev.lu15.voicechat.VoiceChat
import dev.lu15.voicechat.network.minecraft.Packet
import net.kyori.adventure.key.Key
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate
import java.util.UUID

data class GroupRemovedPacket(val group: UUID) : Packet<GroupRemovedPacket> {
    override fun id(): Key {
        return IDENTIFIER
    }

    override fun serializer(): NetworkBuffer.Type<GroupRemovedPacket> {
        return SERIALIZER
    }

    companion object {
        val IDENTIFIER: Key = Key.key(VoiceChat.NAMESPACE, "remove_group")
        val SERIALIZER: NetworkBuffer.Type<GroupRemovedPacket> = NetworkBufferTemplate.template(
            NetworkBuffer.UUID, GroupRemovedPacket::group,
            ::GroupRemovedPacket,
        )
    }
}
