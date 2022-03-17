package com.baboaisystem.dashkit.messages

import com.baboaisystem.bitcoincore.extensions.toReversedHex
import com.baboaisystem.bitcoincore.io.BitcoinInputMarkable
import com.baboaisystem.bitcoincore.network.messages.IMessage
import com.baboaisystem.bitcoincore.network.messages.IMessageParser
import com.baboaisystem.bitcoincore.serializers.TransactionSerializer
import com.baboaisystem.bitcoincore.storage.FullTransaction

class TransactionLockMessage(var transaction: FullTransaction) : IMessage {
    override fun toString(): String {
        return "TransactionLockMessage(${transaction.header.hash.toReversedHex()})"
    }
}

class TransactionLockMessageParser : IMessageParser {
    override val command: String = "ix"

    override fun parseMessage(input: BitcoinInputMarkable): IMessage {
        val transaction = TransactionSerializer.deserialize(input)
        return TransactionLockMessage(transaction)
    }
}
