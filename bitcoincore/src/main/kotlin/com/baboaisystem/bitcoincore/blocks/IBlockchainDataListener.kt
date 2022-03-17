package com.baboaisystem.bitcoincore.blocks

import com.baboaisystem.bitcoincore.models.Block
import com.baboaisystem.bitcoincore.models.Transaction

interface IBlockchainDataListener {
    fun onBlockInsert(block: Block)
    fun onTransactionsUpdate(inserted: List<Transaction>, updated: List<Transaction>, block: Block?)
    fun onTransactionsDelete(hashes: List<String>)
}
