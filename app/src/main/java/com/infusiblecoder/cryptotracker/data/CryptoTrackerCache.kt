package com.infusiblecoder.cryptotracker.data

import com.infusiblecoder.cryptotracker.models.*



object CryptoTrackerCache {

    // cache the news since we don't want to overload the server. 
    var newsMap: MutableMap<String, CryptoPanicNews> = hashMapOf()

    // crypto compare news
    var cyrptoCompareNews: MutableList<CryptoCompareNews> = ArrayList()

    var coinPriceMap: HashMap<String, CoinPrice> = hashMapOf()

    var coinExchangeMap: HashMap<String, MutableList<ExchangePair>> = hashMapOf()

    var topCoinsByTotalVolume: ArrayList<CoinPrice> = ArrayList()

    var topPairsByVolume: ArrayList<CoinPair> = ArrayList()

    var topCoinsByTotalVolume24Hours: ArrayList<CoinPrice> = ArrayList()

    var ticker: MutableMap<String, List<CryptoTicker>> = hashMapOf()

    fun updateCryptoCompareNews(cryptoNews: CryptoCompareNews) {
        cyrptoCompareNews.remove(cryptoNews)
    }
}
