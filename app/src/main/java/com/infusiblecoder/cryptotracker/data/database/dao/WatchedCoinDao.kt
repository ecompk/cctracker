package com.infusiblecoder.cryptotracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.infusiblecoder.cryptotracker.data.database.entities.WatchedCoin
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal


@Dao
interface WatchedCoinDao {

    @Query("select * from WatchedCoin where purchaseQuantity > 0 OR watched = :watched order by sortOrder")
    fun getAllWatchedCoins(watched: Boolean = true): Flow<List<WatchedCoin>>

    @Query("select * from WatchedCoin where purchaseQuantity > 0 OR watched = :watched order by sortOrder")
     fun getAllWatchedCoinsOnetime(watched: Boolean = true): List<WatchedCoin> // this method should be removed

    @Query("select * from WatchedCoin where isTrading = :isTrue order by sortOrder")
    fun getAllCoins(isTrue: Boolean = true): Flow<List<WatchedCoin>>

    @Query("select * from WatchedCoin where symbol = :symbol")
     fun getSingleWatchedCoin(symbol: String): List<WatchedCoin>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertCoinListIntoWatchList(list: List<WatchedCoin>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertCoinIntoWatchList(watchedCoin: WatchedCoin)

    @Query("update WatchedCoin set purchaseQuantity = purchaseQuantity + :quantity where symbol=:symbol")
     fun addPurchaseQuantityForCoin(quantity: BigDecimal, symbol: String): Int

    @Query("UPDATE WatchedCoin SET watched = :watched  WHERE coinId = :coinId")
     fun makeCoinWatched(watched: Boolean, coinId: String)
}
