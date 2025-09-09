package com.infusiblecoder.cryptotracker.features.coindetails

import CoinDetailsPagerContract
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.infusiblecoder.cryptotracker.MyApplication
import com.infusiblecoder.cryptotracker.R
import com.infusiblecoder.cryptotracker.data.database.entities.WatchedCoin
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.infusiblecoder.cryptotracker.databinding.ActivityHomeBinding
import com.infusiblecoder.cryptotracker.databinding.ActivityPagerCoinDetailsBinding


class CoinDetailsPagerActivity : AppCompatActivity(), CoinDetailsPagerContract.View {

    private var watchedCoin: WatchedCoin? = null
    var isCoinInfoChanged = false

    private val allCoinDetailsRepository by lazy {
        CoinDetailsPagerRepository(MyApplication.database)
    }

    private val coinDetailPagerPresenter: CoinDetailPagerPresenter by lazy {
        CoinDetailPagerPresenter(allCoinDetailsRepository)
    }

    companion object {
        private const val WATCHED_COIN = "WATCHED_COIN"

        @JvmStatic
        fun buildLaunchIntent(context: Context, watchedCoin: WatchedCoin): Intent {
            val intent = Intent(context, CoinDetailsPagerActivity::class.java)
            intent.putExtra(WATCHED_COIN, watchedCoin)
            return intent
        }
    }
    private lateinit var binding: ActivityPagerCoinDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityPagerCoinDetailsBinding.inflate(layoutInflater)

        // Set the content view to the root view of the binding
        setContentView(binding.root)

        binding.toolbar.toolbar.elevation = 0f

        val toolbar = findViewById<View>(R.id.toolbar)
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        watchedCoin = intent.getParcelableExtra(WATCHED_COIN)

        coinDetailPagerPresenter.attachView(this)

        lifecycle.addObserver(coinDetailPagerPresenter)

        showOrHideLoadingIndicator(true)

        coinDetailPagerPresenter.loadWatchedCoins()

        FirebaseCrashlytics.getInstance().log("CoinDetailsPagerActivity")
    }

    override fun onWatchedCoinsLoaded(watchedCoinList: List<WatchedCoin>?) {

        supportActionBar?.title = getString(
            R.string.transactionTypeWithQuantity,
            watchedCoin?.coin?.coinName, watchedCoin?.coin?.symbol
        )

        val allCoinsPagerAdapter = CoinDetailsPagerAdapter(watchedCoinList, supportFragmentManager)
        binding.vpCoins.adapter = allCoinsPagerAdapter

        showOrHideLoadingIndicator(false)

        watchedCoinList?.forEachIndexed { index, watch ->
            if (watchedCoin?.coin?.name == watch.coin.name) {
                binding.vpCoins.currentItem = index
            }
        }

        binding. vpCoins.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                supportActionBar?.title = watchedCoinList?.get(position)?.coin?.coinName
            }
        })
    }

    override fun onNetworkError(errorMessage: String) {

        try {
            Snackbar.make(binding.vpCoins, errorMessage, Snackbar.LENGTH_SHORT).show()

        }catch (e:Exception){

        }
    }

    private fun showOrHideLoadingIndicator(showLoading: Boolean) {
        if (showLoading) {
            binding.  pbLoading.visibility = View.VISIBLE
        } else {
            binding.  pbLoading.visibility = View.GONE
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
