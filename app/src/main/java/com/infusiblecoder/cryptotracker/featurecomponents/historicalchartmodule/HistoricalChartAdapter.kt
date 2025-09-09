package com.infusiblecoder.cryptotracker.featurecomponents.historicalchartmodule

import com.infusiblecoder.cryptotracker.models.CryptoCompareHistoricalResponse
import com.robinhood.spark.SparkAdapter



class HistoricalChartAdapter(private val historicalData: List<CryptoCompareHistoricalResponse.Data>, private val baseLineValue: String?) : SparkAdapter() {

    override fun getY(index: Int): Float {
        return historicalData[index].close.toFloat()
    }

    override fun getItem(index: Int): CryptoCompareHistoricalResponse.Data {
        return historicalData[index]
    }

    override fun getCount(): Int {
        return historicalData.size
    }

    override fun hasBaseLine(): Boolean {
        return true
    }

    override fun getBaseLine(): Float {
        return baseLineValue?.toFloat() ?: super.getBaseLine()
    }
}
