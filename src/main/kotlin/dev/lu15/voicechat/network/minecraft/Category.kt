package dev.lu15.voicechat.network.minecraft

import net.minestom.server.network.NetworkBuffer
import net.minestom.server.network.NetworkBufferTemplate

data class Category(
    val name: String,
    val description: String?,
    val icon: Array<IntArray>,
) {
    companion object {
        private const val ICON_SIZE = 16
        val ICON_SERIALIZER: NetworkBuffer.Type<Array<IntArray>> = object : NetworkBuffer.Type<Array<IntArray>> {
            override fun write(buffer: NetworkBuffer, value: Array<IntArray>) {
                for (i in 0..<ICON_SIZE) {
                    for (j in 0..<ICON_SIZE) {
                        buffer.write(NetworkBuffer.INT, value[i][j])
                    }
                }
            }

            override fun read(buffer: NetworkBuffer): Array<IntArray> {
                val icon = Array(ICON_SIZE) { IntArray(ICON_SIZE) }
                for (i in 0..<ICON_SIZE) {
                    for (j in 0..<ICON_SIZE) {
                        icon[i][j] = buffer.read(NetworkBuffer.INT)!!
                    }
                }
                return icon
            }
        }

        val NETWORK_TYPE: NetworkBuffer.Type<Category> = NetworkBufferTemplate.template(
            NetworkBuffer.STRING, Category::name,
            NetworkBuffer.STRING.optional(), Category::description,
            ICON_SERIALIZER.optional(), Category::icon,
            ::Category,
        )
    }
}
