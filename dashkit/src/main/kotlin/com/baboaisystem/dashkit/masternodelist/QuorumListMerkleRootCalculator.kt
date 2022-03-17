package com.baboaisystem.dashkit.masternodelist

import com.baboaisystem.dashkit.models.Quorum

class QuorumListMerkleRootCalculator(private val merkleRootCreator: MerkleRootCreator) {

    fun calculateMerkleRoot(sortedQuorums: List<Quorum>): ByteArray? {
        return merkleRootCreator.create(sortedQuorums.map { it.hash })
    }

}
