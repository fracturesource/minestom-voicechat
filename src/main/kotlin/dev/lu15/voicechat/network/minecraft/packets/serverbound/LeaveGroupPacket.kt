package dev.lu15.voicechat.network.minecraft.packets.serverbound

import dev.lu15.voicechat.VoiceChat
import dev.lu15.voicechat.network.minecraft.Packet
import net.kyori.adventure.key.Key
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate

class LeaveGroupPacket : Packet<LeaveGroupPacket> {
    override fun id(): Key {
        return IDENTIFIER
    }

    override fun serializer(): NetworkBuffer.Type<LeaveGroupPacket> {
        return SERIALIZER
    }

    companion object {
        val IDENTIFIER: Key = Key.key(VoiceChat.NAMESPACE, "leave_group")
        val SERIALIZER: NetworkBuffer.Type<LeaveGroupPacket> = NetworkBufferTemplate.template(::LeaveGroupPacket)
    }
}
