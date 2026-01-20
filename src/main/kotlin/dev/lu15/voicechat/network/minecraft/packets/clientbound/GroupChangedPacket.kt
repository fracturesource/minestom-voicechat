package dev.lu15.voicechat.network.minecraft.packets.clientbound

import dev.lu15.voicechat.VoiceChat
import dev.lu15.voicechat.network.minecraft.Packet
import net.kyori.adventure.key.Key
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate
import java.util.UUID

data class GroupChangedPacket(
    val group: UUID?,
    val incorrectPassword: Boolean
) : Packet<GroupChangedPacket> {
    override fun id(): Key {
        return IDENTIFIER
    }

    override fun serializer(): NetworkBuffer.Type<GroupChangedPacket> {
        return SERIALIZER
    }

    companion object {
        val IDENTIFIER: Key = Key.key(VoiceChat.NAMESPACE, "joined_group")
        val SERIALIZER: NetworkBuffer.Type<GroupChangedPacket> = NetworkBufferTemplate.template(
            NetworkBuffer.UUID.optional(), GroupChangedPacket::group,
            NetworkBuffer.BOOLEAN, GroupChangedPacket::incorrectPassword,
            ::GroupChangedPacket,
        )
    }
}
