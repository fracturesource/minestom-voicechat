package dev.lu15.voicechat.network.minecraft.packets

import dev.lu15.voicechat.network.minecraft.Packet
import net.kyori.adventure.key.Key
import net.minestom.server.entity.Player

class BufferPacketHandler {
    private val handlers: MutableMap<Key, Handler<*>> = mutableMapOf()

    fun <T : Packet<T>> register(id: Key, handler: Handler<T>) {
        handlers[id] = handler
    }

    fun handle(channel: Key, packet: Packet<*>, player: Player) {
        handlers[channel]?.handleGeneric(player, packet)
    }

    fun interface Handler<T : Packet<T>> {
        fun handle(player: Player, packet: T)

        @Suppress("UNCHECKED_CAST")
        fun handleGeneric(player: Player, packet: Packet<*>) {
            handle(player, packet as T)
        }
    }
}
