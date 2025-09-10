package com.infusiblecoder.cryptotracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.infusiblecoder.cryptotracker.data.database.entities.CoinTransaction
import kotlinx.coroutines.flow.Flow


@Dao
interface CoinTransactionDao {

    @Query("select * from cointransaction")
    fun getAllCoinTransaction(): Flow<List<CoinTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(coinTransaction: CoinTransaction)

    @Query("SELECT * FROM cointransaction WHERE coinSymbol = :coinSymbol ORDER BY transactionTime ASC")
    fun getTransactionsForCoin(coinSymbol: String): Flow<List<CoinTransaction>>
}
