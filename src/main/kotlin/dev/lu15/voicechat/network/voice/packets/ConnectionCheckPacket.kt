package dev.lu15.voicechat.network.voice.packets

import dev.lu15.voicechat.network.voice.VoicePacket
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate

class ConnectionCheckPacket : VoicePacket<ConnectionCheckPacket> {
    override fun id(): Int {
        return 0x9
    }

    override fun serializer(): NetworkBuffer.Type<ConnectionCheckPacket> {
        return SERIALIZER
    }

    companion object {
        val SERIALIZER: NetworkBuffer.Type<ConnectionCheckPacket> = NetworkBufferTemplate.template(::ConnectionCheckPacket)
    }
}
