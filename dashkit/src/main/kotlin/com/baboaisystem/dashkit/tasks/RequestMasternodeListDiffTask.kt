package com.baboaisystem.dashkit.tasks

import com.baboaisystem.bitcoincore.network.messages.IMessage
import com.baboaisystem.bitcoincore.network.peer.task.PeerTask
import com.baboaisystem.dashkit.messages.GetMasternodeListDiffMessage
import com.baboaisystem.dashkit.messages.MasternodeListDiffMessage
import java.util.concurrent.TimeUnit

class RequestMasternodeListDiffTask(private val baseBlockHash: ByteArray, private val blockHash: ByteArray) : PeerTask() {

    var masternodeListDiffMessage: MasternodeListDiffMessage? = null

    init {
        allowedIdleTime = TimeUnit.SECONDS.toMillis(5)
    }

    override fun handleTimeout() {
        listener?.onTaskFailed(this, Exception("RequestMasternodeListDiffTask Timeout"))
    }


    override fun start() {
        requester?.send(GetMasternodeListDiffMessage(baseBlockHash, blockHash))
        resetTimer()
    }

    override fun handleMessage(message: IMessage): Boolean {
        if (message is MasternodeListDiffMessage
                && message.baseBlockHash.contentEquals(baseBlockHash)
                && message.blockHash.contentEquals(blockHash)) {

            masternodeListDiffMessage = message

            listener?.onTaskCompleted(this)

            return true
        }

        return false
    }
}
