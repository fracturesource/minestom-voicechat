package dev.lu15.voicechat.network.voice

import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketAddress
import java.net.SocketException

class VoiceSocket {
    private val buffer = ByteArray(4096)

    private var socket: DatagramSocket? = null

    @Throws(SocketException::class)
    fun open(address: InetAddress, port: Int) {
        if (socket != null) error("Socket already open")

        val socket = DatagramSocket(port, address).also { socket = it }

        // https://datatracker.ietf.org/doc/html/rfc1349
        // setting this will allow the socket to prioritize reliability over speed
        socket.trafficClass = 0x04
    }

    @Throws(IOException::class)
    fun read(): RawPacket {
        if (closed()) error("Socket not open")

        val packet = DatagramPacket(this.buffer, this.buffer.size)
        socket?.receive(packet)

        val timestamp = System.currentTimeMillis()
        val data = ByteArray(packet.getLength())
        System.arraycopy(packet.data, packet.getOffset(), data, 0, packet.getLength())

        return RawPacket(data, packet.socketAddress, timestamp)
    }

    @Throws(IOException::class)
    fun write(data: ByteArray, address: SocketAddress) {
        if (closed()) error("Socket not open")
        socket?.send(DatagramPacket(data, data.size, address))
    }

    fun close() {
        if (closed()) return
        socket?.close()
        socket = null
    }

    fun closed(): Boolean {
        val socket = socket
        return socket == null || socket.isClosed
    }
}
