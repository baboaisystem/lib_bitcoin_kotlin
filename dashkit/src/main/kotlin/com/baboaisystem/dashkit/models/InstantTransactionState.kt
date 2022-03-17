package com.baboaisystem.dashkit.models

class InstantTransactionState {
    var instantTransactionHashes = mutableListOf<ByteArray>()

    fun append(hash: ByteArray) {
        instantTransactionHashes.add(hash)
    }
}
