package dev.lu15.voicechat.event

import dev.lu15.voicechat.api.SoundSelector
import dev.lu15.voicechat.api.SoundSelector.Companion.distance
import net.minestom.server.entity.Player
import net.minestom.server.event.trait.CancellableEvent
import net.minestom.server.event.trait.PlayerInstanceEvent

/**
 * Called whenever microphone data is sent by a player. This event is called a lot,
 * so it is recommended to keep listeners as lightweight as possible.
 */
class PlayerMicrophoneEvent(private val _player: Player, var audio: ByteArray) : PlayerInstanceEvent, CancellableEvent {
    var soundSelector: SoundSelector = distance(48.0)

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
