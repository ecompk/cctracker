package com.infusiblecoder.cryptotracker.features.exchangesearch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.infusiblecoder.cryptotracker.R
import com.infusiblecoder.cryptotracker.databinding.ActivityExchangePairSearchBinding
import com.infusiblecoder.cryptotracker.viewmodels.exchangePairItemView
import com.google.firebase.crashlytics.FirebaseCrashlytics

class ExchangeSearchActivity : AppCompatActivity() {

    companion object {
        private const val SEARCH_LIST = "search_list"
        private const val SEARCH_RESULT = "search_result"
        private const val TITLE = "title"

        @JvmStatic
        fun buildLaunchIntent(context: Context, searchList: ArrayList<String>, title: String): Intent {
            val intent = Intent(context, ExchangeSearchActivity::class.java)
            intent.putStringArrayListExtra(SEARCH_LIST, searchList)
            intent.putExtra(TITLE, title)
            return intent
        }

        fun getResultFromIntent(data: Intent): String {
            return data.getStringExtra(SEARCH_RESULT) ?: ""
        }
    }

    private lateinit var binding: ActivityExchangePairSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityExchangePairSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchList = intent.getStringArrayListExtra(SEARCH_LIST)
        checkNotNull(searchList)

        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(TITLE)

        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.divider_thin_horizontal)?.let { dividerItemDecoration.setDrawable(it) }
        binding.rvSearchList.addItemDecoration(dividerItemDecoration)

        setExchangeList(searchList)

        binding.etSearchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(filterText: Editable?) {
                val filterString = filterText.toString()
                setExchangeList(
                    searchList.filter {
                        it.contains(filterString, true)
                    }
                )
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        FirebaseCrashlytics.getInstance().log("ExchangeSearchActivity")
    }

    private fun setExchangeList(searchList: List<String>) {
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
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
