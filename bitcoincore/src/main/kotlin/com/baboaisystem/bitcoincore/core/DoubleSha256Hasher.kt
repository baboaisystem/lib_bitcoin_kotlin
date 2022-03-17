package com.baboaisystem.bitcoincore.core

import com.baboaisystem.bitcoincore.utils.HashUtils

class DoubleSha256Hasher : IHasher {
    override fun hash(data: ByteArray): ByteArray {
        return HashUtils.doubleSha256(data)
    }
}
