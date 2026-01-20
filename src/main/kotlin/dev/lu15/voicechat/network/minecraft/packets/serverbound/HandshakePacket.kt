package dev.lu15.voicechat.network.minecraft.packets.serverbound

import dev.lu15.voicechat.VoiceChat
import dev.lu15.voicechat.network.minecraft.Packet
import net.kyori.adventure.key.Key
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate

data class HandshakePacket(val version: Int) : Packet<HandshakePacket> {
    override fun id(): Key {
        return IDENTIFIER
    }

    override fun serializer(): NetworkBuffer.Type<HandshakePacket> {
        return SERIALIZER
    }

    companion object {
        val IDENTIFIER: Key = Key.key(VoiceChat.NAMESPACE, "request_secret")
        val SERIALIZER: NetworkBuffer.Type<HandshakePacket> = NetworkBufferTemplate.template(
            NetworkBuffer.INT, HandshakePacket::version,
            ::HandshakePacket,
        )
    }
}
