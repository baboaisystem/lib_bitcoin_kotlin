package com.baboaisystem.dashkit.masternodelist

import com.baboaisystem.bitcoincore.core.HashBytes
import com.baboaisystem.bitcoincore.core.IHasher
import com.baboaisystem.dashkit.models.CoinbaseTransaction
import com.baboaisystem.dashkit.models.CoinbaseTransactionSerializer

class MasternodeCbTxHasher(private val coinbaseTransactionSerializer: CoinbaseTransactionSerializer, private val hasher: IHasher) {

    fun hash(coinbaseTransaction: CoinbaseTransaction): HashBytes {
        val serialized = coinbaseTransactionSerializer.serialize(coinbaseTransaction)

        return HashBytes(hasher.hash(serialized))
    }

}
