package com.baboaisystem.bitcoincore.transactions.builder

import com.baboaisystem.bitcoincore.models.Transaction
import com.baboaisystem.bitcoincore.models.TransactionOutput
import com.baboaisystem.bitcoincore.network.Network
import com.baboaisystem.bitcoincore.serializers.TransactionSerializer
import com.baboaisystem.bitcoincore.storage.InputToSign
import com.baboaisystem.bitcoincore.transactions.scripts.ScriptType
import com.baboaisystem.hdwalletkit.HDWallet

class InputSigner(private val hdWallet: HDWallet, val network: Network) {

    fun sigScriptData(transaction: Transaction, inputsToSign: List<InputToSign>, outputs: List<TransactionOutput>, index: Int): List<ByteArray> {

        val input = inputsToSign[index]
        val prevOutput = input.previousOutput
        val publicKey = input.previousOutputPublicKey

        val privateKey = checkNotNull(hdWallet.privateKey(publicKey.account, publicKey.index, publicKey.external)) {
            throw Error.NoPrivateKey()
        }

        val txContent = TransactionSerializer.serializeForSignature(transaction, inputsToSign, outputs, index, prevOutput.scriptType.isWitness || network.sigHashForked) + byteArrayOf(network.sigHashValue, 0, 0, 0)
        val signature = privateKey.createSignature(txContent) + network.sigHashValue

        return when (prevOutput.scriptType) {
            ScriptType.P2PK -> listOf(signature)
            else -> listOf(signature, publicKey.publicKey)
        }
    }

    open class Error : Exception() {
        class NoPrivateKey : Error()
        class NoPreviousOutput : Error()
        class NoPreviousOutputAddress : Error()
    }
}
