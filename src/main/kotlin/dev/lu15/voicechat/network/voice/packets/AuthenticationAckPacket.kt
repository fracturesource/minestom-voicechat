package dev.lu15.voicechat.network.voice.packets

import dev.lu15.voicechat.network.voice.VoicePacket
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate

class AuthenticationAckPacket : VoicePacket<AuthenticationAckPacket> {
    override fun id(): Int {
        return 0x6
    }

    override fun serializer(): NetworkBuffer.Type<AuthenticationAckPacket> {
        return SERIALIZER
    }

    companion object {
        val SERIALIZER: NetworkBuffer.Type<AuthenticationAckPacket> = NetworkBufferTemplate.template(::AuthenticationAckPacket)
    }
}
