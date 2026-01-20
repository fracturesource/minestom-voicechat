package dev.lu15.voicechat.network.minecraft

import dev.lu15.voicechat.network.NetworkTypes.shortEnum
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate
import java.util.UUID

data class Group(
    val id: UUID,
    val name: String,
    val passwordProtected: Boolean,
    val persistent: Boolean,
    val hidden: Boolean,
    val type: Type
) {
    enum class Type {
        NORMAL,
        OPEN,
        ISOLATED
    }

    companion object {
        val NETWORK_TYPE: NetworkBuffer.Type<Group> = NetworkBufferTemplate.template(
            NetworkBuffer.UUID, Group::id,
            NetworkBuffer.STRING, Group::name,
            NetworkBuffer.BOOLEAN, Group::passwordProtected,
            NetworkBuffer.BOOLEAN, Group::persistent,
            NetworkBuffer.BOOLEAN, Group::hidden,
            shortEnum(Type::class.java), Group::type,
            ::Group,
        )
    }
}
