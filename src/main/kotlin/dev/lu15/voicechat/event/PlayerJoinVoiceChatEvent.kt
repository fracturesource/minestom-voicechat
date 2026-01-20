package dev.lu15.voicechat.event

import net.minestom.server.entity.Player
import net.minestom.server.event.trait.PlayerEvent

class PlayerJoinVoiceChatEvent(private val _player: Player) : PlayerEvent {
    override fun getPlayer(): Player {
        return _player
    }
}
