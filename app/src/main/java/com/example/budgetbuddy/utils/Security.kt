package com.example.budgetbuddy.utils

//import org.bouncycastle.jce.provider.BouncyCastleProvider
//import java.security.SecureRandom
//import java.security.Security
//import javax.crypto.Cipher
//import javax.crypto.KeyGenerator
//import javax.crypto.SecretKey
//import javax.crypto.spec.SecretKeySpec
//
//object EncryptionUtils {
//
//    init {
//        Security.addProvider(BouncyCastleProvider())
//    }
//
//    private const val AES_KEY_SIZE = 256
//    private const val AES_ALGORITHM = "AES"
//    private const val AES_TRANSFORMATION = "AES/ECB/PKCS7Padding"
//
//    // Generar una clave secreta AES
//    fun generateAESKey(): SecretKey {
//        val keyGen = KeyGenerator.getInstance(AES_ALGORITHM)
//        keyGen.init(AES_KEY_SIZE, SecureRandom())
//        return keyGen.generateKey()
//    }
//
//    // Encriptar un string utilizando AES
//    fun encryptString(text: String, key: SecretKey): ByteArray {
//        val cipher = Cipher.getInstance(AES_TRANSFORMATION)
//        cipher.init(Cipher.ENCRYPT_MODE, key)
//        return cipher.doFinal(text.toByteArray(Charsets.UTF_8))
//    }
//
//    // Desencriptar un string utilizando AES
//    fun decryptString(cipherText: ByteArray, key: SecretKey): String {
//        val cipher = Cipher.getInstance(AES_TRANSFORMATION)
//        cipher.init(Cipher.DECRYPT_MODE, key)
//        val decryptedBytes = cipher.doFinal(cipherText)
//        return String(decryptedBytes, Charsets.UTF_8)
//    }
//}
