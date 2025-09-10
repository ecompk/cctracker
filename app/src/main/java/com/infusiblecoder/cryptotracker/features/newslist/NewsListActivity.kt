package com.infusiblecoder.cryptotracker.features.newslist

import CryptoNewsContract
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.infusiblecoder.cryptotracker.R
import com.infusiblecoder.cryptotracker.databinding.ActivityNewsListBinding
import com.infusiblecoder.cryptotracker.featurecomponents.cryptonewsmodule.CryptoNewsPresenter
import com.infusiblecoder.cryptotracker.featurecomponents.cryptonewsmodule.CryptoNewsRepository
import com.infusiblecoder.cryptotracker.models.CryptoPanicNews
import com.infusiblecoder.cryptotracker.utils.Formaters
import com.infusiblecoder.cryptotracker.utils.openCustomTab
import com.infusiblecoder.cryptotracker.utils.resourcemanager.AndroidResourceManager
import com.infusiblecoder.cryptotracker.utils.resourcemanager.AndroidResourceManagerImpl
import com.infusiblecoder.cryptotracker.viewmodels.newsItemView
import timber.log.Timber

class NewsListActivity : AppCompatActivity(), CryptoNewsContract.View {

    companion object {
        private const val COIN_FULL_NAME = "COIN_FULL_NAME"
        private const val COIN_SYMBOL = "COIN_SYMBOL"

        @JvmStatic
        fun buildLaunchIntent(context: Context, coinName: String, coinSymbol: String): Intent {
            val intent = Intent(context, NewsListActivity::class.java)
            intent.putExtra(COIN_FULL_NAME, coinName)
            intent.putExtra(COIN_SYMBOL, coinSymbol)
            return intent
        }
    }

    private val cryptoNewsRepository by lazy {
        CryptoNewsRepository()
    }
    private val cryptoNewsPresenter: CryptoNewsPresenter by lazy {
        CryptoNewsPresenter(cryptoNewsRepository)
    }

    private val androidResourceManager: AndroidResourceManager by lazy {
        AndroidResourceManagerImpl(this)
    }

    private val formatter: Formaters by lazy {
        Formaters(androidResourceManager)
    }

    private lateinit var binding: ActivityNewsListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val coinFullName = intent.getStringExtra(COIN_FULL_NAME)?.trim()
        val coinSymbol = intent.getStringExtra(COIN_SYMBOL)?.trim()

        supportActionBar?.title = getString(R.string.newsActivityTitle, coinFullName)

        cryptoNewsPresenter.attachView(this)
        lifecycle.addObserver(cryptoNewsPresenter)

        if (coinSymbol != null) {
            cryptoNewsPresenter.getCryptoNews(coinSymbol)
        }

        FirebaseCrashlytics.getInstance().log("NewsListActivity")
    }

    override fun showOrHideLoadingIndicator(showLoading: Boolean) {
        if (!showLoading) {
            binding.pbLoading.hide()
        } else {
            binding.pbLoading.show()
        }
    }

    override fun onNewsLoaded(cryptoPanicNews: CryptoPanicNews) {
        binding.rvNewsList.withModels {
            cryptoPanicNews.results?.forEachIndexed { index, result ->
                newsItemView {
                    id(index)
                    title(result.title)
                    newsDate(formatter.parseAndFormatIsoDate(result.created_at, true))
                    itemClickListener { _ ->
                        openCustomTab(result.url, this@NewsListActivity)
                    }
                }
            }
        }
        binding.tvFooter.setOnClickListener {
            openCustomTab(getString(R.string.crypto_panic_url), this)
        }
    }

    override fun onNetworkError(errorMessage: String) {
        try {
            Snackbar.make(binding.rvNewsList, errorMessage, Snackbar.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}
