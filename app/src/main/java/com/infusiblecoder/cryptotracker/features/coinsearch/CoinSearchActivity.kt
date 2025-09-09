package com.infusiblecoder.cryptotracker.features.coinsearch

import CoinSearchContract
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.infusiblecoder.cryptotracker.MyApplication
import com.infusiblecoder.cryptotracker.R
import com.infusiblecoder.cryptotracker.data.database.entities.WatchedCoin
import com.infusiblecoder.cryptotracker.viewmodels.CoinSearchItemView
import com.infusiblecoder.cryptotracker.features.CryptoCompareRepository
import com.infusiblecoder.cryptotracker.features.coindetails.CoinDetailsActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.infusiblecoder.cryptotracker.databinding.ActivityCoinSearchBinding
import com.infusiblecoder.cryptotracker.databinding.ActivityHomeBinding
import com.infusiblecoder.cryptotracker.databinding.FragmentDiscoveryBinding
import com.infusiblecoder.cryptotracker.viewmodels.coinSearchItemView
import java.math.BigDecimal

class CoinSearchActivity : AppCompatActivity(), CoinSearchContract.View {
    private var isCoinInfoChanged = false

    companion object {
        @JvmStatic
        fun buildLaunchIntent(context: Context): Intent {
            return Intent(context, CoinSearchActivity::class.java)
        }
    }

    private val coinRepo by lazy {
        CryptoCompareRepository(MyApplication.database)
    }

    private val coinSearchPresenter: CoinSearchPresenter by lazy {
        CoinSearchPresenter(coinRepo)
    }


    private lateinit var binding: ActivityCoinSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.rvSearchList.layoutManager = LinearLayoutManager(this)

        coinSearchPresenter.attachView(this)

        lifecycle.addObserver(coinSearchPresenter)

        coinSearchPresenter.loadAllCoins()

        FirebaseCrashlytics.getInstance().log("CoinSearchActivity")
    }

    override fun showOrHideLoadingIndicator(showLoading: Boolean) {
        if (!showLoading) {
            binding. pbLoading.hide()
        } else {
            binding.  pbLoading.show()
        }
    }

    override fun onNetworkError(errorMessage: String) {
        try {
            Snackbar.make(binding.rvSearchList, errorMessage, Snackbar.LENGTH_SHORT).show()
        }catch (e:Exception){

        }

    }

    override fun onCoinsLoaded(coinList: List<WatchedCoin>) {

        showCoinsInTheList(coinList)

        binding.etSearchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(filterText: Editable?) {
                val filterString = filterText.toString().trim().toLowerCase()

                showCoinsInTheList(
                    coinList.filter { watchedCoin ->
                        watchedCoin.coin.coinName.contains(filterString, true) ||
                            watchedCoin.coin.symbol.contains(filterString, true)
                    }
                )
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    private fun showCoinsInTheList(coinList: List<WatchedCoin>) {
        binding.rvSearchList.withModels {
            coinList.forEach { watchedCoin ->
                coinSearchItemView {
                    id(watchedCoin.coin.id)
                    watchedCoin(watchedCoin)
                    itemClickListener { _ ->
                        val coinDetailsIntent = CoinDetailsActivity.buildLaunchIntent(this@CoinSearchActivity, watchedCoin)
                        startActivity(coinDetailsIntent)
                    }
                    onWatchedChecked(object : CoinSearchItemView.OnSearchItemClickListener {
                        override fun onItemWatchedClicked(watched: Boolean) {
                            if (watchedCoin.purchaseQuantity == BigDecimal.ZERO) {
                                coinSearchPresenter.updateCoinWatchedStatus(watched, watchedCoin.coin.id, watchedCoin.coin.symbol)
                                isCoinInfoChanged = true
                            } else {

                                try {
                                    Snackbar.make(binding.rvSearchList, getString(R.string.coin_already_purchased), Snackbar.LENGTH_SHORT).show()
                                }catch (e:Exception){

                                }

                            }
                        }
                    })
                }
            }
        }
    }

    override fun onCoinWatchedStatusUpdated(watched: Boolean, coinSymbol: String) {

        val statusText = if (watched) {
            getString(R.string.coin_added_to_watchlist, coinSymbol)
        } else {
            getString(R.string.coin_removed_to_watchlist, coinSymbol)
        }
        try {
            Snackbar.make(binding.rvSearchList, statusText, Snackbar.LENGTH_SHORT).show()
        }catch (e:Exception){

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (isCoinInfoChanged) {
            setResult(Activity.RESULT_OK)
        }

        finish()
    }
}
