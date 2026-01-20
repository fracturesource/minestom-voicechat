package dev.lu15.voicechat.network.voice.packets

import dev.lu15.voicechat.network.voice.VoicePacket
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate

data class MicPacket(
    val data: ByteArray,
    val sequenceNumber: Long,
    val whispering: Boolean
) : VoicePacket<MicPacket> {
    override fun id(): Int {
        return 0x1
    }

    override fun serializer(): NetworkBuffer.Type<MicPacket> {
        return SERIALIZER
    }

    override fun timeToLive(): Long {
        return 500
    }

    companion object {
        val SERIALIZER: NetworkBuffer.Type<MicPacket> =
            NetworkBufferTemplate.template(
                NetworkBuffer.BYTE_ARRAY, MicPacket::data,
                NetworkBuffer.LONG, MicPacket::sequenceNumber,
                NetworkBuffer.BOOLEAN, MicPacket::whispering,
                ::MicPacket,
            )
    }
}
