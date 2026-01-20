package dev.lu15.voicechat.network.minecraft.packets.clientbound

import dev.lu15.voicechat.VoiceChat
import dev.lu15.voicechat.network.minecraft.Packet
import net.kyori.adventure.key.Key
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate

data class CategoryRemovedPacket(val category: String) : Packet<CategoryRemovedPacket> {
    constructor(category: Key) : this(category.toString().replace(':', '_'))

    override fun id(): Key {
        return IDENTIFIER
    }

    override fun serializer(): NetworkBuffer.Type<CategoryRemovedPacket> {
        return SERIALIZER
    }

    init {
        require(category.length <= 16) { "Category id is too long, found " + category.length + " characters, maximum is 16" }
    }

    companion object {
        val IDENTIFIER: Key = Key.key(VoiceChat.NAMESPACE, "remove_category")
        val SERIALIZER: NetworkBuffer.Type<CategoryRemovedPacket> = NetworkBufferTemplate.template(
            NetworkBuffer.STRING, CategoryRemovedPacket::category,
            ::CategoryRemovedPacket,
        )
    }
}
