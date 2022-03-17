package com.baboaisystem.bitcoincore.blocks.validators

import com.baboaisystem.bitcoincore.crypto.CompactBits
import com.baboaisystem.bitcoincore.extensions.toReversedHex
import com.baboaisystem.bitcoincore.models.Block
import java.math.BigInteger

class ProofOfWorkValidator : IBlockChainedValidator {

    override fun isBlockValidatable(block: Block, previousBlock: Block): Boolean {
        return true
    }

    override fun validate(block: Block, previousBlock: Block) {
        check(BigInteger(block.headerHash.toReversedHex(), 16) < CompactBits.decode(block.bits)) {
            throw BlockValidatorException.InvalidProofOfWork()
        }
    }
}
