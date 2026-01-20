package dev.lu15.voicechat.network.voice.packets

import dev.lu15.voicechat.network.voice.VoiceFlags
import dev.lu15.voicechat.network.voice.VoicePacket
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate
import java.util.UUID

data class GroupSoundPacket(
    val channel: UUID,
    val sender: UUID,
    val data: ByteArray,
    val sequenceNumber: Long,
    val category: String?
) : VoicePacket<GroupSoundPacket> {
    private constructor(
        channel: UUID,
        sender: UUID,
        data: ByteArray,
        sequenceNumber: Long,
        flags: VoiceFlags
    ) : this(
        channel,
        sender,
        data,
        sequenceNumber,
        flags.category
    )

    override fun id(): Int {
        return 0x3
    }

    override fun serializer(): NetworkBuffer.Type<GroupSoundPacket> {
        return SERIALIZER
    }

    companion object {
        val SERIALIZER: NetworkBuffer.Type<GroupSoundPacket> = NetworkBufferTemplate.template(
            NetworkBuffer.UUID, GroupSoundPacket::channel,
            NetworkBuffer.UUID, GroupSoundPacket::sender,
            NetworkBuffer.BYTE_ARRAY, GroupSoundPacket::data,
            NetworkBuffer.LONG, GroupSoundPacket::sequenceNumber,
            VoiceFlags.SERIALIZER,
            { packet -> VoiceFlags.category(packet.category) },
            ::GroupSoundPacket,
        )
    }
}
