package dev.lu15.voicechat.network.minecraft.packets.clientbound

import dev.lu15.voicechat.VoiceChat
import dev.lu15.voicechat.network.minecraft.Packet
import net.kyori.adventure.key.Key
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate
import java.util.UUID

data class VoiceStateRemovedPacket(val uuid: UUID) : Packet<VoiceStateRemovedPacket> {
    override fun id(): Key {
        return IDENTIFIER
    }

    override fun serializer(): NetworkBuffer.Type<VoiceStateRemovedPacket> {
        return SERIALIZER
    }

    companion object {
        val IDENTIFIER: Key = Key.key(VoiceChat.NAMESPACE, "remove_state")
        val SERIALIZER: NetworkBuffer.Type<VoiceStateRemovedPacket> = NetworkBufferTemplate.template(
            NetworkBuffer.UUID, VoiceStateRemovedPacket::uuid,
            ::VoiceStateRemovedPacket,
        )
    }
}
