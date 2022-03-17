package com.baboaisystem.bitcoincore.blocks

import com.baboaisystem.bitcoincore.crypto.BloomFilter
import com.baboaisystem.bitcoincore.managers.BloomFilterManager
import com.baboaisystem.bitcoincore.network.peer.Peer
import com.baboaisystem.bitcoincore.network.peer.PeerGroup
import com.baboaisystem.bitcoincore.network.peer.PeerManager

class BloomFilterLoader(private val bloomFilterManager: BloomFilterManager, private val peerManager: PeerManager)
    : PeerGroup.Listener, BloomFilterManager.Listener {

    override fun onPeerConnect(peer: Peer) {
        bloomFilterManager.bloomFilter?.let {
            peer.filterLoad(it)
        }
    }

    override fun onFilterUpdated(bloomFilter: BloomFilter) {
        peerManager.connected().forEach {
            it.filterLoad(bloomFilter)
        }
    }
}
