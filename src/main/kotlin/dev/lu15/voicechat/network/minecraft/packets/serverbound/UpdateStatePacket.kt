package dev.lu15.voicechat.network.minecraft.packets.serverbound

import dev.lu15.voicechat.VoiceChat
import dev.lu15.voicechat.network.minecraft.Packet
import net.kyori.adventure.key.Key
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate

data class UpdateStatePacket(val disabled: Boolean) : Packet<UpdateStatePacket> {
    override fun id(): Key {
        return IDENTIFIER
    }

    override fun serializer(): NetworkBuffer.Type<UpdateStatePacket> {
        return SERIALIZER
    }

    companion object {
        val IDENTIFIER: Key = Key.key(VoiceChat.NAMESPACE, "update_state")
        val SERIALIZER: NetworkBuffer.Type<UpdateStatePacket> = NetworkBufferTemplate.template(
            NetworkBuffer.BOOLEAN, UpdateStatePacket::disabled,
            ::UpdateStatePacket,
        )
    }
}
