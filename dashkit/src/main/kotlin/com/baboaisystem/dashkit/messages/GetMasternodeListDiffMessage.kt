package com.baboaisystem.dashkit.messages

import com.baboaisystem.bitcoincore.io.BitcoinOutput
import com.baboaisystem.bitcoincore.network.messages.IMessage
import com.baboaisystem.bitcoincore.network.messages.IMessageSerializer

class GetMasternodeListDiffMessage(val baseBlockHash: ByteArray, val blockHash: ByteArray) : IMessage

class GetMasternodeListDiffMessageSerializer : IMessageSerializer {
    override val command: String = "getmnlistd"

    override fun serialize(message: IMessage): ByteArray? {
        if (message !is GetMasternodeListDiffMessage) {
            return null
        }

        return BitcoinOutput()
                .write(message.baseBlockHash)
                .write(message.blockHash)
                .toByteArray()
    }
}
