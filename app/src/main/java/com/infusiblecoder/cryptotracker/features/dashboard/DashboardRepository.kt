package com.infusiblecoder.cryptotracker.features.dashboard

import com.infusiblecoder.cryptotracker.data.database.CryptoTrackerDatabase
import com.infusiblecoder.cryptotracker.data.database.entities.CoinTransaction
import com.infusiblecoder.cryptotracker.data.database.entities.WatchedCoin
import com.infusiblecoder.cryptotracker.api.api.api
import com.infusiblecoder.cryptotracker.models.CoinPrice
import com.infusiblecoder.cryptotracker.utils.getCoinPricesFromJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged



class DashboardRepository(
    private val cryptoTrackerDatabase: CryptoTrackerDatabase?
) {

    /**
     * Get list of all coins that is added in watch list
     */
    fun loadWatchedCoins(): Flow<List<WatchedCoin>>? {
        cryptoTrackerDatabase?.let {
            return it.watchedCoinDao().getAllWatchedCoins().distinctUntilChanged()
        }
        return null
    }

    /**
     * Get list of all coin transactions
     */
    fun loadTransactions(): Flow<List<CoinTransaction>>? {

        cryptoTrackerDatabase?.let {
            return it.coinTransactionDao().getAllCoinTransaction().distinctUntilChanged()
        }
        return null
    }

    /**
     * Get the price of a coin from the API
     * want data from. [fromCurrencySymbol] specifies what currencies data you want for example bitcoin.
     * [toCurrencySymbol] is which currency you want data in for like USD
     */
    suspend fun getCoinPriceFull(fromCurrencySymbol: String, toCurrencySymbol: String): ArrayList<CoinPrice> {
        return getCoinPricesFromJson(api.getPricesFull(fromCurrencySymbol, toCurrencySymbol))
    }
}
