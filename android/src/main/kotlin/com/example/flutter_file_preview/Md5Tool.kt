package com.example.flutter_file_preview

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 */
object Md5Tool {
    private fun bytesToHexString(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in bytes.indices) {
            val hex = Integer.toHexString(0xFF and bytes[i].toInt())
            if (hex.length == 1) {
                sb.append('0')
            }
            sb.append(hex)
        }
        return sb.toString()
    }

    fun hashKey(key: String?): String {
        val hashKey: String
        hashKey = try {
            val mDigest = MessageDigest.getInstance("MD5")
            mDigest.update(key?.toByteArray())
            bytesToHexString(mDigest.digest())
        } catch (e: NoSuchAlgorithmException) {
            key.hashCode().toString()
        }
        return hashKey
    }
}