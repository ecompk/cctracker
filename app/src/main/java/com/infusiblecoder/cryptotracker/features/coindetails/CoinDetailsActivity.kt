package com.infusiblecoder.cryptotracker.features.coindetails

import CoinDetailsContract
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.infusiblecoder.cryptotracker.MyApplication
import com.infusiblecoder.cryptotracker.R
import com.infusiblecoder.cryptotracker.data.database.entities.WatchedCoin
import com.infusiblecoder.cryptotracker.features.CryptoCompareRepository
import com.infusiblecoder.cryptotracker.features.coin.CoinFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.infusiblecoder.cryptotracker.databinding.ActivityCoinDetailsBinding

class CoinDetailsActivity : AppCompatActivity(), CoinDetailsContract.View {

    companion object {
        private const val WATCHED_COIN = "WATCHED_COIN"
        private const val COIN_SYMBOL = "COIN_SYMBOL"

        @JvmStatic
        fun buildLaunchIntent(context: Context, watchedCoin: WatchedCoin): Intent {
            val intent = Intent(context, CoinDetailsActivity::class.java)
            intent.putExtra(WATCHED_COIN, watchedCoin)
            return intent
        }

        @JvmStatic
        fun buildLaunchIntent(context: Context, symbol: String): Intent {
            val intent = Intent(context, CoinDetailsActivity::class.java)
            intent.putExtra(COIN_SYMBOL, symbol)
            return intent
        }
    }

    private lateinit var binding: ActivityCoinDetailsBinding

    private val coinRepo by lazy {
        CryptoCompareRepository(MyApplication.database)
    }

    private val coinDetailPresenter: CoinDetailPresenter by lazy {
        CoinDetailPresenter(coinRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.toolbar.elevation = 0f

        coinDetailPresenter.attachView(this)
        lifecycle.addObserver(coinDetailPresenter)

        val watchedCoin: WatchedCoin? = intent.getParcelableExtra(WATCHED_COIN)
        if (watchedCoin != null) {
            onWatchedCoinLoaded(watchedCoin)
        } else {
            intent.getStringExtra(COIN_SYMBOL)?.let {
                coinDetailPresenter.getWatchedCoinFromSymbol(it)
            }
        }

        FirebaseCrashlytics.getInstance().log("CoinDetailsActivity")
    }

    override fun onWatchedCoinLoaded(coin: WatchedCoin?) {
        if (coin != null) {
            showOrHideLoadingIndicator(false)

            val coinDetailsFragment = CoinFragment()
            coinDetailsFragment.arguments = CoinFragment.getArgumentBundle(coin)

            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.flCoinDetails, coinDetailsFragment)
            fragmentTransaction.commit()

            supportActionBar?.title = getString(
                R.string.transactionTypeWithQuantity,
                coin.coin.coinName, coin.coin.symbol
            )
        }
    }

    override fun showOrHideLoadingIndicator(showLoading: Boolean) {
        if (!showLoading) {
            binding.pbLoading.hide()
        } else {
            binding.pbLoading.show()
        }
    }

    override fun onNetworkError(errorMessage: String) {
        try {
            Snackbar.make(binding.flCoinDetails, errorMessage, Snackbar.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Handle exception if necessary
        }
    }
}