package dev.lu15.voicechat.network.minecraft.packets.serverbound

import dev.lu15.voicechat.VoiceChat
import dev.lu15.voicechat.network.NetworkTypes.shortEnum
import dev.lu15.voicechat.network.minecraft.Group
import dev.lu15.voicechat.network.minecraft.Packet
import net.kyori.adventure.key.Key
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate

data class CreateGroupPacket(
    val name: String,
    val password: String?,
    val type: Group.Type
) : Packet<CreateGroupPacket> {
    override fun id(): Key {
        return IDENTIFIER
    }

    override fun serializer(): NetworkBuffer.Type<CreateGroupPacket> {
        return SERIALIZER
    }

    companion object {
        val IDENTIFIER: Key = Key.key(VoiceChat.NAMESPACE, "create_group")
        val SERIALIZER: NetworkBuffer.Type<CreateGroupPacket> = NetworkBufferTemplate.template(
            NetworkBuffer.STRING, CreateGroupPacket::name,
            NetworkBuffer.STRING.optional(), CreateGroupPacket::password,
            shortEnum(Group.Type::class.java), CreateGroupPacket::type,
            ::CreateGroupPacket,
        )
    }
}
