package com.baboaisystem.dashkit.managers

import com.baboaisystem.bitcoincore.BitcoinCore
import com.baboaisystem.bitcoincore.blocks.IPeerSyncListener
import com.baboaisystem.bitcoincore.blocks.InitialBlockDownload
import com.baboaisystem.bitcoincore.extensions.toReversedByteArray
import com.baboaisystem.bitcoincore.network.peer.IPeerTaskHandler
import com.baboaisystem.bitcoincore.network.peer.Peer
import com.baboaisystem.bitcoincore.network.peer.PeerGroup
import com.baboaisystem.bitcoincore.network.peer.task.PeerTask
import com.baboaisystem.dashkit.tasks.PeerTaskFactory
import com.baboaisystem.dashkit.tasks.RequestMasternodeListDiffTask
import java.util.concurrent.Executors

class MasternodeListSyncer(
        private val bitcoinCore: BitcoinCore,
        private val peerTaskFactory: PeerTaskFactory,
        private val masternodeListManager: MasternodeListManager,
        private val initialBlockDownload: InitialBlockDownload)
    : IPeerTaskHandler, IPeerSyncListener, PeerGroup.Listener {

    @Volatile
    private var workingPeer: Peer? = null
    private val peersQueue = Executors.newSingleThreadExecutor()

    override fun onPeerSynced(peer: Peer) {
        assignNextSyncPeer()
    }

    override fun onPeerDisconnect(peer: Peer, e: Exception?) {
        if (peer == workingPeer) {
            workingPeer = null

            assignNextSyncPeer()
        }
    }

    private fun assignNextSyncPeer() {
        peersQueue.execute {
            if (workingPeer == null) {
                bitcoinCore.lastBlockInfo?.let { lastBlockInfo ->
                    initialBlockDownload.syncedPeers.firstOrNull()?.let { syncedPeer ->
                        val blockHash = lastBlockInfo.headerHash.toReversedByteArray()
                        val baseBlockHash = masternodeListManager.baseBlockHash

                        if (!blockHash.contentEquals(baseBlockHash)) {
                            val task = peerTaskFactory.createRequestMasternodeListDiffTask(baseBlockHash, blockHash)
                            syncedPeer.addTask(task)

                            workingPeer = syncedPeer
                        }
                    }
                }
            }
        }
    }


    override fun handleCompletedTask(peer: Peer, task: PeerTask): Boolean {
        return when (task) {
            is RequestMasternodeListDiffTask -> {
                task.masternodeListDiffMessage?.let { masternodeListDiffMessage ->
                    masternodeListManager.updateList(masternodeListDiffMessage)
                    workingPeer = null
                }
                true
            }
            else -> false
        }
    }

}
