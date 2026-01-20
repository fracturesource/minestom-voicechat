package dev.lu15.voicechat.network

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.network.NetworkBuffer

object NetworkTypes {
    fun <E : Enum<E>> byteEnum(enumClass: Class<E>): NetworkBuffer.Type<E> {
        return object : NetworkBuffer.Type<E> {
            override fun write(buffer: NetworkBuffer, value: E) {
                buffer.write(NetworkBuffer.BYTE, value.ordinal.toByte())
            }

            override fun read(buffer: NetworkBuffer): E {
                return enumClass.getEnumConstants()[buffer.read(NetworkBuffer.BYTE).toInt()]
            }
        }
    }

    fun <E : Enum<E>> shortEnum(enumClass: Class<E>): NetworkBuffer.Type<E> {
        return object : NetworkBuffer.Type<E> {
            override fun write(buffer: NetworkBuffer, value: E) {
                buffer.write(NetworkBuffer.SHORT, value.ordinal.toShort())
            }

            override fun read(buffer: NetworkBuffer): E {
                return enumClass.getEnumConstants()[buffer.read(NetworkBuffer.SHORT).toInt()]
            }
        }
    }

    fun <T> intIndexedCollection(backend: NetworkBuffer.Type<T>): NetworkBuffer.Type<Collection<T>> {
        return object : NetworkBuffer.Type<Collection<T>> {
            override fun write(buffer: NetworkBuffer, value: Collection<T>) {
                buffer.write(NetworkBuffer.INT, value.size)
                for (element in value) buffer.write(backend, element)
            }

            override fun read(buffer: NetworkBuffer): Collection<T> {
                val size = buffer.read(NetworkBuffer.INT)!!
                val list = mutableListOf<T>()
                (0..<size).forEach { _ ->
                    list.add(buffer.read(backend))
                }
                return list
            }
        }
    }

    val POSITION: NetworkBuffer.Type<Point> = object : NetworkBuffer.Type<Point> {
        override fun read(buffer: NetworkBuffer): Point {
            return Pos(
                buffer.read(NetworkBuffer.DOUBLE),
                buffer.read(NetworkBuffer.DOUBLE),
                buffer.read(NetworkBuffer.DOUBLE)
            )
        }

        override fun write(buffer: NetworkBuffer, value: Point) {
            buffer.write(NetworkBuffer.DOUBLE, value.x())
            buffer.write(NetworkBuffer.DOUBLE, value.y())
            buffer.write(NetworkBuffer.DOUBLE, value.z())
        }
    }
}
