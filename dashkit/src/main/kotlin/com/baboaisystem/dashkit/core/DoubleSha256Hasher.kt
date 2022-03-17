package com.baboaisystem.dashkit.core

import com.baboaisystem.bitcoincore.core.IHasher
import com.baboaisystem.bitcoincore.utils.HashUtils

class SingleSha256Hasher : IHasher {
    override fun hash(data: ByteArray): ByteArray {
        return HashUtils.sha256(data)
    }
}