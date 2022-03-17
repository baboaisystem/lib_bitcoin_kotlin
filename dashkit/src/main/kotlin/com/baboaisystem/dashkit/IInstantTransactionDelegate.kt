package com.baboaisystem.dashkit

// TODO Rename to listener
interface IInstantTransactionDelegate {
    fun onUpdateInstant(transactionHash: ByteArray)
}
