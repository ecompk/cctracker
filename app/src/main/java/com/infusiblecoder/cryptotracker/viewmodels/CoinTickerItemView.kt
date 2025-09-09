package com.infusiblecoder.cryptotracker.viewmodels

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import coil.transform.CircleCropTransformation
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.infusiblecoder.cryptotracker.R
import com.infusiblecoder.cryptotracker.data.PreferenceManager
import com.infusiblecoder.cryptotracker.featurecomponents.ModuleItem
import com.infusiblecoder.cryptotracker.api.BASE_CRYPTOCOMPARE_IMAGE_URL
import com.infusiblecoder.cryptotracker.databinding.CoinTickerModuleBinding
import com.infusiblecoder.cryptotracker.databinding.GenericFooterModuleBinding
import com.infusiblecoder.cryptotracker.models.CryptoTicker
import com.infusiblecoder.cryptotracker.utils.Formaters
import com.infusiblecoder.cryptotracker.utils.getUrlWithoutParameters
import com.infusiblecoder.cryptotracker.utils.openCustomTab
import com.infusiblecoder.cryptotracker.utils.resourcemanager.AndroidResourceManager
import com.infusiblecoder.cryptotracker.utils.resourcemanager.AndroidResourceManagerImpl
import java.util.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class CoinTickerItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val currency by lazy {
        Currency.getInstance(PreferenceManager.getDefaultCurrency(context))
    }

    private val androidResourceManager: AndroidResourceManager by lazy {
        AndroidResourceManagerImpl(context)
    }
    private val formatter: Formaters by lazy {
        Formaters(androidResourceManager)
    }

    private val cropCircleTransformation by lazy {
        CircleCropTransformation()
    }

    private var binding: CoinTickerModuleBinding


    init {
        // Initialize ViewBinding
        binding = CoinTickerModuleBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
    }

    @ModelProp(options = [ModelProp.Option.IgnoreRequireHashCode])
    fun setCoinTickerData(coinTickerModuleData: CoinTickerModuleData) {
        val tickerData = coinTickerModuleData.tickerData

        if (tickerData.isNotEmpty()) {

            binding.pbLoading.visibility = View.GONE

            binding.tvFirstFromCoin.text = tickerData[0].base
            binding.tvFirstToPrice.text = tickerData[0].target
            binding.tvFirstPrice.text = formatter.formatAmount(tickerData[0].last, currency, true)
            binding.tvFirstExchange.text = tickerData[0].marketName
            binding.tvFirstVolume.text = formatter.formatAmount(tickerData[0].convertedVolumeUSD, currency, true)
            binding.ivFirstExchange.visibility = View.VISIBLE

            if (tickerData[0].imageUrl.isNotEmpty()) {
                binding.ivFirstExchange.load(BASE_CRYPTOCOMPARE_IMAGE_URL + tickerData[0].imageUrl) {
                    crossfade(true)
                    error(R.drawable.ic_appicon)
                    transformations(cropCircleTransformation)
                }
            } else {
                binding.ivFirstExchange.load(R.drawable.ic_appicon)
            }

            binding.clFirstMarket.setOnClickListener {
                if (tickerData[0].exchangeUrl.isNotBlank()) {
                    openCustomTab(getUrlWithoutParameters(tickerData[0].exchangeUrl), context)
                }
            }

            if (tickerData.size > 1) {
                binding.tvSecondFromCoin.text = tickerData[1].base
                binding.tvSecondToPrice.text = tickerData[1].target
                binding.tvSecondPrice.text = formatter.formatAmount(tickerData[1].last, currency, true)
                binding.tvSecondExchange.text = tickerData[1].marketName
                binding.tvSecondVolume.text = formatter.formatAmount(tickerData[1].convertedVolumeUSD, currency, true)
                binding.clSecondMarket.setOnClickListener {
                    if (tickerData[1].exchangeUrl.isNotBlank()) {
                        openCustomTab(getUrlWithoutParameters(tickerData[1].exchangeUrl), context)
                    }
                }
                binding.ivSecondExchange.visibility = View.VISIBLE

                if (tickerData[1].imageUrl.isNotEmpty()) {
                    binding.ivSecondExchange.load(BASE_CRYPTOCOMPARE_IMAGE_URL + tickerData[1].imageUrl) {
                        crossfade(true)
                        error(R.drawable.ic_appicon)
                        transformations(cropCircleTransformation)
                    }
                } else {
                    binding.ivSecondExchange.load(R.drawable.ic_appicon)
                }
            }

            if (tickerData.size > 2) {
                binding.tvThirdFromCoin.text = tickerData[0].base
                binding.tvThirdToPrice.text = tickerData[0].target
                binding.tvThirdPrice.text = formatter.formatAmount(tickerData[2].last, currency, true)
                binding.tvThirdExchange.text = tickerData[2].marketName
                binding.tvThirdVolume.text = formatter.formatAmount(tickerData[2].convertedVolumeUSD, currency, true)
                binding.clThirdMarket.setOnClickListener {
                    if (tickerData[2].exchangeUrl.isNotBlank()) {
                        openCustomTab(getUrlWithoutParameters(tickerData[2].exchangeUrl), context)
                    }
                }
                binding. ivThirdExchange.visibility = View.VISIBLE

                if (tickerData[2].imageUrl.isNotEmpty()) {
                    binding.ivThirdExchange.load(BASE_CRYPTOCOMPARE_IMAGE_URL + tickerData[2].imageUrl) {
                        crossfade(true)
                        error(R.drawable.ic_appicon)
                        transformations(cropCircleTransformation)
                    }
                } else {
                    binding.ivThirdExchange.load(R.drawable.ic_appicon)
                }
            }
        } else {
            binding. tvTickerError.visibility = View.VISIBLE
            binding. tickerContentGroup.visibility = View.GONE
        }
    }

    @CallbackProp
    fun setMoreClickListener(listener: OnClickListener?) {
        binding. tvMore.setOnClickListener(listener)
    }

    data class CoinTickerModuleData(val tickerData: List<CryptoTicker>) : ModuleItem
}
