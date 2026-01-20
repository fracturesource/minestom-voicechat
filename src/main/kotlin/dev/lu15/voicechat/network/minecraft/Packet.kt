package dev.lu15.voicechat.network.minecraft

import net.kyori.adventure.key.Key
import net.minestom.server.network.NetworkBuffer

interface Packet<T : Packet<T>> {
    fun id(): Key
    fun serializer(): NetworkBuffer.Type<T>
}
