package com.baboaisystem.bitcoincore.transactions.builder

import com.baboaisystem.bitcoincore.DustCalculator
import com.baboaisystem.bitcoincore.core.ITransactionDataSorterFactory
import com.baboaisystem.bitcoincore.core.PluginManager
import com.baboaisystem.bitcoincore.managers.IUnspentOutputSelector
import com.baboaisystem.bitcoincore.managers.PublicKeyManager
import com.baboaisystem.bitcoincore.models.TransactionDataSortType
import com.baboaisystem.bitcoincore.models.TransactionInput
import com.baboaisystem.bitcoincore.storage.InputToSign
import com.baboaisystem.bitcoincore.storage.UnspentOutput
import com.baboaisystem.bitcoincore.transactions.TransactionSizeCalculator
import com.baboaisystem.bitcoincore.transactions.scripts.ScriptType
import com.baboaisystem.bitcoincore.utils.IAddressConverter

class InputSetter(
        private val unspentOutputSelector: IUnspentOutputSelector,
        private val publicKeyManager: PublicKeyManager,
        private val addressConverter: IAddressConverter,
        private val changeScriptType: ScriptType,
        private val transactionSizeCalculator: TransactionSizeCalculator,
        private val pluginManager: PluginManager,
        private val dustCalculator: DustCalculator,
        private val transactionDataSorterFactory: ITransactionDataSorterFactory
) {
    fun setInputs(mutableTransaction: MutableTransaction, feeRate: Int, senderPay: Boolean, sortType: TransactionDataSortType) {
        val value = mutableTransaction.recipientValue
        val dust = dustCalculator.dust(changeScriptType)
        val unspentOutputInfo = unspentOutputSelector.select(
                value,
                feeRate,
                mutableTransaction.recipientAddress.scriptType,
                changeScriptType,
                senderPay, dust,
                mutableTransaction.getPluginDataOutputSize()
        )

        val sorter = transactionDataSorterFactory.sorter(sortType)
        val unspentOutputs = sorter.sortUnspents(unspentOutputInfo.outputs)

        for (unspentOutput in unspentOutputs) {
            mutableTransaction.addInput(inputToSign(unspentOutput))
        }

        mutableTransaction.recipientValue = unspentOutputInfo.recipientValue

        // Add change output if needed
        unspentOutputInfo.changeValue?.let { changeValue ->
            val changePubKey = publicKeyManager.changePublicKey()
            val changeAddress = addressConverter.convert(changePubKey, changeScriptType)

            mutableTransaction.changeAddress = changeAddress
            mutableTransaction.changeValue = changeValue
        }

        pluginManager.processInputs(mutableTransaction)
    }

    fun setInputs(mutableTransaction: MutableTransaction, unspentOutput: UnspentOutput, feeRate: Int) {
        if (unspentOutput.output.scriptType != ScriptType.P2SH) {
            throw TransactionBuilder.BuilderException.NotSupportedScriptType()
        }

        // Calculate fee
        val transactionSize = transactionSizeCalculator.transactionSize(listOf(unspentOutput.output), listOf(mutableTransaction.recipientAddress.scriptType), 0)
        val fee = transactionSize * feeRate

        if (unspentOutput.output.value < fee) {
            throw TransactionBuilder.BuilderException.FeeMoreThanValue()
        }

        // Add to mutable transaction
        mutableTransaction.addInput(inputToSign(unspentOutput))
        mutableTransaction.recipientValue = unspentOutput.output.value - fee
    }

    private fun inputToSign(unspentOutput: UnspentOutput): InputToSign {
        val previousOutput = unspentOutput.output
        val transactionInput = TransactionInput(previousOutput.transactionHash, previousOutput.index.toLong())

        if (unspentOutput.output.scriptType == ScriptType.P2WPKH) {
            unspentOutput.output.keyHash = unspentOutput.output.keyHash?.drop(2)?.toByteArray()
        }

        return InputToSign(transactionInput, previousOutput, unspentOutput.publicKey)
    }
}
