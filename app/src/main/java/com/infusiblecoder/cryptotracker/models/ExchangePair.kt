package com.infusiblecoder.cryptotracker.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize



@Parcelize
data class ExchangePair(val exchangeName: String, val pairList: MutableList<String>) : Parcelable
