package com.baboaisystem.bitcoincore.transactions

import com.baboaisystem.bitcoincore.core.IPluginData
import com.baboaisystem.bitcoincore.core.IRecipientSetter
import com.baboaisystem.bitcoincore.managers.PublicKeyManager
import com.baboaisystem.bitcoincore.models.TransactionDataSortType
import com.baboaisystem.bitcoincore.transactions.builder.InputSetter
import com.baboaisystem.bitcoincore.transactions.builder.MutableTransaction
import com.baboaisystem.bitcoincore.transactions.scripts.ScriptType
import com.baboaisystem.bitcoincore.utils.AddressConverterChain

class TransactionFeeCalculator(
        private val recipientSetter: IRecipientSetter,
        private val inputSetter: InputSetter,
        private val addressConverter: AddressConverterChain,
        private val publicKeyManager: PublicKeyManager,
        private val changeScriptType: ScriptType
) {

    fun fee(value: Long, feeRate: Int, senderPay: Boolean, toAddress: String?, pluginData: Map<Byte, IPluginData>): Long {
        val mutableTransaction = MutableTransaction()

        recipientSetter.setRecipient(mutableTransaction, toAddress ?: sampleAddress(), value, pluginData, true)
        inputSetter.setInputs(mutableTransaction, feeRate, senderPay, TransactionDataSortType.None)

        val inputsTotalValue = mutableTransaction.inputsToSign.map { it.previousOutput.value }.sum()
        val outputsTotalValue = mutableTransaction.recipientValue + mutableTransaction.changeValue

        return inputsTotalValue - outputsTotalValue
    }

    private fun sampleAddress(): String {
        return addressConverter.convert(publicKey = publicKeyManager.changePublicKey(), scriptType = changeScriptType).string
    }
}
