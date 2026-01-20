package dev.lu15.voicechat.network.voice

import net.minestom.server.network.NetworkBuffer
import kotlin.experimental.and
import kotlin.experimental.or

data class VoiceFlags(
    val whispering: Boolean,
    val category: String?
) {
    companion object {
        const val WHISPERING: Byte = 1
        const val CATEGORY: Byte = 2

        val SERIALIZER: NetworkBuffer.Type<VoiceFlags> = object : NetworkBuffer.Type<VoiceFlags> {
            override fun write(buffer: NetworkBuffer, value: VoiceFlags) {
                var flags: Byte = 0

                if (value.whispering) flags = flags or WHISPERING
                if (value.category != null) flags = flags or CATEGORY

                buffer.write(NetworkBuffer.BYTE, flags)

                if (value.category != null) {
                    buffer.write(NetworkBuffer.STRING, value.category)
                }
            }

            override fun read(buffer: NetworkBuffer): VoiceFlags {
                val flags: Byte = buffer.read(NetworkBuffer.BYTE)

                val whispering = flags and WHISPERING != 0.toByte()
                val category = if (flags and CATEGORY != 0.toByte()) {
                    buffer.read(NetworkBuffer.STRING)
                } else null

                return VoiceFlags(whispering, category)
            }
        }

        fun category(category: String?): VoiceFlags {
            return VoiceFlags(false, category)
        }

        fun flags(whispering: Boolean, category: String?): VoiceFlags {
            return VoiceFlags(whispering, category)
        }
    }
}
