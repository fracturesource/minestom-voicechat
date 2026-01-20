package dev.lu15.voicechat.network.voice.packets

import dev.lu15.voicechat.network.voice.VoiceFlags
import dev.lu15.voicechat.network.voice.VoicePacket
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate
import java.util.UUID

data class PlayerSoundPacket(
    val channel: UUID,
    val sender: UUID,
    val data: ByteArray,
    val sequenceNumber: Long,
    val distance: Float,
    val whispering: Boolean,
    val category: String?
) : VoicePacket<PlayerSoundPacket> {
    private constructor(
        channel: UUID,
        sender: UUID,
        data: ByteArray,
        sequenceNumber: Long,
        distance: Float,
        flags: VoiceFlags
    ) : this(
        channel,
        sender,
        data,
        sequenceNumber,
        distance,
        flags.whispering,
        flags.category,
    )

    override fun id(): Int {
        return 0x2
    }

    override fun serializer(): NetworkBuffer.Type<PlayerSoundPacket> {
        return SERIALIZER
    }

    companion object {
        val SERIALIZER: NetworkBuffer.Type<PlayerSoundPacket> = NetworkBufferTemplate.template(
            NetworkBuffer.UUID,
            PlayerSoundPacket::channel,
            NetworkBuffer.UUID,
            PlayerSoundPacket::sender,
            NetworkBuffer.BYTE_ARRAY,
            PlayerSoundPacket::data,
            NetworkBuffer.LONG,
            PlayerSoundPacket::sequenceNumber,
            NetworkBuffer.FLOAT,
            PlayerSoundPacket::distance,
            VoiceFlags.SERIALIZER,
            { packet -> VoiceFlags.flags(packet.whispering, packet.category) },
            ::PlayerSoundPacket,
        )
    }
}
