package dev.lu15.voicechat.network.voice

import java.net.SocketAddress

data class RawPacket(
    val data: ByteArray,
    val address: SocketAddress,
    val timestamp: Long
)
