package com.infusiblecoder.cryptotracker.data.database

import androidx.room.TypeConverter
import java.math.BigDecimal


class BigDecimalConverter {
    @TypeConverter
    fun fromString(value: String?): BigDecimal {
        return if (value == null) BigDecimal.ZERO else BigDecimal(value)
    }

    @TypeConverter
    fun amountToString(bigDecimal: BigDecimal): String {
        return bigDecimal.toPlainString()
    }
}
