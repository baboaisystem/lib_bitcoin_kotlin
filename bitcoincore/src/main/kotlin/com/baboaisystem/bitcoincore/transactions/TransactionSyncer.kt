package com.baboaisystem.bitcoincore.transactions

import com.baboaisystem.bitcoincore.core.IStorage
import com.baboaisystem.bitcoincore.managers.BloomFilterManager
import com.baboaisystem.bitcoincore.managers.PublicKeyManager
import com.baboaisystem.bitcoincore.storage.FullTransaction

class TransactionSyncer(
        private val storage: IStorage,
        private val transactionProcessor: PendingTransactionProcessor,
        private val invalidator: TransactionInvalidator,
        private val publicKeyManager: PublicKeyManager) {

    fun getNewTransactions(): List<FullTransaction> {
        return storage.getNewTransactions()
    }

    fun handleRelayed(transactions: List<FullTransaction>) {
        if (transactions.isEmpty()) return

        var needToUpdateBloomFilter = false

        try {
            transactionProcessor.processReceived(transactions, false)
        } catch (e: BloomFilterManager.BloomFilterExpired) {
            needToUpdateBloomFilter = true
        }

        if (needToUpdateBloomFilter) {
            publicKeyManager.fillGap()
        }
    }

    fun shouldRequestTransaction(hash: ByteArray): Boolean {
        return !storage.isRelayedTransactionExists(hash)
    }

    fun handleInvalid(fullTransaction: FullTransaction) {
        invalidator.invalidate(fullTransaction.header)
    }
}
