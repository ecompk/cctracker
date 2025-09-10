package com.infusiblecoder.cryptotracker.featurecomponents.cryptonewsmodule

import com.infusiblecoder.cryptotracker.data.CryptoTrackerCache
import com.infusiblecoder.cryptotracker.api.API
import com.infusiblecoder.cryptotracker.api.api.cryptoCompareRetrofit
import com.infusiblecoder.cryptotracker.models.CryptoPanicNews



class CryptoNewsRepository {

    /**
     * Get the top news for specific coin from cryptopanic
     */
    suspend fun getCryptoPanicNews(coinSymbol: String): CryptoPanicNews {

        return if (CryptoTrackerCache.newsMap.containsKey(coinSymbol)) {
            CryptoTrackerCache.newsMap[coinSymbol]!!
        } else {
            cryptoCompareRetrofit.create(API::class.java)
                .getCryptoNewsForCurrency(coinSymbol, "important", true)
        }
    }
}
