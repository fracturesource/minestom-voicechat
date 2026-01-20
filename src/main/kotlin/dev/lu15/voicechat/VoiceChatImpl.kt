@file:Suppress("UnstableApiUsage")

package dev.lu15.voicechat

import dev.lu15.voicechat.config.VoiceChatConfiguration
import dev.lu15.voicechat.event.PlayerHandshakeVoiceChatEvent
import dev.lu15.voicechat.event.PlayerJoinVoiceChatEvent
import dev.lu15.voicechat.event.PlayerUpdateVoiceStateEvent
import dev.lu15.voicechat.network.minecraft.Category
import dev.lu15.voicechat.network.minecraft.MinecraftPacketHandler
import dev.lu15.voicechat.network.minecraft.Packet
import dev.lu15.voicechat.network.minecraft.VoiceState
import dev.lu15.voicechat.network.minecraft.packets.clientbound.CategoryAddedPacket
import dev.lu15.voicechat.network.minecraft.packets.clientbound.CategoryRemovedPacket
import dev.lu15.voicechat.network.minecraft.packets.clientbound.SecretPacket
import dev.lu15.voicechat.network.minecraft.packets.clientbound.VoiceStateUpdatedPacket
import dev.lu15.voicechat.network.minecraft.packets.serverbound.HandshakePacket
import dev.lu15.voicechat.network.minecraft.packets.serverbound.UpdateStatePacket
import dev.lu15.voicechat.network.voice.VoicePacket
import dev.lu15.voicechat.network.voice.VoiceServer
import dev.lu15.voicechat.network.voice.encryption.SecretUtilities
import net.kyori.adventure.key.Key
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventDispatcher
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerPluginMessageEvent
import net.minestom.server.registry.DynamicRegistry
import net.minestom.server.registry.RegistryKey
import net.minestom.server.utils.PacketSendingUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetAddress

