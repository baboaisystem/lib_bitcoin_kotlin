package com.baboaisystem.bitcoincore.core

import com.baboaisystem.bitcoincore.models.TransactionDataSortType
import com.baboaisystem.bitcoincore.utils.Bip69Sorter
import com.baboaisystem.bitcoincore.utils.ShuffleSorter
import com.baboaisystem.bitcoincore.utils.StraightSorter

class TransactionDataSorterFactory : ITransactionDataSorterFactory {
    override fun sorter(type: TransactionDataSortType): ITransactionDataSorter {
        return when (type) {
            TransactionDataSortType.None -> StraightSorter()
            TransactionDataSortType.Shuffle -> ShuffleSorter()
            TransactionDataSortType.Bip69 -> Bip69Sorter()
        }
    }
}
