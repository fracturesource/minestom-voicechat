package dev.lu15.voicechat.network.voice

import net.minestom.server.network.NetworkBuffer

interface VoicePacket<T : VoicePacket<T>> {
    fun id(): Int

    fun serializer(): NetworkBuffer.Type<T>

    fun timeToLive(): Long {
        return 10000
    }
}
