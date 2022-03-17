package com.baboaisystem.bitcoincore.managers

import com.baboaisystem.bitcoincore.storage.UnspentOutput

interface IUnspentOutputProvider {
    fun getSpendableUtxo(): List<UnspentOutput>
}
