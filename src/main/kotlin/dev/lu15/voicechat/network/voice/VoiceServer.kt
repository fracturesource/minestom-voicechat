package dev.lu15.voicechat.network.voice

import dev.lu15.voicechat.SoundSources
import dev.lu15.voicechat.VoiceChat
import dev.lu15.voicechat.VoiceChatTags
import dev.lu15.voicechat.config.VoiceChatConfiguration
import dev.lu15.voicechat.event.PlayerJoinVoiceChatEvent
import dev.lu15.voicechat.event.PlayerMicrophoneEvent
import dev.lu15.voicechat.network.minecraft.packets.clientbound.VoiceStatesUpdatedPacket
import dev.lu15.voicechat.network.voice.encryption.SecretUtilities.getSecret
import dev.lu15.voicechat.network.voice.packets.AuthenticatePacket
import dev.lu15.voicechat.network.voice.packets.AuthenticationAckPacket
import dev.lu15.voicechat.network.voice.packets.ConnectionCheckAckPacket
import dev.lu15.voicechat.network.voice.packets.ConnectionCheckPacket
import dev.lu15.voicechat.network.voice.packets.KeepAlivePacket
import dev.lu15.voicechat.network.voice.packets.MicPacket
import dev.lu15.voicechat.network.voice.packets.PingPacket
import dev.lu15.voicechat.network.voice.packets.PlayerSoundPacket
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventDispatcher
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.InetAddress
import java.net.SocketAddress
import java.net.SocketException
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

