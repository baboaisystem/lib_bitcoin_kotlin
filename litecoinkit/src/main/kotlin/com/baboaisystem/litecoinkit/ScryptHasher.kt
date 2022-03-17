package com.baboaisystem.litecoinkit

import com.baboaisystem.bitcoincore.core.IHasher
import com.baboaisystem.bitcoincore.utils.HashUtils

class ScryptHasher : IHasher {

    override fun hash(data: ByteArray): ByteArray {
        return try {
            HashUtils.scrypt(data, data, 1024, 1, 1, 32).reversedArray()
        } catch (e: Exception) {
            byteArrayOf()
        }
    }

}
