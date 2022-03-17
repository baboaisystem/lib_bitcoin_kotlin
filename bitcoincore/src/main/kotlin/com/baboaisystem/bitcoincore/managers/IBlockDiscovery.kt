package com.baboaisystem.bitcoincore.managers

import com.baboaisystem.bitcoincore.models.BlockHash
import com.baboaisystem.bitcoincore.models.PublicKey
import io.reactivex.Single

interface IBlockDiscovery {
    fun discoverBlockHashes(account: Int): Single<Pair<List<PublicKey>, List<BlockHash>>>
}
