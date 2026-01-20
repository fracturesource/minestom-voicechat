package dev.lu15.voicechat.network.voice.packets

import dev.lu15.voicechat.network.voice.VoicePacket
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate

class KeepAlivePacket : VoicePacket<KeepAlivePacket> {
    override fun id(): Int {
        return 0x8
    }

    override fun serializer(): NetworkBuffer.Type<KeepAlivePacket> {
        return SERIALIZER
    }

    companion object {
        val SERIALIZER: NetworkBuffer.Type<KeepAlivePacket> = NetworkBufferTemplate.template(::KeepAlivePacket)
    }
}
