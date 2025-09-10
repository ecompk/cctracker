package com.infusiblecoder.cryptotracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.infusiblecoder.cryptotracker.data.database.entities.Exchange


@Dao
interface ExchangeDao {

    @Query("select * from exchange")
    fun getAllExchanges(): List<Exchange>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExchanges(list: List<Exchange>)
}
