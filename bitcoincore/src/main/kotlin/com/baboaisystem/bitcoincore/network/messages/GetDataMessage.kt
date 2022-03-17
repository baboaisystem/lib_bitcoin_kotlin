package com.baboaisystem.bitcoincore.network.messages

import com.baboaisystem.bitcoincore.extensions.toReversedHex
import com.baboaisystem.bitcoincore.io.BitcoinInputMarkable
import com.baboaisystem.bitcoincore.io.BitcoinOutput
import com.baboaisystem.bitcoincore.models.InventoryItem

class GetDataMessage(var inventory: List<InventoryItem>) : IMessage {
    override fun toString(): String {
        val invList = inventory.take(10)
                .map { inv -> "${inv.type} :${inv.hash.toReversedHex()}" }
                .toTypedArray()
                .joinToString()

        return "GetDataMessage(${inventory.size}: [$invList])"
    }
}

class GetDataMessageParser : IMessageParser {
    override val command: String = "getdata"

    override fun parseMessage(input: BitcoinInputMarkable): IMessage {
        val count = input.readVarInt() // do not store count
        val inventory = List(count.toInt()) {
            InventoryItem(input)
        }
        return GetDataMessage(inventory)
    }
}

class GetDataMessageSerializer : IMessageSerializer {
    override val command: String = "getdata"

    override fun serialize(message: IMessage): ByteArray? {
        if (message !is GetDataMessage) {
            return null
        }

        val output = BitcoinOutput().writeVarInt(message.inventory.size.toLong())
        message.inventory.forEach {
            output.write(it.toByteArray())
        }

        return output.toByteArray()
    }
}
