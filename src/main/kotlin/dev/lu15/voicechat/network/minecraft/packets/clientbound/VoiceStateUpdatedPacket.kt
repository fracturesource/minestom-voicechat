package dev.lu15.voicechat.network.minecraft.packets.clientbound

import dev.lu15.voicechat.VoiceChat
import dev.lu15.voicechat.network.minecraft.Packet
import dev.lu15.voicechat.network.minecraft.VoiceState
import net.kyori.adventure.key.Key
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate

data class VoiceStateUpdatedPacket(val state: VoiceState) : Packet<VoiceStateUpdatedPacket> {
    override fun id(): Key {
        return IDENTIFIER
    }

    override fun serializer(): NetworkBuffer.Type<VoiceStateUpdatedPacket> {
        return SERIALIZER
    }

    companion object {
        val IDENTIFIER: Key = Key.key(VoiceChat.NAMESPACE, "state")
        val SERIALIZER: NetworkBuffer.Type<VoiceStateUpdatedPacket> = NetworkBufferTemplate.template(
            VoiceState.NETWORK_TYPE, VoiceStateUpdatedPacket::state,
            ::VoiceStateUpdatedPacket,
        )
    }
}
