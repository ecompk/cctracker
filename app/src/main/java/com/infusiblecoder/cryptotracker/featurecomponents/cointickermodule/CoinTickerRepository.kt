package com.infusiblecoder.cryptotracker.featurecomponents.cointickermodule

import com.infusiblecoder.cryptotracker.data.CryptoTrackerCache
import com.infusiblecoder.cryptotracker.data.database.CryptoTrackerDatabase
import com.infusiblecoder.cryptotracker.data.database.entities.Exchange
import com.infusiblecoder.cryptotracker.api.api.api
import com.infusiblecoder.cryptotracker.models.CryptoTicker
import com.infusiblecoder.cryptotracker.utils.getCoinTickerFromJson



class CoinTickerRepository(
    private val cryptoTrackerDatabase: CryptoTrackerDatabase?
) {

    /**
     * Get the ticker info from coin gecko
     */
    suspend fun getCryptoTickers(coinName: String): List<CryptoTicker>? {

        return if (CryptoTrackerCache.ticker.containsKey(coinName)) {
            CryptoTrackerCache.ticker[coinName]
        } else {
            val exchangeList = loadExchangeList()
            val coinTickerFromJson = getCoinTickerFromJson(api.getCoinTicker(coinName), exchangeList)
            if (coinTickerFromJson.isNotEmpty()) {
                CryptoTrackerCache.ticker[coinName] = coinTickerFromJson
                coinTickerFromJson
            } else {
                null
            }
        }
    }

    /**
     * Get list of all exchanges, this is needed for logo
     */
    private suspend fun loadExchangeList(): List<Exchange>? {
        cryptoTrackerDatabase?.let {
            return it.exchangeDao().getAllExchanges()
        }
        return null
    }
}
