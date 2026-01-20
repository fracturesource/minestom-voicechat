package dev.lu15.voicechat.network.voice.packets

import dev.lu15.voicechat.network.voice.VoicePacket
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate
import java.util.UUID

data class AuthenticatePacket(
    val player: UUID,
    val secret: UUID
) : VoicePacket<AuthenticatePacket> {
    override fun id(): Int {
        return 0x5
    }

    override fun serializer(): NetworkBuffer.Type<AuthenticatePacket> {
        return SERIALIZER
    }

    companion object {
        val SERIALIZER: NetworkBuffer.Type<AuthenticatePacket> = NetworkBufferTemplate.template(
            NetworkBuffer.UUID, AuthenticatePacket::player,
            NetworkBuffer.UUID, AuthenticatePacket::secret,
            ::AuthenticatePacket,
        )
    }
}
