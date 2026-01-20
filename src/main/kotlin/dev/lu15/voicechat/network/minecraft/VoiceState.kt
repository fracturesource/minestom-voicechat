package dev.lu15.voicechat.network.minecraft

import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate
import java.util.UUID

data class VoiceState(
    val disabled: Boolean,
    val disconnected: Boolean,
    val uuid: UUID,
    val name: String,
    val group: UUID?
) {
    companion object {
        val NETWORK_TYPE: NetworkBuffer.Type<VoiceState> = NetworkBufferTemplate.template(
            NetworkBuffer.BOOLEAN, VoiceState::disabled,
            NetworkBuffer.BOOLEAN, VoiceState::disconnected,
            NetworkBuffer.UUID, VoiceState::uuid,
            NetworkBuffer.STRING, VoiceState::name,
            NetworkBuffer.UUID.optional(), VoiceState::group,
            ::VoiceState,
        )
    }
}
