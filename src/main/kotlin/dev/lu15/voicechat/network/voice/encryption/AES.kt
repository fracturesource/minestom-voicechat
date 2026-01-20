package dev.lu15.voicechat.network.voice.encryption

import net.minestom.server.network.NetworkBuffer
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import java.util.Random
import java.util.UUID
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object AES {
    private val RANDOM: Random = SecureRandom()
    private const val CIPHER = "AES/GCM/NoPadding"
    private const val SECRET_LENGTH = 16
    private const val IV_LENGTH = 12
    private const val TAG_LEN_BITS = 128

    fun getBytesFromUuid(uuid: UUID): ByteArray {
        val buffer = NetworkBuffer.staticBuffer(SECRET_LENGTH.toLong())
        buffer.write(NetworkBuffer.UUID, uuid)
        val bytes = ByteArray(SECRET_LENGTH)
        buffer.copyTo(0, bytes, 0, SECRET_LENGTH.toLong())
        return bytes
    }

    private fun generateIv(): ByteArray {
        val iv = ByteArray(IV_LENGTH)
        RANDOM.nextBytes(iv)
        return iv
    }

    private fun createKey(secret: UUID): SecretKeySpec {
        return SecretKeySpec(getBytesFromUuid(secret), "AES")
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun encrypt(secret: UUID, data: ByteArray): ByteArray {
        val iv = generateIv()
        val spec: AlgorithmParameterSpec = GCMParameterSpec(TAG_LEN_BITS, iv)
        val cipher = Cipher.getInstance(CIPHER)
        cipher.init(Cipher.ENCRYPT_MODE, createKey(secret), spec)

        val encrypted = cipher.doFinal(data)
        val result = ByteArray(iv.size + encrypted.size)

        System.arraycopy(iv, 0, result, 0, iv.size)
        System.arraycopy(encrypted, 0, result, iv.size, encrypted.size)
        return result
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun decrypt(secret: UUID, result: ByteArray): ByteArray {
        val iv = ByteArray(IV_LENGTH) // Changed from UUID_LENGTH
        System.arraycopy(result, 0, iv, 0, iv.size)

        val data = ByteArray(result.size - iv.size)
        System.arraycopy(result, iv.size, data, 0, data.size)

        val spec: AlgorithmParameterSpec = GCMParameterSpec(TAG_LEN_BITS, iv)
        val cipher = Cipher.getInstance(CIPHER)
        cipher.init(Cipher.DECRYPT_MODE, createKey(secret), spec)

        return cipher.doFinal(data)
    }
}
