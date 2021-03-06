package com.baboaisystem.bitcoincore.core

import com.baboaisystem.bitcoincore.transactions.scripts.ScriptType

import com.baboaisystem.hdwalletkit.HDWallet

enum class Bip {
    BIP44,
    BIP49,
    BIP84;

    val scriptType: ScriptType
        get() = when (this) {
            BIP44 -> ScriptType.P2PKH
            BIP49 -> ScriptType.P2WPKHSH
            BIP84 -> ScriptType.P2WPKH
        }

    val purpose: HDWallet.Purpose
        get() = when (this) {
            BIP44 -> HDWallet.Purpose.BIP44
            BIP49 -> HDWallet.Purpose.BIP49
            BIP84 -> HDWallet.Purpose.BIP84
        }

}
