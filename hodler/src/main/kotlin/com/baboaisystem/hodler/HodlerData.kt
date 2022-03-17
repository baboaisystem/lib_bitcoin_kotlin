package com.baboaisystem.hodler

import com.baboaisystem.bitcoincore.core.IPluginData

data class HodlerData(val lockTimeInterval: LockTimeInterval) : IPluginData
