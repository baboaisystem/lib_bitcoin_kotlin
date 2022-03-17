package com.baboaisystem.bitcoincore.transactions.builder

import com.baboaisystem.bitcoincore.core.IPluginData
import com.baboaisystem.bitcoincore.core.IRecipientSetter
import com.baboaisystem.bitcoincore.models.TransactionDataSortType
import com.baboaisystem.bitcoincore.storage.FullTransaction
import com.baboaisystem.bitcoincore.storage.UnspentOutput

class TransactionBuilder(
        private val recipientSetter: IRecipientSetter,
        private val outputSetter: OutputSetter,
        private val inputSetter: InputSetter,
        private val signer: TransactionSigner,
        private val lockTimeSetter: LockTimeSetter
) {

    fun buildTransaction(toAddress: String, value: Long, feeRate: Int, senderPay: Boolean, sortType: TransactionDataSortType, pluginData: Map<Byte, IPluginData>): FullTransaction {
        val mutableTransaction = MutableTransaction()

        recipientSetter.setRecipient(mutableTransaction, toAddress, value, pluginData, false)
        inputSetter.setInputs(mutableTransaction, feeRate, senderPay, sortType)
        lockTimeSetter.setLockTime(mutableTransaction)

        outputSetter.setOutputs(mutableTransaction, sortType)
        signer.sign(mutableTransaction)

        return mutableTransaction.build()
    }

    fun buildTransaction(unspentOutput: UnspentOutput, toAddress: String, feeRate: Int, sortType: TransactionDataSortType): FullTransaction {
        val mutableTransaction = MutableTransaction(false)

        recipientSetter.setRecipient(mutableTransaction, toAddress, unspentOutput.output.value, mapOf(), false)
        inputSetter.setInputs(mutableTransaction, unspentOutput, feeRate)
        lockTimeSetter.setLockTime(mutableTransaction)

        outputSetter.setOutputs(mutableTransaction, sortType)
        signer.sign(mutableTransaction)

        return mutableTransaction.build()
    }

    open class BuilderException : Exception() {
        class FeeMoreThanValue : BuilderException()
        class NotSupportedScriptType : BuilderException()
    }
}
