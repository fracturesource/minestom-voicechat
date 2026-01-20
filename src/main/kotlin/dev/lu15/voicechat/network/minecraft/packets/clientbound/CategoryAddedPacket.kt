package dev.lu15.voicechat.network.minecraft.packets.clientbound

import dev.lu15.voicechat.VoiceChat
import dev.lu15.voicechat.network.minecraft.Category
import dev.lu15.voicechat.network.minecraft.Packet
import net.kyori.adventure.key.Key
import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate
import java.util.regex.Pattern

data class CategoryAddedPacket(val identifier: String, val category: Category) : Packet<CategoryAddedPacket> {
    constructor(identifier: Key, category: Category) : this(identifier.toString().replace(':', '_'), category)

    override fun id(): Key {
        return IDENTIFIER
    }

    override fun serializer(): NetworkBuffer.Type<CategoryAddedPacket> {
        return SERIALIZER
    }

    init {
        require(IDENTIFIER_PATTERN.matcher(identifier).matches()) { "category id does not match pattern " + IDENTIFIER_PATTERN.pattern() }
        require(category.name.length <= 16) { "category name is too long, found " + category.name.length + " characters, maximum is 16" }
        require(!(category.description != null && category.description.length > 32767)) { "category description is too long, found " + category.description!!.length + " characters, maximum is 32767" }

        val icon = category.icon
        require(icon.size == 16) { "category icon is not 16x16, found " + icon.size + "x" + icon.size }
        icon.forEach { row ->
            require(row.size == 16) { "category icon is not 16x16, found " + row.size + "x16" }
        }
    }

    companion object {
        val IDENTIFIER: Key = Key.key(VoiceChat.NAMESPACE, "add_category")
        val SERIALIZER: NetworkBuffer.Type<CategoryAddedPacket> = NetworkBufferTemplate.template(
            NetworkBuffer.STRING, CategoryAddedPacket::identifier,
            Category.NETWORK_TYPE, CategoryAddedPacket::category,
            ::CategoryAddedPacket,
        )

        val IDENTIFIER_PATTERN: Pattern = Pattern.compile("^[a-z_]{1,16}$")
    }
}
