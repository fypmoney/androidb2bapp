package com.fypmoney.view.kycagent.util

import android.os.Build

import java.util.Base64

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object AES {
    private var salt = byteArrayOf(83, 71, 26, 58, 54, 35, 22, 11, 83, 71, 26, 58, 54, 35, 22, 11)
    var config = AppConfig()

    fun encrypt(data: String): String? {
        try {
            val passwordchars = config.encryptKey.toCharArray()
            val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val pbeKeySpec = PBEKeySpec(passwordchars, salt, 1000, 256 + 128)
            val secretKey: SecretKey = factory.generateSecret(pbeKeySpec)
            val key = ByteArray(32)
            val iv = ByteArray(16)
            System.arraycopy(secretKey.encoded, 0, iv, 0, 16)
            System.arraycopy(secretKey.encoded, 16, key, 0, 32)
            val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            val secretKeySpec = SecretKeySpec(key, "AES")
            val ivps = IvParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivps)
            val encrypted: ByteArray = cipher.doFinal(data.toByteArray(charset("UTF-8")))
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Base64.getEncoder().encodeToString(encrypted)
            } else {
                android.util.Base64.encode(data.toByteArray(), android.util.Base64.DEFAULT).toString()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    fun decrypt(data: String): String? {
        try {
            val passwordchars = config.encryptKey.toCharArray()
            val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val pbeKeySpec = PBEKeySpec(passwordchars, salt, 1000, 256 + 128)
            val secretKey: SecretKey = factory.generateSecret(pbeKeySpec)
            val key = ByteArray(32)
            val iv = ByteArray(16)
            System.arraycopy(secretKey.encoded, 0, iv, 0, 16)
            System.arraycopy(secretKey.encoded, 16, key, 0, 32)
            val secretKeySpec = SecretKeySpec(key, "AES")
            val ivps = IvParameterSpec(iv)
            val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivps)
            val original: ByteArray =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    cipher.doFinal(Base64.getDecoder().decode(data.toByteArray(charset("UTF-8"))))
                } else {
                    cipher.doFinal(android.util.Base64.decode(data.toByteArray(), android.util.Base64.DEFAULT))
                }
            return String(original)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }
}