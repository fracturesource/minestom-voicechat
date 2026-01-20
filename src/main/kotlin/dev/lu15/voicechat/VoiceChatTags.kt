package dev.lu15.voicechat

import dev.lu15.voicechat.network.minecraft.VoiceState
import net.kyori.adventure.key.Key
import net.minestom.server.tag.Tag
import java.net.SocketAddress
import java.util.UUID

object VoiceChatTags {
    val VOICE_CLIENT: Tag<SocketAddress> = create("voice-client", Tag<SocketAddress>::Transient)
    val PLAYER_STATE: Tag<VoiceState> = create("player-state", Tag<VoiceState>::Transient)
    val LAST_KEEP_ALIVE: Tag<Long> = create("last-keep-alive", Tag<Long>::Long)
    val SECRET: Tag<UUID> = create("secret", Tag<UUID>::Transient)

    private fun <T> create(id: String, factory: (id: String) -> Tag<T>): Tag<T> {
        return factory.invoke(Key.key(VoiceChat.NAMESPACE, id).toString())
    }
}
