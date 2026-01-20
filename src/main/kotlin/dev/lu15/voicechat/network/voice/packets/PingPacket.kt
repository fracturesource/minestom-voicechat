package dev.lu15.voicechat.network.voice.packets

import dev.lu15.voicechat.network.voice.VoicePacket
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate
import java.util.UUID

data class PingPacket(
    val player: UUID,
    val timestamp: Long
) : VoicePacket<PingPacket> {
    override fun id(): Int {
        return 0x7
    }

    override fun serializer(): NetworkBuffer.Type<PingPacket> {
        return SERIALIZER
    }

    companion object {
        val SERIALIZER: NetworkBuffer.Type<PingPacket> = NetworkBufferTemplate.template(
            NetworkBuffer.UUID, PingPacket::player,
            NetworkBuffer.LONG, PingPacket::timestamp,
            ::PingPacket,
        )
    }
}
