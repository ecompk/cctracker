package com.infusiblecoder.cryptotracker.api

import com.infusiblecoder.cryptotracker.api.API_KEY
import com.infusiblecoder.cryptotracker.api.APP_NAME
import com.infusiblecoder.cryptotracker.models.CryptoCompareHistoricalResponse
import com.infusiblecoder.cryptotracker.models.CryptoPanicNews
import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface API {

    // Cryptocompare API

    @Headers(API_KEY)
    @GET("all/coinlist")
    suspend fun getCoinList(@Query("extraParams") apiKey: String = APP_NAME): JsonObject

    @Headers(API_KEY)
    @GET("pricemultifull")
    suspend fun getPricesFull(
        @Query("fsyms") fromSymbol: String,
        @Query("tsyms") toSymbol: String,
        @Query("extraParams") apiKey: String = APP_NAME
    ): JsonObject

    @Headers(API_KEY)
    @GET("{period}")
    suspend fun getCryptoHistoricalData(
        @Path("period") period: String,
        @Query("fsym") fromCurrencySymbol: String?,
        @Query("tsym") toCurrencySymbol: String?,
        @Query("limit") limit: Int,
        @Query("aggregate") aggregate: Int,
        @Query("extraParams") apiKey: String = APP_NAME
    ): CryptoCompareHistoricalResponse

    @Headers(API_KEY)
    @GET("price")
    suspend fun getPrice(
        @Query("fsym") fromSymbol: String,
        @Query("tsyms") toSymbol: String,
        @Query("e") exchange: String,
        @Query("extraParams") apiKey: String = APP_NAME
    ): JsonObject

    @Headers(API_KEY)
    @GET("all/exchanges")
    suspend fun getExchangeList(@Query("extraParams") apiKey: String = APP_NAME): JsonObject

    @Headers(API_KEY)
    @GET("exchanges/general")
    suspend fun getExchangesInfo(@Query("extraParams") apiKey: String = APP_NAME): JsonObject

    @Headers(API_KEY)
    @GET("pricehistorical")
    suspend fun getCoinPriceForTimeStamp(
        @Query("fsym") fromSymbol: String,
        @Query("tsyms") toSymbol: String,
        @Query("e") exchange: String,
        @Query("ts") timeStamp: String,
        @Query("extraParams") apiKey: String = APP_NAME
    ): JsonObject

    @Headers(API_KEY)
    @GET("top/totalvolfull")
    suspend fun getTopCoinsByTotalVolume24hours(
        @Query("tsym") toSymbol: String,
        @Query("limit") limit: Int,
        @Query("extraParams") apiKey: String = APP_NAME
    ): JsonObject

    @Headers(API_KEY)
    @GET("top/mktcapfull")
    suspend fun getTopCoinsByTotalVolume(
        @Query("tsym") toSymbol: String,
        @Query("limit") limit: Int,
        @Query("extraParams") apiKey: String = APP_NAME
    ): JsonObject

    @Headers(API_KEY)
    @GET("top/volumes")
    suspend fun getTopPairsVolume(
        @Query("tsym") toSymbol: String,
        @Query("limit") limit: Int,
        @Query("extraParams") apiKey: String = APP_NAME
    ): JsonObject

    @Headers(API_KEY)
    @GET("v2/news/")
    suspend fun getTopNewsArticleFromCryptocompare(
        @Query("lang") lang: String,
        @Query("sortOrder") sortOrder: String,
        @Query("extraParams") apiKey: String = APP_NAME
    ): JsonObject

    // all other API

    @GET("https://api.coingecko.com/api/v3/coins/{coinName}/tickers")
    suspend fun getCoinTicker(
        @Path("coinName") coinName: String
    ): JsonObject

    @GET("https://cryptopanic.com/api/posts/?auth_token=b00f970ff565d986aa8dc3c1a69a88263c342c6e")
    suspend fun getCryptoNewsForCurrency(
        @Query("currencies") coinSymbol: String,
        @Query("filter") filter: String,
        @Query("public") public: Boolean
    ): CryptoPanicNews
}
