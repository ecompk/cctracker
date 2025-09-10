package com.infusiblecoder.cryptotracker.viewmodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import coil.clear
import coil.load
import coil.transform.CircleCropTransformation
import com.airbnb.epoxy.*
import com.infusiblecoder.cryptotracker.R
import com.infusiblecoder.cryptotracker.api.BASE_CRYPTOCOMPARE_IMAGE_URL
import com.infusiblecoder.cryptotracker.models.CryptoTicker

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class CoinTickerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val ivExchange: ImageView
    private val tvFromCoin: TextView
    private val tvToPrice: TextView
    private val tvExchange: TextView
    private val tvPrice: TextView
    private val tvVolume: TextView
    private val clMarket: View

    private val cropCircleTransformation by lazy {
        CircleCropTransformation()
    }

    init {
        View.inflate(context, R.layout.ticker_item, this)
        ivExchange = findViewById(R.id.ivExchange)
        tvFromCoin = findViewById(R.id.tvFromCoin)
        tvToPrice = findViewById(R.id.tvToPrice)
        tvExchange = findViewById(R.id.tvExchange)
        tvPrice = findViewById(R.id.tvPrice)
        tvVolume = findViewById(R.id.tvVolume)
        clMarket = findViewById(R.id.clMarket)
    }

    @ModelProp
    fun setTicker(ticker: CryptoTicker) {
        tvFromCoin.text = ticker.base
        tvToPrice.text = ticker.target
        tvExchange.text = ticker.marketName
        if (ticker.imageUrl.isNotEmpty()) {
            ivExchange.load(BASE_CRYPTOCOMPARE_IMAGE_URL + ticker.imageUrl) {
                crossfade(true)
                error(R.drawable.ic_appicon)
                transformations(cropCircleTransformation)
            }
        } else {
            ivExchange.load(R.drawable.ic_appicon)
        }
    }

    @TextProp
    fun setTickerPrice(price: CharSequence) {
        tvPrice.text = price
    }

    @TextProp
    fun setTickerVolume(volume: CharSequence) {
        tvVolume.text = volume
    }

    @OnViewRecycled
    fun viewRecycled() {
        ivExchange.clear()
    }

    @CallbackProp
    fun setItemClickListener(listener: OnClickListener?) {
        clMarket.setOnClickListener(listener)
    }
}
