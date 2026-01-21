package dev.lu15.voicechat

import dev.lu15.voicechat.network.minecraft.Category
import dev.lu15.voicechat.network.minecraft.Packet
import dev.lu15.voicechat.network.voice.VoicePacket
import net.kyori.adventure.key.Key
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.registry.RegistryKey
import org.jetbrains.annotations.Unmodifiable

interface VoiceChat {
    fun <T : Packet<T>> sendPacket(player: Player, packet: T)

    fun <T : VoicePacket<T>> sendPacket(player: Player, packet: T)

    val categories: @Unmodifiable Collection<Category>

    fun addCategory(id: Key, category: Category): RegistryKey<Category>

    fun removeCategory(category: RegistryKey<Category>): Boolean

    interface Builder {
        /**
         * Set the event node to use for voice chat events. This must be registered by yourself.
         * @param eventNode the event node
         * @return this builder
         */
        fun eventNode(eventNode: EventNode<Event>): Builder

        /**
         * Set the public address of the voice server. This is used to tell clients where to connect to.
         * By default, this is blank and clients will use the address they connected to the Minecraft server with.
         * @param publicAddress the public address of the voice server
         * @return this builder
         */
        fun publicAddress(publicAddress: String): Builder

        /**
         * Enable the voice chat server.
         * @return the voice chat server
         */
        fun enable(): VoiceChat
    }

    companion object {
        /**
         * Construct a new voice chat server. The server will start after building.
         * @param address the address to bind to
         * @param port the port to bind to, this can be the same as the Minecraft server port
         * @return a new voice chat server builder
         */
        fun builder(address: String, port: Int): Builder {
            return VoiceChatImpl.BuilderImpl(address, port)
        }

        const val NAMESPACE: String = "voicechat"

        val Player.hasVoiceChat: Boolean get() = hasTag(VoiceChatTags.VOICE_CLIENT)
    }
}
