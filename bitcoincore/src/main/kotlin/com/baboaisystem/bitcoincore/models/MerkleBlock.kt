package com.baboaisystem.bitcoincore.models

import com.baboaisystem.bitcoincore.core.HashBytes
import com.baboaisystem.bitcoincore.storage.BlockHeader
import com.baboaisystem.bitcoincore.storage.FullTransaction

class MerkleBlock(val header: BlockHeader, val associatedTransactionHashes: Map<HashBytes, Boolean>) {

    var height: Int? = null
    var associatedTransactions = mutableListOf<FullTransaction>()
    val blockHash = header.hash

    val complete: Boolean
        get() = associatedTransactionHashes.size == associatedTransactions.size

}
