package com.baboaisystem.bitcoincore.core

import com.baboaisystem.bitcoincore.models.TransactionInfo
import com.baboaisystem.bitcoincore.storage.FullTransactionInfo

class TransactionInfoConverter : ITransactionInfoConverter {
    override lateinit var baseConverter: BaseTransactionInfoConverter

    override fun transactionInfo(fullTransactionInfo: FullTransactionInfo): TransactionInfo {
        return baseConverter.transactionInfo(fullTransactionInfo)
    }
}
