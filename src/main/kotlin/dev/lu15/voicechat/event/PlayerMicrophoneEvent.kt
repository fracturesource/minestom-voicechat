package dev.lu15.voicechat.event

import net.minestom.server.entity.Player
import net.minestom.server.event.trait.CancellableEvent
import net.minestom.server.event.trait.PlayerInstanceEvent

/**
 * Called whenever microphone data is sent by a player. This event is called a lot,
 * so it is recommended to keep listeners as lightweight as possible.
 */
class PlayerMicrophoneEvent(private val _player: Player, val receiver: Player, var audio: ByteArray) : PlayerInstanceEvent, CancellableEvent {
    private var cancelled = false
    var distance: Float = 48.0f

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
