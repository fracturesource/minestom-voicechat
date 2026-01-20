package dev.lu15.voicechat.network.voice.packets

import dev.lu15.voicechat.network.NetworkTypes
import dev.lu15.voicechat.network.voice.VoiceFlags
import dev.lu15.voicechat.network.voice.VoicePacket
import net.minestom.server.coordinate.Point
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate
import java.util.UUID

data class LocationSoundPacket(
    val channel: UUID,
    val sender: UUID,
    val position: Point,
    val data: ByteArray,
    val sequenceNumber: Long,
    val distance: Float,
    val category: String?
) : VoicePacket<LocationSoundPacket> {
    private constructor(
        channel: UUID,
        sender: UUID,
        position: Point,
        data: ByteArray,
        sequenceNumber: Long,
        distance: Float,
        flags: VoiceFlags
    ) : this(
        channel,
        sender,
        position,
        data,
        sequenceNumber,
        distance,
        flags.category
    )

    override fun id(): Int {
        return 0x4
    }

    override fun serializer(): NetworkBuffer.Type<LocationSoundPacket> {
        return SERIALIZER
    }

    companion object {
        val SERIALIZER: NetworkBuffer.Type<LocationSoundPacket> = NetworkBufferTemplate.template(
            NetworkBuffer.UUID,
            LocationSoundPacket::channel,
            NetworkBuffer.UUID,
            LocationSoundPacket::sender,
            NetworkTypes.POSITION,
            LocationSoundPacket::position,
            NetworkBuffer.BYTE_ARRAY,
            LocationSoundPacket::data,
            NetworkBuffer.LONG,
            LocationSoundPacket::sequenceNumber,
            NetworkBuffer.FLOAT,
            LocationSoundPacket::distance,
            VoiceFlags.SERIALIZER,
            { packet -> VoiceFlags.category(packet.category) },
            ::LocationSoundPacket,
        )
    }
}
