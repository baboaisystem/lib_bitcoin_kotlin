package com.baboaisystem.bitcoincash.blocks.validators

import com.baboaisystem.bitcoincash.blocks.AsertAlgorithm
import com.baboaisystem.bitcoincore.blocks.validators.BlockValidatorException
import com.baboaisystem.bitcoincore.blocks.validators.IBlockChainedValidator
import com.baboaisystem.bitcoincore.models.Block

class AsertValidator : IBlockChainedValidator {
    private val anchorBlockHeight = 661647
    private val anchorParentBlockTime = 1605447844.toBigInteger() // 2020 November 15, 14:13 GMT
    private val anchorBlockBits = 0x1804dafe

    override fun isBlockValidatable(block: Block, previousBlock: Block): Boolean {
        return previousBlock.height >= anchorBlockHeight
    }

    override fun validate(block: Block, previousBlock: Block) {
        val bits = AsertAlgorithm.computeAsertTarget(anchorBlockBits, anchorParentBlockTime, anchorBlockHeight.toBigInteger(), previousBlock.timestamp.toBigInteger(), previousBlock.height.toBigInteger())
        if (bits != block.bits.toBigInteger()) {
            throw BlockValidatorException.NotEqualBits()
        }
    }
}
