package com.baboaisystem.bitcoincore.managers

import com.baboaisystem.bitcoincore.core.IStorage

class ApiSyncStateManager(
        private val storage: IStorage,
        private val restoreFromApi: Boolean
) {

    var restored: Boolean
        get() {
            if (!restoreFromApi) {
                return true
            }

            return storage.initialRestored ?: false
        }
        set(value) {
            storage.setInitialRestored(value)
        }
}
