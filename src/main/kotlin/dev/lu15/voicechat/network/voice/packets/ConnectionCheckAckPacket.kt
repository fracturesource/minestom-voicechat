package dev.lu15.voicechat.network.voice.packets

import dev.lu15.voicechat.network.voice.VoicePacket
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate

class ConnectionCheckAckPacket : VoicePacket<ConnectionCheckAckPacket> {
    override fun id(): Int {
        return 0xA
    }

    override fun serializer(): NetworkBuffer.Type<ConnectionCheckAckPacket> {
        return SERIALIZER
    }

    companion object {
        val SERIALIZER: NetworkBuffer.Type<ConnectionCheckAckPacket> = NetworkBufferTemplate.template(::ConnectionCheckAckPacket)
    }
}
