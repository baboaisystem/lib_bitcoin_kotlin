package com.baboaisystem.litecoinkit

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.baboaisystem.bitcoincore.AbstractKit
import com.baboaisystem.bitcoincore.BitcoinCore
import com.baboaisystem.bitcoincore.BitcoinCore.SyncMode
import com.baboaisystem.bitcoincore.BitcoinCoreBuilder
import com.baboaisystem.bitcoincore.blocks.validators.BitsValidator
import com.baboaisystem.bitcoincore.blocks.validators.BlockValidatorChain
import com.baboaisystem.bitcoincore.blocks.validators.BlockValidatorSet
import com.baboaisystem.bitcoincore.blocks.validators.LegacyTestNetDifficultyValidator
import com.baboaisystem.bitcoincore.core.Bip
import com.baboaisystem.bitcoincore.managers.*
import com.baboaisystem.bitcoincore.network.Network
import com.baboaisystem.bitcoincore.storage.CoreDatabase
import com.baboaisystem.bitcoincore.storage.Storage
import com.baboaisystem.bitcoincore.utils.Base58AddressConverter
import com.baboaisystem.bitcoincore.utils.PaymentAddressParser
import com.baboaisystem.bitcoincore.utils.SegwitAddressConverter
import com.baboai.hdwalletkit.Mnemonic
import com.baboaisystem.litecoinkit.validators.LegacyDifficultyAdjustmentValidator
import com.baboaisystem.litecoinkit.validators.ProofOfWorkValidator

class LitecoinKit : AbstractKit {
    enum class NetworkType {
        MainNet,
        TestNet
    }

    interface Listener : BitcoinCore.Listener

    override var bitcoinCore: BitcoinCore
    override var network: Network

    var listener: Listener? = null
        set(value) {
            field = value
            bitcoinCore.listener = value
        }

    constructor(
            context: Context,
            words: List<String>,
            passphrase: String,
            walletId: String,
            networkType: NetworkType = NetworkType.MainNet,
            peerSize: Int = 10,
            syncMode: SyncMode = SyncMode.Api(),
            confirmationsThreshold: Int = 6,
            bip: Bip = Bip.BIP44
    ) : this(context, Mnemonic().toSeed(words, passphrase), walletId, networkType, peerSize, syncMode, confirmationsThreshold, bip)

    constructor(
            context: Context,
            seed: ByteArray,
            walletId: String,
            networkType: NetworkType = NetworkType.MainNet,
            peerSize: Int = 10,
            syncMode: SyncMode = SyncMode.Api(),
            confirmationsThreshold: Int = 6,
            bip: Bip = Bip.BIP44
    ) {
        val database = CoreDatabase.getInstance(context, getDatabaseName(networkType, walletId, syncMode, bip))
        val storage = Storage(database)
        var initialSyncUrl = ""

        network = when (networkType) {
            NetworkType.MainNet -> {
                initialSyncUrl = "https://ltc.baboaisystem.com/api"
                MainNetLitecoin()
            }
            NetworkType.TestNet -> {
                initialSyncUrl = ""
                TestNetLitecoin()
            }
        }

        val paymentAddressParser = PaymentAddressParser("litecoin", removeScheme = true)
        val initialSyncApi = BCoinApi(initialSyncUrl)

        val blockValidatorSet = BlockValidatorSet()

        val proofOfWorkValidator = ProofOfWorkValidator(ScryptHasher())
        blockValidatorSet.addBlockValidator(proofOfWorkValidator)

        val blockValidatorChain = BlockValidatorChain()

        val blockHelper = BlockValidatorHelper(storage)

        if (networkType == NetworkType.MainNet) {
            blockValidatorChain.add(LegacyDifficultyAdjustmentValidator(blockHelper, heightInterval, targetTimespan, maxTargetBits))
            blockValidatorChain.add(BitsValidator())
        } else if (networkType == NetworkType.TestNet) {
            blockValidatorChain.add(LegacyDifficultyAdjustmentValidator(blockHelper, heightInterval, targetTimespan, maxTargetBits))
            blockValidatorChain.add(LegacyTestNetDifficultyValidator(storage, heightInterval, targetSpacing, maxTargetBits))
            blockValidatorChain.add(BitsValidator())
        }

        blockValidatorSet.addBlockValidator(blockValidatorChain)

        val coreBuilder = BitcoinCoreBuilder()

        bitcoinCore = coreBuilder
                .setContext(context)
                .setSeed(seed)
                .setNetwork(network)
                .setBip(bip)
                .setPaymentAddressParser(paymentAddressParser)
                .setPeerSize(peerSize)
                .setSyncMode(syncMode)
                .setConfirmationThreshold(confirmationsThreshold)
                .setStorage(storage)
                .setInitialSyncApi(initialSyncApi)
                .setBlockValidator(blockValidatorSet)
                .build()

        //  extending bitcoinCore

        val bech32AddressConverter = SegwitAddressConverter(network.addressSegwitHrp)
        val base58AddressConverter = Base58AddressConverter(network.addressVersion, network.addressScriptVersion)

        bitcoinCore.prependAddressConverter(bech32AddressConverter)

        when (bip) {
            Bip.BIP44 -> {
                bitcoinCore.addRestoreKeyConverter(Bip44RestoreKeyConverter(base58AddressConverter))
            }
            Bip.BIP49 -> {
                bitcoinCore.addRestoreKeyConverter(Bip49RestoreKeyConverter(base58AddressConverter))
            }
            Bip.BIP84 -> {
                bitcoinCore.addRestoreKeyConverter(KeyHashRestoreKeyConverter())
            }
        }
    }

    companion object {

        const val maxTargetBits: Long = 0x1e0fffff      // Maximum difficulty
        const val targetSpacing = 150                   // 2.5 minutes per block.
        const val targetTimespan: Long = 302400         // 3.5 days per difficulty cycle, on average.
        const val heightInterval = targetTimespan / targetSpacing // 2016 blocks

        private fun getDatabaseName(networkType: NetworkType, walletId: String, syncMode: SyncMode, bip: Bip): String = "Litecoin-${networkType.name}-$walletId-${syncMode.javaClass.simpleName}-${bip.name}"

        fun clear(context: Context, networkType: NetworkType, walletId: String) {
            for (syncMode in listOf(SyncMode.Api(), SyncMode.Full(), SyncMode.NewWallet())) {
                for (bip in Bip.values())
                    try {
                        SQLiteDatabase.deleteDatabase(context.getDatabasePath(getDatabaseName(networkType, walletId, syncMode, bip)))
                    } catch (ex: Exception) {
                        continue
                    }
            }
        }
    }

}
