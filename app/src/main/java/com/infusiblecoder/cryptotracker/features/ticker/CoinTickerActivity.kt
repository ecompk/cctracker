package com.infusiblecoder.cryptotracker.features.ticker

import CoinTickerContract
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.infusiblecoder.cryptotracker.MyApplication
import com.infusiblecoder.cryptotracker.R
import com.infusiblecoder.cryptotracker.data.PreferenceManager
import com.infusiblecoder.cryptotracker.viewmodels.coinTickerView
import com.infusiblecoder.cryptotracker.featurecomponents.cointickermodule.CoinTickerPresenter
import com.infusiblecoder.cryptotracker.featurecomponents.cointickermodule.CoinTickerRepository
import com.infusiblecoder.cryptotracker.models.CryptoTicker
import com.infusiblecoder.cryptotracker.utils.Formaters
import com.infusiblecoder.cryptotracker.utils.getUrlWithoutParameters
import com.infusiblecoder.cryptotracker.utils.openCustomTab
import com.infusiblecoder.cryptotracker.utils.resourcemanager.AndroidResourceManagerImpl
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.infusiblecoder.cryptotracker.databinding.ActivityCoinTickerListBinding
import java.util.*

class CoinTickerActivity : AppCompatActivity(), CoinTickerContract.View {

    companion object {
        private const val COIN_NAME = "COIN_FULL_NAME"

        @JvmStatic
        fun buildLaunchIntent(context: Context, coinName: String): Intent {
            val intent = Intent(context, CoinTickerActivity::class.java)
            intent.putExtra(COIN_NAME, coinName)
            return intent
        }
    }

    private val coinTickerRepository by lazy {
        CoinTickerRepository(MyApplication.database)
    }

    private val androidResourceManager by lazy {
        AndroidResourceManagerImpl(this)
    }

    private val formatter: Formaters by lazy {
        Formaters(androidResourceManager)
    }

    private val coinTickerPresenter: CoinTickerPresenter by lazy {
        CoinTickerPresenter(coinTickerRepository, androidResourceManager)
    }

    private val currency: Currency by lazy {
        Currency.getInstance(PreferenceManager.getDefaultCurrency(this))
    }

    private var _binding: ActivityCoinTickerListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCoinTickerListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val coinName = intent.getStringExtra(COIN_NAME)?.trim()

        supportActionBar?.title = getString(R.string.tickerActivityTitle, coinName)

        coinTickerPresenter.attachView(this)

        lifecycle.addObserver(coinTickerPresenter)

        if (coinName != null) {
            coinTickerPresenter.getCryptoTickers(coinName.lowercase())
        }

        FirebaseCrashlytics.getInstance().log("CoinTickerActivity")
    }

    override fun showOrHideLoadingIndicatorForTicker(showLoading: Boolean) {
        if (!showLoading) {
            binding.pbLoading.hide()
        } else {
            binding.pbLoading.show()
        }
    }

    override fun onPriceTickersLoaded(tickerData: List<CryptoTicker>) {
        binding.rvCoinTickerList.withModels {
            tickerData.forEachIndexed { index, cryptoTicker ->
                coinTickerView {
                    id(index)
                    ticker(cryptoTicker)
                    tickerPrice(formatter.formatAmount(cryptoTicker.convertedVolumeUSD, currency, true))
                    tickerVolume(formatter.formatAmount(cryptoTicker.last, currency, true))
                    itemClickListener { _ ->
                        if (cryptoTicker.exchangeUrl.isNotBlank()) {
                            openCustomTab(getUrlWithoutParameters(cryptoTicker.exchangeUrl), this@CoinTickerActivity)
                        }
                    }
                }
            }
        }

        binding.tvFooter.setOnClickListener {
            openCustomTab(getString(R.string.coin_gecko_url), this)
        }
    }

    override fun onNetworkError(errorMessage: String) {
        try {
            Snackbar.make(binding.rvCoinTickerList, errorMessage, Snackbar.LENGTH_SHORT).show()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log("Network error: ${e.message}")
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
