package com.baboaisystem.bitcoincore.network.peer

import com.baboaisystem.bitcoincore.models.InventoryItem

interface IInventoryItemsHandler {
    fun handleInventoryItems(peer: Peer, inventoryItems: List<InventoryItem>)
}

class InventoryItemsHandlerChain : IInventoryItemsHandler {

    private val concreteHandlers = mutableListOf<IInventoryItemsHandler>()

    override fun handleInventoryItems(peer: Peer, inventoryItems: List<InventoryItem>) {
        concreteHandlers.forEach {
            it.handleInventoryItems(peer, inventoryItems)
        }
    }

    fun addHandler(h: IInventoryItemsHandler) {
        concreteHandlers.add(h)
    }

}
