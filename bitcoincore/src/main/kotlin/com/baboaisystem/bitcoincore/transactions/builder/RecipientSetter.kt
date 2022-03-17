package com.baboaisystem.bitcoincore.transactions.builder

import com.baboaisystem.bitcoincore.core.IPluginData
import com.baboaisystem.bitcoincore.core.IRecipientSetter
import com.baboaisystem.bitcoincore.core.PluginManager
import com.baboaisystem.bitcoincore.transactions.builder.MutableTransaction
import com.baboaisystem.bitcoincore.utils.IAddressConverter

class RecipientSetter(
        private val addressConverter: IAddressConverter,
        private val pluginManager: PluginManager
) : IRecipientSetter {

    override fun setRecipient(mutableTransaction: MutableTransaction, toAddress: String, value: Long, pluginData: Map<Byte, IPluginData>, skipChecking: Boolean) {
        mutableTransaction.recipientAddress = addressConverter.convert(toAddress)
        mutableTransaction.recipientValue = value

        pluginManager.processOutputs(mutableTransaction, pluginData, skipChecking)
    }

}
