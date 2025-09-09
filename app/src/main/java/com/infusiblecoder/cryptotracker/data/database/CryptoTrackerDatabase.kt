package com.infusiblecoder.cryptotracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.infusiblecoder.cryptotracker.data.database.dao.CoinTransactionDao
import com.infusiblecoder.cryptotracker.data.database.dao.ExchangeDao
import com.infusiblecoder.cryptotracker.data.database.dao.WatchedCoinDao
import com.infusiblecoder.cryptotracker.data.database.entities.CoinTransaction
import com.infusiblecoder.cryptotracker.data.database.entities.Exchange
import com.infusiblecoder.cryptotracker.data.database.entities.WatchedCoin


@Database(entities = [Exchange::class, WatchedCoin::class, CoinTransaction::class], version = 1, exportSchema = false)
@TypeConverters(BigDecimalConverter::class)
abstract class CryptoTrackerDatabase : RoomDatabase() {

    abstract fun exchangeDao(): ExchangeDao
    abstract fun watchedCoinDao(): WatchedCoinDao
    abstract fun coinTransactionDao(): CoinTransactionDao
}
