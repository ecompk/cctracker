package com.infusiblecoder.cryptotracker.features.pairsearch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.infusiblecoder.cryptotracker.R
import com.infusiblecoder.cryptotracker.databinding.ActivityExchangePairSearchBinding
import com.infusiblecoder.cryptotracker.viewmodels.exchangePairItemView

class PairSearchActivity : AppCompatActivity() {

    companion object {
        private const val SEARCH_LIST = "search_list"
        private const val SEARCH_RESULT = "search_result"
        private const val COIN_SYMBOL = "coin_symbol"

        @JvmStatic
        fun buildLaunchIntent(context: Context, searchList: ArrayList<String>, coinSymbol: String): Intent {
            val intent = Intent(context, PairSearchActivity::class.java)
            intent.putStringArrayListExtra(SEARCH_LIST, searchList)
            intent.putExtra(COIN_SYMBOL, coinSymbol)
            return intent
        }

        fun getResultFromIntent(data: Intent): String {
            return data.getStringExtra(SEARCH_RESULT) ?: ""
        }
    }

    private lateinit var binding: ActivityExchangePairSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExchangePairSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchList = intent.getStringArrayListExtra(SEARCH_LIST) ?: arrayListOf()

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.change_trading_pair)

        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.divider_thin_horizontal)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        binding.rvSearchList.addItemDecoration(dividerItemDecoration)

        setPairList(searchList)

        binding.etSearchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(filterText: Editable?) {
                val filterString = filterText.toString()
                setPairList(searchList.filter {
                    it.contains(filterString, true)
                })
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        FirebaseCrashlytics.getInstance().log("PairSearchActivity")
    }

    private fun setPairList(searchList: List<String>) {
        binding.rvSearchList.withModels {
            searchList.forEachIndexed { index, s ->
                exchangePairItemView {
                    id(s + index)
                    exchangeName(s)
                    itemClickListener { _ ->
                        intent.putExtra(SEARCH_RESULT, s)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
