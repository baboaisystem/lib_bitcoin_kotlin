package com.baboaisystem.bitcoincore.storage

import androidx.room.Embedded
import com.baboaisystem.bitcoincore.extensions.toHexString
import com.baboaisystem.bitcoincore.models.*
import com.baboaisystem.bitcoincore.serializers.TransactionSerializer
import com.baboaisystem.bitcoincore.utils.HashUtils

class BlockHeader(
        val version: Int,
        val previousBlockHeaderHash: ByteArray,
        val merkleRoot: ByteArray,
        val timestamp: Long,
        val bits: Long,
        val nonce: Long,
        val hash: ByteArray)

class FullTransaction(val header: Transaction, val inputs: List<TransactionInput>, val outputs: List<TransactionOutput>) {

    val metadata: TransactionMetadata

    init {
        if (header.hash.isEmpty()) {
            header.hash = HashUtils.doubleSha256(TransactionSerializer.serialize(this, withWitness = false))
        }

        metadata = TransactionMetadata(header.hash)

        inputs.forEach {
            it.transactionHash = header.hash
        }
        outputs.forEach {
            it.transactionHash = header.hash
        }
    }

}

class InputToSign(
        val input: TransactionInput,
        val previousOutput: TransactionOutput,
        val previousOutputPublicKey: PublicKey)

class TransactionWithBlock(
        @Embedded val transaction: Transaction,
        @Embedded val block: Block?)

class PublicKeyWithUsedState(
        @Embedded val publicKey: PublicKey,
        val usedCount: Int) {

    val used: Boolean
        get() = usedCount > 0
}

class PreviousOutput(
        val publicKeyPath: String?,
        val value: Long,
        val index: Int,

        // PreviousOutput is intended to be used with TransactionInput.
        // Here we use outputTransactionHash, since the TransactionInput has field `transactionHash` field
        val outputTransactionHash: ByteArray
)

class InputWithPreviousOutput(
        @Embedded val input: TransactionInput,
        @Embedded val previousOutput: PreviousOutput?)

class UnspentOutput(
        @Embedded val output: TransactionOutput,
        @Embedded val publicKey: PublicKey,
        @Embedded val transaction: Transaction,
        @Embedded val block: Block?)

class FullTransactionInfo(
        val block: Block?,
        val header: Transaction,
        val inputs: List<InputWithPreviousOutput>,
        val outputs: List<TransactionOutput>,
        val metadata: TransactionMetadata
) {

    val rawTransaction: String
        get() {
            val fullTransaction = FullTransaction(header, inputs.map { it.input }, outputs)
            return TransactionSerializer.serialize(fullTransaction).toHexString()
        }
}

