package dev.lu15.voicechat.network.minecraft.packets.serverbound

import dev.lu15.voicechat.VoiceChat
import dev.lu15.voicechat.network.minecraft.Packet
import net.kyori.adventure.key.Key
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate
import java.util.UUID

data class JoinGroupPacket(
    val group: UUID,
    val password: String?
) : Packet<JoinGroupPacket> {
    override fun id(): Key {
        return IDENTIFIER
    }

    override fun serializer(): NetworkBuffer.Type<JoinGroupPacket> {
        return SERIALIZER
    }

    companion object {
        val IDENTIFIER: Key = Key.key(VoiceChat.NAMESPACE, "set_group")
        val SERIALIZER: NetworkBuffer.Type<JoinGroupPacket> = NetworkBufferTemplate.template(
            NetworkBuffer.UUID, JoinGroupPacket::group,
            NetworkBuffer.STRING.optional(), JoinGroupPacket::password,
            ::JoinGroupPacket,
        )
    }
}
