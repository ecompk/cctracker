package com.infusiblecoder.cryptotracker.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize



@Parcelize
data class CryptoPanicNews(
    val count: String = "",
    val results: List<Results>?
) : Parcelable

@Parcelize
data class Results(
    val id: String = "",

    val title: String = "",

    val source: Source = Source(),

    val domain: String = "",

    val created_at: String = "",

    val slug: String = "",

    val url: String = "",

    val published_at: String = ""
) : Parcelable

@Parcelize
data class Source(val title: String = "", val domain: String = "") : Parcelable
