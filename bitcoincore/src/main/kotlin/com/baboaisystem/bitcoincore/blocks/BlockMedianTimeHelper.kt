package com.baboaisystem.bitcoincore.blocks

import com.baboaisystem.bitcoincore.core.IStorage
import com.baboaisystem.bitcoincore.models.Block

class BlockMedianTimeHelper(private val storage: IStorage) {
    private val medianTimeSpan = 11

    val medianTimePast: Long?
        get() = storage.lastBlock()?.let { medianTimePast(it) }

    fun medianTimePast(block: Block): Long? {
        val startIndex = block.height - medianTimeSpan + 1
        val median = storage.timestamps(from = startIndex, to = block.height)

        return when {
            block.height >= medianTimeSpan && median.size < medianTimeSpan -> null
            else -> median[median.size / 2]
        }
    }

}
