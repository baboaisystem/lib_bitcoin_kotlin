package com.baboaisystem.dashkit.tasks

import com.baboaisystem.bitcoincore.models.InventoryItem
import com.baboaisystem.bitcoincore.network.messages.GetDataMessage
import com.baboaisystem.bitcoincore.network.messages.IMessage
import com.baboaisystem.bitcoincore.network.peer.task.PeerTask
import com.baboaisystem.bitcoincore.storage.FullTransaction
import com.baboaisystem.dashkit.InventoryType
import com.baboaisystem.dashkit.messages.TransactionLockMessage

class RequestTransactionLockRequestsTask(hashes: List<ByteArray>) : PeerTask() {

    val hashes = hashes.toMutableList()
    var transactions = mutableListOf<FullTransaction>()

    override fun start() {
        val items = hashes.map { hash ->
            InventoryItem(InventoryType.MSG_TXLOCK_REQUEST, hash)
        }

        requester?.send(GetDataMessage(items))
    }

    override fun handleMessage(message: IMessage) = when (message) {
        is TransactionLockMessage -> handleTransactionLockRequest(message.transaction)
        else -> false
    }

    private fun handleTransactionLockRequest(transaction: FullTransaction): Boolean {
        val hash = hashes.firstOrNull { it.contentEquals(transaction.header.hash) } ?: return false

        hashes.remove(hash)
        transactions.add(transaction)

        if (hashes.isEmpty()) {
            listener?.onTaskCompleted(this)
        }

        return true
    }

}
