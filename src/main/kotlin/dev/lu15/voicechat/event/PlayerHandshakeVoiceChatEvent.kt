package dev.lu15.voicechat.event

import net.minestom.server.entity.Player
import net.minestom.server.event.trait.CancellableEvent
import net.minestom.server.event.trait.PlayerEvent
import java.util.UUID

class PlayerHandshakeVoiceChatEvent(private val _player: Player, var secret: UUID) : PlayerEvent, CancellableEvent {
    private var cancelled = false

    override fun getPlayer(): Player {
        return _player
    }

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }
}
