package dev.lu15.voicechat.network.minecraft.packets.clientbound

import dev.lu15.voicechat.VoiceChat
import dev.lu15.voicechat.network.NetworkTypes.intIndexedCollection
import dev.lu15.voicechat.network.minecraft.Packet
import dev.lu15.voicechat.network.minecraft.VoiceState
import net.kyori.adventure.key.Key
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate

data class VoiceStatesUpdatedPacket(val states: Collection<VoiceState>) : Packet<VoiceStatesUpdatedPacket> {
    override fun id(): Key {
        return IDENTIFIER
    }

    override fun serializer(): NetworkBuffer.Type<VoiceStatesUpdatedPacket> {
        return SERIALIZER
    }

    companion object {
        val IDENTIFIER: Key = Key.key(VoiceChat.NAMESPACE, "states")
        val SERIALIZER: NetworkBuffer.Type<VoiceStatesUpdatedPacket> = NetworkBufferTemplate.template(
            intIndexedCollection(VoiceState.NETWORK_TYPE), VoiceStatesUpdatedPacket::states,
            ::VoiceStatesUpdatedPacket,
        )
    }
}