internal class VoiceChatImpl private constructor(
    address: InetAddress,
    private val port: Int,
    eventNode: EventNode<Event>,
    private val publicAddress: String
) : VoiceChat {
    private val packetHandler = MinecraftPacketHandler()
    val categoriesRegistry: DynamicRegistry<Category> = DynamicRegistry.create(Key.key(VoiceChat.NAMESPACE, "categories"))

    private val server: VoiceServer

    init {
        // minestom doesn't allow removal of items from registries by default, so
        // we have to enable this feature to allow for the removal of categories
        System.setProperty("minestom.registry.unsafe-ops", "true")

        val voiceServerEventNode = EventNode.all("voice-server")
        eventNode.addChild(voiceServerEventNode)
        this.server = VoiceServer(this, address, port, VoiceChatConfiguration(), voiceServerEventNode)

        this.server.start()
        logger.info("Voice server started on {}:{}", address, port)

        eventNode.addListener(PlayerPluginMessageEvent::class.java) { event ->
            val channel = event.identifier

            if (!Key.parseable(channel)) return@addListener
            val identifier = Key.key(channel)

            if (identifier.namespace() != VoiceChat.NAMESPACE) return@addListener
            try {
                val packet = this.packetHandler.read(channel, event.message)
                val player = event.player
                when (packet) {
                    is HandshakePacket -> this.handle(player, packet)
                    is UpdateStatePacket -> this.handle(player, packet)
                    null -> logger.warn("Received unknown packet from {}: {}", player.username, channel)
                    else -> throw UnsupportedOperationException("Unimplemented packet: $packet")
                }
            } catch (e: Exception) {
                // we ignore this exception because it's most
                // likely to be caused by the client sending
                // an invalid packet.
                logger.debug("failed to read plugin message", e)
            }
        }

        // send existing categories to newly joining players
        eventNode.addListener(PlayerJoinVoiceChatEvent::class.java) { event ->
            categoriesRegistry.values().forEach { category ->
                val key = this.categoriesRegistry.getKey(category)
                checkNotNull(key) { "category not found in registry" }
                sendPacket(event.player, CategoryAddedPacket(key.key(), category))
            }
        }
    }

    private fun handle(player: Player, packet: HandshakePacket) {
        if (packet.version < 18) {
            logger.warn("player {} using wrong version: {}", player.username, packet.version)
            return
        }

        if (SecretUtilities.hasSecret(player)) {
            logger.warn("player {} already has a secret", player.username)
            return
        }

        val config = server.config
        val event = PlayerHandshakeVoiceChatEvent(player, SecretUtilities.generateSecret())
        EventDispatcher.callCancellable(event) {
            val secret = event.secret
            SecretUtilities.setSecret(player, secret)
            player.sendPacket(
                this.packetHandler.write(
                    SecretPacket(
                        secret,
                        port,
                        player.uuid,
                        config.codec,
                        config.mtuSize,
                        config.voiceChatDistance,
                        config.keepAliveCheckPeriodMillis,
                        config.groups,
                        publicAddress,
                        config.allowRecording,
                    )
                )
            )
        }
    }

    private fun handle(player: Player, packet: UpdateStatePacket) {
        // todo: set state when players disconnect from voice chat server - NOT when they disconnect from the minecraft server
        val state = VoiceState(packet.disabled, false, player.uuid, player.username, null)
        player.setTag(VoiceChatTags.PLAYER_STATE, state)
        PacketSendingUtils.broadcastPlayPacket(packetHandler.write(VoiceStateUpdatedPacket(state)))
        EventDispatcher.call(PlayerUpdateVoiceStateEvent(player, state))
    }

    override fun <T : Packet<T>> sendPacket(player: Player, packet: T) {
        player.sendPacket(packetHandler.write(packet))
    }

    override fun <T : VoicePacket<T>> sendPacket(player: Player, packet: T) {
        this.server.write(player, packet)
    }

    override val categories: Collection<Category> get() = categoriesRegistry.values().toSet()

    override fun addCategory(id: Key, category: Category): RegistryKey<Category> {
        val existing = this.categoriesRegistry.get(id)
        val key = this.categoriesRegistry.register(id, category)

        MinecraftServer.getConnectionManager().onlinePlayers.forEach { player ->
            if (!player.hasTag(VoiceChatTags.VOICE_CLIENT)) return@forEach  // only send to voice chat clients

            // remove the existing category if it exists, then add the new one
            if (existing != null) sendPacket(player, CategoryRemovedPacket(id))
            sendPacket(player, CategoryAddedPacket(id, category))
        }

        return key
    }

    override fun removeCategory(category: RegistryKey<Category>): Boolean {
        val removed = this.categoriesRegistry.remove(category.key())
        if (!removed) return false

        MinecraftServer.getConnectionManager().onlinePlayers.forEach { player ->
            if (!player.hasTag(VoiceChatTags.VOICE_CLIENT)) return@forEach  // only send to voice chat clients
            sendPacket(player, CategoryRemovedPacket(category.key()))
        }

        return true
    }

    internal class BuilderImpl(address: String, val port: Int) : VoiceChat.Builder {
        private val address: InetAddress = runCatching {
            InetAddress.getByName(address)
        }.getOrElse { error("Invalid address: $it") }

        private var publicAddress = "" // this causes the client to attempt to connect to the same ip as the minecraft server

        private var eventNode: EventNode<Event>? = null

        override fun eventNode(node: EventNode<Event>): VoiceChat.Builder {
            eventNode = node
            return this
        }

        override fun publicAddress(publicAddress: String): VoiceChat.Builder {
            this.publicAddress = publicAddress
            return this
        }

        override fun enable(): VoiceChat {
            // if the user did not provide an event node, create and register one
            if (eventNode == null) {
                eventNode = EventNode.all("voice-chat")
                MinecraftServer.getGlobalEventHandler().addChild(eventNode)
            }

            return VoiceChatImpl(address, port, eventNode!!, publicAddress)
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(VoiceChatImpl::class.java)
    }
}
