package com.baboaisystem.litecoinkit.validators

import com.baboaisystem.bitcoincore.blocks.validators.BlockValidatorException
import com.baboaisystem.bitcoincore.blocks.validators.IBlockChainedValidator
import com.baboaisystem.bitcoincore.crypto.CompactBits
import com.baboaisystem.bitcoincore.extensions.toHexString
import com.baboaisystem.bitcoincore.io.BitcoinOutput
import com.baboaisystem.bitcoincore.models.Block
import com.baboaisystem.litecoinkit.ScryptHasher
import java.math.BigInteger

class ProofOfWorkValidator(private val scryptHasher: ScryptHasher) : IBlockChainedValidator {

    override fun validate(block: Block, previousBlock: Block) {
        val blockHeaderData = getSerializedBlockHeader(block)

        val powHash = scryptHasher.hash(blockHeaderData).toHexString()

        check(BigInteger(powHash, 16) < CompactBits.decode(block.bits)) {
            throw BlockValidatorException.InvalidProofOfWork()
        }
    }

    private fun getSerializedBlockHeader(block: Block): ByteArray {
        return BitcoinOutput()
                .writeInt(block.version)
                .write(block.previousBlockHash)
                .write(block.merkleRoot)
                .writeUnsignedInt(block.timestamp)
                .writeUnsignedInt(block.bits)
                .writeUnsignedInt(block.nonce)
                .toByteArray()
    }

    override fun isBlockValidatable(block: Block, previousBlock: Block): Boolean {
        return true
    }

}
