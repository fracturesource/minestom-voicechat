package dev.lu15.voicechat.event

import dev.lu15.voicechat.network.minecraft.VoiceState
import net.minestom.server.entity.Player
import net.minestom.server.event.trait.PlayerEvent

class PlayerUpdateVoiceStateEvent(private val _player: Player, val state: VoiceState) : PlayerEvent {
    override fun getPlayer(): Player {
        return _player
    }
}
