package com.baboaisystem.bitcoincore.blocks.validators

import com.baboaisystem.bitcoincore.models.Block

interface IBlockValidator {
    fun validate(block: Block, previousBlock: Block)
}

interface IBlockChainedValidator : IBlockValidator {
    fun isBlockValidatable(block: Block, previousBlock: Block): Boolean
}
