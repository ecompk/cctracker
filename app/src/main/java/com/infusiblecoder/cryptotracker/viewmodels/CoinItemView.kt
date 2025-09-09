package com.infusiblecoder.cryptotracker.viewmodels

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import coil.load
import coil.transform.RoundedCornersTransformation
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.infusiblecoder.cryptotracker.R
import com.infusiblecoder.cryptotracker.api.BASE_CRYPTOCOMPARE_IMAGE_URL
import com.infusiblecoder.cryptotracker.data.PreferenceManager
import com.infusiblecoder.cryptotracker.data.database.entities.CoinTransaction
import com.infusiblecoder.cryptotracker.data.database.entities.WatchedCoin
import com.infusiblecoder.cryptotracker.databinding.DashboardCoinModuleBinding
import com.infusiblecoder.cryptotracker.featurecomponents.ModuleItem
import com.infusiblecoder.cryptotracker.models.CoinPrice
import com.infusiblecoder.cryptotracker.utils.CryptoExtendedCurrency
import com.infusiblecoder.cryptotracker.utils.Formaters
import com.infusiblecoder.cryptotracker.utils.chartAnimationDuration
import com.infusiblecoder.cryptotracker.utils.getTotalCost
import com.infusiblecoder.cryptotracker.utils.resourcemanager.AndroidResourceManager
import com.infusiblecoder.cryptotracker.utils.resourcemanager.AndroidResourceManagerImpl
import java.math.BigDecimal
import java.util.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class CoinItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val toCurrency: String by lazy {
        PreferenceManager.getDefaultCurrency(context.applicationContext)
    }

    private val currency by lazy {
        Currency.getInstance(toCurrency)
    }

    val androidResourceManager: AndroidResourceManager by lazy {
        AndroidResourceManagerImpl(context)
    }

    private val formatter by lazy {
        Formaters(androidResourceManager)
    }

    private val cropCircleTransformation by lazy {
        RoundedCornersTransformation(15F)
    }

    private var onCoinItemClickListener: OnCoinItemClickListener? = null

    private var binding: DashboardCoinModuleBinding


    init {
        // Initialize ViewBinding
        binding = DashboardCoinModuleBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
    }


    @ModelProp
    fun setDashboardCoinModuleData(dashboardCoinModuleData: DashboardCoinModuleData) {
        val coin = dashboardCoinModuleData.watchedCoin.coin
        val coinPrice = dashboardCoinModuleData.coinPrice

        val imageUrl = BASE_CRYPTOCOMPARE_IMAGE_URL + "${coin.imageUrl}?width=50"
        binding.ivCoin.load(imageUrl) {
            crossfade(true)
            error(R.drawable.ic_appicon)
            transformations(cropCircleTransformation)
        }

        binding.tvCoinName.text = coin.coinName

        if (coinPrice != null) {
            binding.pbLoading.hide()

            if (coinPrice.changePercentageDay != null) {
                binding.tvCoinPercentChange.text = androidResourceManager.getString(
                    R.string.coinDayChanges,
                    coinPrice.changePercentageDay.toDouble()
                )

                if (coinPrice.changePercentageDay.toDouble() < 0) {
                    binding.tvCoinPercentChange.setTextColor(ContextCompat.getColor(context, R.color.colorLoss))
                } else {
                    binding.tvCoinPercentChange.setTextColor(ContextCompat.getColor(context, R.color.colorGain))
                }
            }

            animateCoinPrice(coinPrice.price)
            val purchaseQuantity = dashboardCoinModuleData.watchedCoin.purchaseQuantity

            binding.tvCoinMarketCap.text = CryptoExtendedCurrency.getAmountTextForDisplay(BigDecimal(coinPrice.marketCap), currency)

            // check if coin is purchased
            if (purchaseQuantity > BigDecimal.ZERO) {
                binding.purchaseItemsGroup.visibility = View.VISIBLE
                binding.tvQuantity.text = purchaseQuantity.toPlainString()

                val currentWorth = purchaseQuantity.multiply(BigDecimal(coinPrice.price))
                val totalCost = getTotalCost(dashboardCoinModuleData.coinTransactionList, coin.symbol)

                binding.tvCurrentValue.text = formatter.formatAmount(currentWorth.toPlainString(), currency)

                // do the profit or loss things here.
                val totalReturnAmount = currentWorth?.subtract(totalCost)
                // val totalReturnPercentage = (totalReturnAmount?.divide(totalCost, mc))?.multiply(BigDecimal(100), mc)

                if (totalReturnAmount != null) {
                    binding.tvProfitLoss.text = formatter.formatAmount(totalReturnAmount.toPlainString(), currency)
                }

                if (totalReturnAmount != null && totalReturnAmount < BigDecimal.ZERO) {
                    binding.tvProfitLoss.setTextColor(ContextCompat.getColor(context, R.color.colorLoss))
                } else {
                    binding.tvProfitLoss.setTextColor(ContextCompat.getColor(context, R.color.colorGain))
                }
            } else {
                binding.purchaseItemsGroup.visibility = View.GONE
            }

            binding.coinCard.setOnClickListener {
                onCoinItemClickListener?.onCoinClicked(dashboardCoinModuleData.watchedCoin)
            }
        }

        if (dashboardCoinModuleData.isTopCard) {
            binding.coinCard.background = context.getDrawable(R.drawable.ripple_background_rounded_top)
        }
    }

    @CallbackProp
    fun setItemClickListener(listener: OnCoinItemClickListener?) {
        onCoinItemClickListener = listener
    }

    private fun animateCoinPrice(amount: String?) {
        if (amount != null) {
            val chartCoinPriceAnimation = ValueAnimator.ofFloat(0f, amount.toFloat())
            chartCoinPriceAnimation.duration = chartAnimationDuration
            chartCoinPriceAnimation.addUpdateListener { updatedAnimation ->
                val animatedValue = updatedAnimation.animatedValue as Float
                binding.tvCost.text = formatter.formatAmount(animatedValue.toString(), currency)
                binding.tvCost.tag = animatedValue
            }
            chartCoinPriceAnimation.start()
        }
    }

    data class DashboardCoinModuleData(
        val isTopCard: Boolean = false,
        val watchedCoin: WatchedCoin,
        var coinPrice: CoinPrice?,
        val coinTransactionList: List<CoinTransaction>
    ) : ModuleItem

    interface OnCoinItemClickListener {
        fun onCoinClicked(watchedCoin: WatchedCoin)
    }
}
