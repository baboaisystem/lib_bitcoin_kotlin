package com.baboaisystem.bitcoincore.network.messages

import com.baboaisystem.bitcoincore.io.BitcoinInputMarkable
import com.baboaisystem.bitcoincore.models.NetworkAddress

class AddrMessage(var addresses: List<NetworkAddress>) : IMessage {
    override fun toString(): String {
        return "AddrMessage(count=${addresses.size})"
    }
}

class AddrMessageParser : IMessageParser {
    override val command = "addr"

    override fun parseMessage(input: BitcoinInputMarkable): IMessage {
        val count = input.readVarInt() // do not store count

        val addresses = List(count.toInt()) {
            NetworkAddress(input, false)
        }

        return AddrMessage(addresses)
    }
}
