package com.infusiblecoder.cryptotracker.features.coindetails

import com.infusiblecoder.cryptotracker.data.database.CryptoTrackerDatabase
import com.infusiblecoder.cryptotracker.data.database.entities.WatchedCoin



class CoinDetailsPagerRepository(
    private val cryptoTrackerDatabase: CryptoTrackerDatabase?
) {

    /**
     * Get list of all coins that is added in watch list
     */
    suspend fun loadWatchedCoins(): List<WatchedCoin>? {

        cryptoTrackerDatabase?.let {
            return it.watchedCoinDao().getAllWatchedCoinsOnetime()
        }
        return null
    }
}
