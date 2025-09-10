package com.infusiblecoder.cryptotracker.features

import com.infusiblecoder.cryptotracker.MyApplication
import com.infusiblecoder.cryptotracker.data.CryptoTrackerCache
import com.infusiblecoder.cryptotracker.data.database.CryptoTrackerDatabase
import com.infusiblecoder.cryptotracker.data.database.entities.CoinTransaction
import com.infusiblecoder.cryptotracker.data.database.entities.Exchange
import com.infusiblecoder.cryptotracker.data.database.entities.WatchedCoin
import com.infusiblecoder.cryptotracker.api.api.api
import com.infusiblecoder.cryptotracker.models.*
import com.infusiblecoder.cryptotracker.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.math.BigDecimal
import java.nio.charset.Charset



class CryptoCompareRepository(
    private val cryptoTrackerDatabase: CryptoTrackerDatabase?
) {


    suspend fun getAllCoinsFromAPI(coinList: ArrayList<CCCoin>? = null, coinInfoMap: Map<String, CoinInfo>? = null): Pair<ArrayList<CCCoin>, Map<String, CoinInfo>> {
        return if (coinList != null) {
            if (coinInfoMap != null) {
                Pair(coinList, coinInfoMap)
            } else {
                Pair(coinList, getCoinInfoMap())
            }
        } else {
            Timber.d("Coin fetched, parsing response")
            val coinsFromJson = getCoinsFromJson(api.getCoinList())
            Pair(coinsFromJson, getCoinInfoMap())
        }
    }

    private fun getCoinInfoMap(): Map<String, CoinInfo> {
        val coinInfoMap = mutableMapOf<String, CoinInfo>()

        val json: String?
        var inputStream: InputStream? = null
        try {
            inputStream = MyApplication.getGlobalAppContext().assets.open("currencyinfo.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.defaultCharset())

            val amountCurrencyType = object : TypeToken<ArrayList<CoinInfoWithCurrency>>() {
            }.type

            val coinInfoWithCurrencyList = Gson().fromJson<ArrayList<CoinInfoWithCurrency>>(json, amountCurrencyType)

            coinInfoWithCurrencyList.forEach {
                coinInfoMap[it.currencyName.toLowerCase()] = it.info
            }

            return coinInfoMap
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }

        return coinInfoMap
    }

    // get only price of the coinSymbol
    suspend fun getCoinPrice(fromCurrencySymbol: String, toCurrencySymbol: String, exchange: String): BigDecimal {
        return getCoinPriceFromJson(api.getPrice(fromCurrencySymbol, toCurrencySymbol, exchange))
    }

    /**
     * Get Price for specific coin to 1 or many other coins or currency.
     * This prices are for specific exchange and for specific time stamp.
     */
    suspend fun getCoinPriceForTimeStamp(fromCoinSymbol: String, toSymbols: String, exchange: String, time: String): MutableMap<String, BigDecimal> {
        return getCoinPriceFromJsonHistorical(api.getCoinPriceForTimeStamp(fromCoinSymbol, toSymbols, exchange, time))
    }

    /**
     * Get Price and other details of Single currency to single currency, we are using the same api for multi.
     */
    suspend fun getCoinPriceFull(fromCurrencySymbol: String, toCurrencySymbol: String): CoinPrice? {
        return if (CryptoTrackerCache.coinPriceMap.containsKey(fromCurrencySymbol)) {
            CryptoTrackerCache.coinPriceMap[fromCurrencySymbol]
        } else {
            val coinPriceList = getCoinPricesFromJson(api.getPricesFull(fromCurrencySymbol, toCurrencySymbol))
            if (coinPriceList.size > 0) {
                CryptoTrackerCache.coinPriceMap[fromCurrencySymbol] = coinPriceList[0]
                coinPriceList[0]
            } else {
                null
            }
        }
    }

    /**
     * Get price of all currencies with respect to a specific currency
     * want data from. [fromCurrencySymbol] specifies what currencies data you want for example btc,eth.[toCurrencySymbol]
     * is which currency you want data in for like USD
     */
    suspend fun getCoinPriceFullList(fromCurrencySymbol: String, toCurrencySymbol: String): ArrayList<CoinPrice> {
        return getCoinPricesFromJson(api.getPricesFull(fromCurrencySymbol, toCurrencySymbol))
    }

    /**
     * Get the top coins pair by total volume for last 24 hours
     */
    suspend fun getTopCoinsByTotalVolume24hours(tsyms: String): List<CoinPrice> {
        return if (CryptoTrackerCache.topCoinsByTotalVolume24Hours.isNotEmpty()) {
            CryptoTrackerCache.topCoinsByTotalVolume24Hours
        } else {
            getCoinPriceListFromJson(api.getTopCoinsByTotalVolume24hours(tsyms, 10)).apply {
                if (size > 0) {
                    CryptoTrackerCache.topCoinsByTotalVolume24Hours = this
                }
            }
        }
    }

    /**
     * Get the top coins pair by total volume
     */
    suspend fun getTopCoinsByTotalVolume(tsyms: String): List<CoinPrice> {
        return if (CryptoTrackerCache.topCoinsByTotalVolume.isNotEmpty()) {
            CryptoTrackerCache.topCoinsByTotalVolume
        } else {
            getCoinPriceListFromJson(api.getTopCoinsByTotalVolume(tsyms, 20)).apply {
                if (size > 0) {
                    CryptoTrackerCache.topCoinsByTotalVolume = this
                }
            }
        }
    }

    /**
     * Get the top coins pair by total volume
     */
    suspend fun getTopPairsByTotalVolume(tsyms: String): List<CoinPair> {
        return if (CryptoTrackerCache.topPairsByVolume.isNotEmpty()) {
            CryptoTrackerCache.topPairsByVolume
        } else {
            getTopPairsFromJson(api.getTopPairsVolume(tsyms, 50)).apply {
                if (size > 0) {
                    CryptoTrackerCache.topPairsByVolume = this
                }
            }
        }
    }

    /**
     * Get the top news article from crypto compare
     */
    suspend fun getTopNewsFromCryptoCompare(): MutableList<CryptoCompareNews> {
        return if (CryptoTrackerCache.cyrptoCompareNews.isNotEmpty()) {
            CryptoTrackerCache.cyrptoCompareNews
        } else {
            getCryptoNewsJson(api.getTopNewsArticleFromCryptocompare("EN", "popular")).apply {
                if (isNotEmpty()) {
                    if (size > 20) {
                        CryptoTrackerCache.cyrptoCompareNews = subList(0, 20)
                    } else {
                        CryptoTrackerCache.cyrptoCompareNews = this
                    }
                }
            }
        }
    }

    /**
     * Get list of all supported exchanges coinSymbol pairs
     */
    suspend fun getAllSupportedExchanges(): HashMap<String, MutableList<ExchangePair>> {

        return if (CryptoTrackerCache.coinExchangeMap.size > 0) {
            CryptoTrackerCache.coinExchangeMap
        } else {
            getExchangeListFromJson(api.getExchangeList()).apply {
                CryptoTrackerCache.coinExchangeMap = this
            }
        }
    }

    /**
     * Get exchange details and save in DB
     */
    suspend fun getExchangeInfo(): List<Exchange> {
        return getExchangeInfoFromJson(api.getExchangesInfo())
    }

    /**
     * --- Database operations --
     */

    /**
     * Get all recent transactions
     */
    fun getRecentTransaction(symbol: String): Flow<List<CoinTransaction>>? {
        return cryptoTrackerDatabase?.coinTransactionDao()?.getTransactionsForCoin(symbol.toUpperCase())
            ?.distinctUntilChanged()
    }

    suspend fun insertCoinsInWatchList(watchedCoinList: List<WatchedCoin>): Unit? {
        return cryptoTrackerDatabase?.watchedCoinDao()?.insertCoinListIntoWatchList(watchedCoinList)
    }

    suspend fun updateCoinWatchedStatus(watched: Boolean, coinID: String): Unit? {
        return cryptoTrackerDatabase?.watchedCoinDao()?.makeCoinWatched(watched, coinID)
    }

    suspend fun insertTransaction(transaction: CoinTransaction): Unit? {
        var quantity = transaction.quantity

        if (transaction.transactionType == TRANSACTION_TYPE_SELL) {
            quantity = quantity.multiply(BigDecimal(-1)) // since this is sell we need to decrease the quantity
        }

        cryptoTrackerDatabase?.watchedCoinDao()?.addPurchaseQuantityForCoin(quantity, transaction.coinSymbol)
        return cryptoTrackerDatabase?.coinTransactionDao()?.insertTransaction(transaction)
    }

    /**
     * Get list of all coins with there watched status
     */
    fun getAllCoins(): Flow<List<WatchedCoin>>? {
        cryptoTrackerDatabase?.let {
            return it.watchedCoinDao().getAllCoins().distinctUntilChanged()
        }
        return null
    }

    /**
     * Get single coin based on coin name and symbol
     */
    suspend fun getSingleCoin(symbol: String): List<WatchedCoin>? {
        cryptoTrackerDatabase?.let {
            return it.watchedCoinDao().getSingleWatchedCoin(symbol)
        }
        return null
    }

    suspend fun insertExchangeIntoList(exchangeList: List<Exchange>): Unit? {
        return cryptoTrackerDatabase?.exchangeDao()?.insertExchanges(exchangeList)
    }
}

fun getTop5CoinsToWatch(): MutableList<String> {
    val watchedCoin: MutableList<String> = mutableListOf()

    val bitcoin = "1182"
    watchedCoin.add(bitcoin)

    val eth = "7605"
    watchedCoin.add(eth)

    val ripple = "5031"
    watchedCoin.add(ripple)

    val eos = "166503"
    watchedCoin.add(eos)

    val litcoin = "3808"
    watchedCoin.add(litcoin)

    return watchedCoin
}
