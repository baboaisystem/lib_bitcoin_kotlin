package com.baboaisystem.bitcoincore.transactions.builder

import com.baboaisystem.bitcoincore.core.IStorage

class LockTimeSetter(private val storage: IStorage) {

    fun setLockTime(transaction: MutableTransaction) {
        transaction.transaction.lockTime = storage.lastBlock()?.height?.toLong() ?: 0
    }

}
