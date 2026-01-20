package dev.lu15.voicechat.network.minecraft.packets.clientbound

import dev.lu15.voicechat.VoiceChat
import dev.lu15.voicechat.network.minecraft.Group
import dev.lu15.voicechat.network.minecraft.Packet
import net.kyori.adventure.key.Key
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate

data class GroupCreatedPacket(val group: Group) : Packet<GroupCreatedPacket> {
    override fun id(): Key {
        return IDENTIFIER
    }

    override fun serializer(): NetworkBuffer.Type<GroupCreatedPacket> {
        return SERIALIZER
    }

    companion object {
        val IDENTIFIER: Key = Key.key(VoiceChat.NAMESPACE, "add_group")
        val SERIALIZER: NetworkBuffer.Type<GroupCreatedPacket> = NetworkBufferTemplate.template(
            Group.NETWORK_TYPE, GroupCreatedPacket::group,
            ::GroupCreatedPacket,
        )
    }
}
