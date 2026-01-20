package dev.lu15.voicechat.network.voice.encryption

import dev.lu15.voicechat.VoiceChatTags
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import java.security.SecureRandom
import java.util.Random
import java.util.UUID

object SecretUtilities {
    private val random: Random = SecureRandom()

    fun getSecret(player: UUID): UUID? {
        // todo: this method is O(n), is it worth storing a map of UUIDs to players ourselves?
        val p = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(player)
        return if (p != null) getSecret(p) else null
    }

    fun getSecret(player: Player): UUID? {
        return player.getTag(VoiceChatTags.SECRET)
    }

    fun hasSecret(player: Player): Boolean {
        return player.hasTag(VoiceChatTags.SECRET)
    }

    fun setSecret(player: Player, secret: UUID) {
        player.setTag(VoiceChatTags.SECRET, secret)
    }

    fun generateSecret(): UUID {
        return UUID(random.nextLong(), random.nextLong())
    }
}