class VoiceServer(
    private val voiceChat: VoiceChat,
    private val address: InetAddress,
    private val port: Int,
    val config: VoiceChatConfiguration,
    eventNode: EventNode<Event>
) {
    private val socket = VoiceSocket()
    private val packetQueue: BlockingQueue<RawPacket> = LinkedBlockingQueue()
    private val connections: MutableMap<SocketAddress, Player> = HashMap()

    private var running = false
    private var lastKeepAlive: Long = 0

    init {
        eventNode.addListener(PlayerDisconnectEvent::class.java) { event ->
            val player = event.player
            player.getTag(VoiceChatTags.VOICE_CLIENT)?.also(connections::remove)
        }
    }

    fun start() {
        running = true
        Thread.ofVirtual().name("voice-server-entrypoint").start(::entrypoint)
        Thread.ofVirtual().name("voice-processor").start(::processor)
    }

    fun stop() {
        running = false
    }

    private fun entrypoint() {
        try {
            socket.open(address, port)

            while (!socket.closed() || !running) {
                try {
                    val packet = socket.read()
                    packetQueue.put(packet)
                } catch (e: IOException) {
                    // we ignore this exception because it's most
                    // likely to be caused by the client sending
                    // an invalid packet.
                    logger.debug("Failed to read raw packet", e)
                } catch (e: InterruptedException) {
                    // wait interrupted, ignore
                    logger.debug("Interrupted while waiting for packet queue", e)
                }
            }

            logger.debug("Voice server closed")
        } catch (e: SocketException) {
            logger.error("Failed to open voice socket", e)
        } finally {
            running = false
        }
    }

    private fun processor() {
        while (running) {
            try {
                val keepAliveTime = System.currentTimeMillis()
                if (keepAliveTime - lastKeepAlive > config.keepAliveCheckPeriodMillis) {
                    checkKeepAlives()
                    lastKeepAlive = keepAliveTime
                }

                val rawPacket = packetQueue.poll(10, TimeUnit.MILLISECONDS) ?: continue
                val packet = VoicePacketHandler.read(rawPacket) ?: continue

                if (System.currentTimeMillis() - rawPacket.timestamp > packet.timeToLive()) {
                    logger.error("Dropping expired voice packet: {}", packet)
                    continue
                }

                if (packet is AuthenticatePacket) {
                    val player = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(packet.player)
                    if (player == null) {
                        logger.warn("Received authentication packet from unknown player: {}", packet.player)
                        continue
                    }
                    handleAuthenticate(player, packet, rawPacket.address)
                    continue
                }

                val address = rawPacket.address
                val player = connections[address]
                if (player == null) {
                    logger.warn("Received voice packet from unknown address: {}", address)
                    continue
                }

                when (packet) {
                    is ConnectionCheckPacket -> handleConnectionCheck(player)
                    is MicPacket -> handleMic(player, packet)
                    is KeepAlivePacket -> handleKeepAlive(player)
                    is PingPacket -> handlePing(player, packet)
                    else -> throw IllegalStateException("Unexpected packet: $packet")
                }
            } catch (_: InterruptedException) {
                // wait interrupted, ignore
            } catch (e: Exception) {
                // we ignore this exception because it's most
                // likely to be caused by the client sending
                // an invalid packet.
                logger.debug("Failed to read voice packet", e)
            }
        }
    }

    fun <T : VoicePacket<T>> write(player: Player, packet: T) {
        runCatching {
            write0(player, packet)
        }.onFailure { e -> logger.debug("Failed to write voice packet", e) }
    }

    @Throws(IOException::class)
    private fun <T : VoicePacket<T>> write0(player: Player, packet: T) {
        val address = player.getTag(VoiceChatTags.VOICE_CLIENT) ?: return
        socket.write(VoicePacketHandler.write(player, packet), address)
    }

    private fun checkKeepAlives() {
        val time = System.currentTimeMillis()
        connections.toMap().forEach { (address, player) ->
            if (time - player.getTag(VoiceChatTags.LAST_KEEP_ALIVE) > config.keepAliveTimeoutMillis) {
                // todo: will the client be trying to reconnect?
                logger.warn("Player {} did not send keepalive packet", player.username)
                connections.remove(address)
            } else write(player, KeepAlivePacket())
        }
    }

    private fun handleAuthenticate(player: Player, packet: AuthenticatePacket, address: SocketAddress) {
        if (connections.containsKey(address)) {
            logger.warn("Received duplicate authentication packet from {}", address)
            return
        }

        if (packet.secret != getSecret(player)) {
            logger.warn("Received invalid secret from {}", player.username)
            player.kick(Component.text("Simple Voice Chat | Received incorrect secret, please rejoin."))
            return
        }

        player.setTag<Long>(VoiceChatTags.LAST_KEEP_ALIVE, System.currentTimeMillis())
        player.setTag<SocketAddress>(VoiceChatTags.VOICE_CLIENT, address)
        connections[address] = player
        write(player, AuthenticationAckPacket())
    }

    private fun handleConnectionCheck(player: Player) {
        write(player, KeepAlivePacket())
        write(player, ConnectionCheckAckPacket())

        val states = connections.values.mapNotNull { it.getTag(VoiceChatTags.PLAYER_STATE) }
        voiceChat.sendPacket(player, VoiceStatesUpdatedPacket(states))

        // this is the packet that is sent when the client is ready to receive voice packets
        // this means they are successfully connected to the voice server
        logger.debug("Player {} connected to voice chat", player.username)

        EventDispatcher.call(PlayerJoinVoiceChatEvent(player))
    }

    private fun handleMic(player: Player, packet: MicPacket) {
        // todo: implement groups?

        val event = PlayerMicrophoneEvent(player, packet.data)
        EventDispatcher.callCancellable(event) {
            val soundPacket = PlayerSoundPacket(
                player.uuid, // the channel is the sender's UUID
                player.uuid,
                event.audio,
                packet.sequenceNumber,
                event.soundSelector.distance(),
                packet.whispering,
                SoundSources.PROXIMITY,
            )

            val hearable = event.soundSelector.canHear(player).toMutableSet() // TODO wtf is sound selector??
            hearable.remove(player)
            hearable.removeIf { player -> player.getTag(VoiceChatTags.PLAYER_STATE)?.disabled == true }
            hearable.forEach { player -> write(player, soundPacket) }
        }
    }

    private fun handleKeepAlive(player: Player) {
        player.setTag(VoiceChatTags.LAST_KEEP_ALIVE, System.currentTimeMillis())
    }

    private fun handlePing(player: Player, packet: PingPacket) {
        logger.debug("Received ping packet: {}", packet)
        voiceChat.sendPacket(player, packet)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(VoiceServer::class.java)
    }
}
