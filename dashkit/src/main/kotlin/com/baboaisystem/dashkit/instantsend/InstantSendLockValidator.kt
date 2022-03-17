package com.baboaisystem.dashkit.instantsend

import com.baboaisystem.bitcoincore.io.BitcoinOutput
import com.baboaisystem.bitcoincore.utils.HashUtils
import com.baboaisystem.dashkit.DashKitErrors
import com.baboaisystem.dashkit.managers.QuorumListManager
import com.baboaisystem.dashkit.messages.ISLockMessage
import com.baboaisystem.dashkit.models.QuorumType

class InstantSendLockValidator(
        private val quorumListManager: QuorumListManager,
        private val bls: BLS
) {

    @Throws
    fun validate(islock: ISLockMessage) {
        // 01. Select quorum
        val quorum = quorumListManager.getQuorum(QuorumType.LLMQ_50_60, islock.requestId)

        // 02. Make signId data to verify signature
        val signIdPayload = BitcoinOutput()
                .writeByte(quorum.type)
                .write(quorum.quorumHash)
                .write(islock.requestId)
                .write(islock.txHash)
                .toByteArray()

        val signId = HashUtils.doubleSha256(signIdPayload)

        // 03. Verify signature by BLS
        if (!bls.verifySignature(quorum.quorumPublicKey, islock.sign, signId)) {
            throw DashKitErrors.ISLockValidation.SignatureNotValid()
        }
    }
}
