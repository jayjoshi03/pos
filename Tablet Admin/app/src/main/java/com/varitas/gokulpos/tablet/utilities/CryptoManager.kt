package com.varitas.gokulpos.tablet.utilities

import android.os.Build
import android.util.Base64
import java.io.UnsupportedEncodingException
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.PBEParameterSpec

open class CryptoManager(private val PASSWORD: String) {
    companion object {
        protected const val UTF8: String = "utf-8"
        private const val ALGORITHM = "PBEWithMD5AndDES"
        private var secretKey: SecretKey? = null
        private var PBESpec: PBEParameterSpec? = null
    }

    private fun getSecretKey(passphraseOrPin: String): SecretKey? {
        if (secretKey == null) secretKey = generateSecretKey(passphraseOrPin)
        return secretKey
    }

    private val pBESpec: PBEParameterSpec?
        get() {
            if (PBESpec == null) PBESpec = generatePBESpec()
            return PBESpec
        }

    /**
     * @param value string to encrypt
     * @return encrypted string using secret key
     */
    fun encrypt(value: String?): String {
        return try {
            val bytes = value?.toByteArray(charset(UTF8)) ?: ByteArray(0)
            val pbeCipher = Cipher.getInstance(ALGORITHM)
            pbeCipher.init(Cipher.ENCRYPT_MODE, getSecretKey(PASSWORD), pBESpec)
            val encrypted = Base64.encode(pbeCipher.doFinal(bytes), Base64.NO_WRAP)
            String(encrypted, charset(UTF8))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * @param value string to decrypt
     * @return decrypted string using secret key
     */
    fun decrypt(value: String?): String {
        return try {
            val bytes = if (value != null) Base64.decode(value, Base64.DEFAULT) else ByteArray(0)
            val pbeCipher = Cipher.getInstance(ALGORITHM)
            pbeCipher.init(Cipher.DECRYPT_MODE, getSecretKey(PASSWORD), pBESpec)
            String(pbeCipher.doFinal(bytes), charset(UTF8))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun generateSecretKey(passphraseOrPin: String): SecretKey {
        val iterations = 128 // Generate a 64-bit key
        val outputKeyLength = 64
        return try {
            val factory = SecretKeyFactory.getInstance(ALGORITHM)
            val keySpec: KeySpec = PBEKeySpec(passphraseOrPin.toCharArray(), uniquePseudoDeviceID.toByteArray(charset(UTF8)), iterations, outputKeyLength)
            factory.generateSecret(keySpec)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun generatePBESpec(): PBEParameterSpec {
        return try {
            PBEParameterSpec(uniquePseudoDeviceID.toByteArray(charset(UTF8)), 20)
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }
    }

    //we make this look like a valid IMEI for example 355715565309247
    private val uniquePseudoDeviceID: String
        get() = "35" + //we make this look like a valid IMEI for example 355715565309247
                Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.BOOTLOADER.length % 10 + Build.DEVICE.length % 10 + Build.DISPLAY.length % 10 + Build.HOST.length % 10 + Build.ID.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10 + Build.USER.length % 10

}