package dev.lu15.voicechat.api

import net.minestom.server.entity.Player
import net.minestom.server.instance.EntityTracker

interface SoundSelector {
    fun canHear(player: Player): Set<Player>

    fun distance(): Float

    companion object {
        fun distance(distance: Double): SoundSelector {
            return object : SoundSelector {
                override fun canHear(player: Player): Set<Player> {
                    val instance = player.getInstance() ?: return emptySet()

                    val players = mutableSetOf<Player>()
                    instance.entityTracker.nearbyEntities(
                        player.getPosition(),
                        distance,
                        EntityTracker.Target.PLAYERS,
                        players::add
                    )

                    return players
                }

                override fun distance(): Float {
                    return distance.toFloat()
                }
            }
        }
    }
}
