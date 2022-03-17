package com.baboaisystem.dashkit.masternodelist

import com.baboaisystem.bitcoincore.core.IHasher
import com.baboaisystem.bitcoincore.utils.HashUtils
import com.baboaisystem.dashkit.IMerkleHasher

class MerkleRootHasher: IHasher, IMerkleHasher {

    override fun hash(data: ByteArray): ByteArray {
        return HashUtils.doubleSha256(data)
    }

    override fun hash(first: ByteArray, second: ByteArray): ByteArray {
        return HashUtils.doubleSha256(first + second)
    }
}
