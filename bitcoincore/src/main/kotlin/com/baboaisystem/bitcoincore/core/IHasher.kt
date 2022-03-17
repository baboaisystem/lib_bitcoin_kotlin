package com.baboaisystem.bitcoincore.core

interface IHasher {
    fun hash(data: ByteArray) : ByteArray
}
