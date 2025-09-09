package com.infusiblecoder.cryptotracker.features.settings

import SettingsContract
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
//import com.infusiblecoder.cryptotracker.BuildConfig
import com.infusiblecoder.cryptotracker.MyApplication
import com.infusiblecoder.cryptotracker.R
import com.infusiblecoder.cryptotracker.data.PreferenceManager
import com.infusiblecoder.cryptotracker.features.CryptoCompareRepository
import com.infusiblecoder.cryptotracker.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mynameismidori.currencypicker.CurrencyPicker
import com.infusiblecoder.cryptotracker.databinding.FragmentSettingsBinding
import timber.log.Timber

class SettingsFragment : Fragment(), SettingsContract.View {

    companion object {
        const val TAG = "SettingsFragment"
    }

    private val coinRepo by lazy {
        CryptoCompareRepository(MyApplication.database)
    }

    private val settingsPresenter: SettingsPresenter by lazy {
        SettingsPresenter(coinRepo)
    }

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val toolbar = binding.toolbar
        toolbar?.title = getString(R.string.settings)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        settingsPresenter.attachView(this)
        lifecycle.addObserver(settingsPresenter)

        initializeUI()

        FirebaseCrashlytics.getInstance().log("SettingsFragment")

        return binding.root
    }

    private fun initializeUI() {
        val currency = PreferenceManager.getPreference(requireContext(), PreferenceManager.DEFAULT_CURRENCY, defaultCurrency)
        binding.tvCurrencyValue.text = currency

        binding.clCurrency.setOnClickListener {
            openCurrencyPicker()
        }

        binding.clCurrencyList.setOnClickListener {
            settingsPresenter.refreshCoinList(currency)
            binding.ivCurrencyList.visibility = View.INVISIBLE
            binding.pbLoadingCurrencyList.visibility = View.VISIBLE
        }

        binding.clExchangeList.setOnClickListener {
            settingsPresenter.refreshExchangeList()
            binding.ivExchangeList.visibility = View.INVISIBLE
            binding.pbLoadingExchangeList.visibility = View.VISIBLE
        }

        binding.clRate.setOnClickListener {
            rateMyApp(requireContext())
        }

        binding.clShare.setOnClickListener {
            shareMyApp(requireContext())
        }

        binding.clFeedback.setOnClickListener {
            sendEmail(requireContext(), getString(R.string.email_address), getString(R.string.feedback_cryptotracker), "Hello, \n")
        }

        binding.clPrivacy.setOnClickListener {
            openCustomTab(getString(R.string.privacyPolicyUrl), requireContext())
        }

//        binding.tvAppVersionValue.text = BuildConfig.VERSION_NAME

        binding.clAttribution.setOnClickListener {
            openCustomTab(getString(R.string.attributionUrl), requireContext())
        }

        binding.clCryptoCompare.setOnClickListener {
            openCustomTab(getString(R.string.crypto_compare_url), requireContext())
        }

        binding.clCoinGecko.setOnClickListener {
            openCustomTab(getString(R.string.coin_gecko_url), requireContext())
        }

        binding.clCryptoPanic.setOnClickListener {
            openCustomTab(getString(R.string.crypto_panic_url), requireContext())
        }
    }

    private fun openCurrencyPicker() {
        val picker = CurrencyPicker.newInstance(getString(R.string.select_currency)) // dialog title

        picker.setListener { name, code, _, _ ->
            Timber.d("Currency code selected $name,$code")
            PreferenceManager.setPreference(requireContext(), PreferenceManager.DEFAULT_CURRENCY, code)
            picker.dismiss() // Show currency that is picked.

            val currency = PreferenceManager.getPreference(requireContext(), PreferenceManager.DEFAULT_CURRENCY, defaultCurrency)
            binding.tvCurrencyValue.text = currency

            // get list of all coins
            settingsPresenter.refreshCoinList(currency)
        }

        childFragmentManager?.let {
            picker.show(it, "CURRENCY_PICKER")
        }
    }

    override fun onCoinListRefreshed() {
        binding.ivCurrencyList.visibility = View.VISIBLE
        binding.pbLoadingCurrencyList.visibility = View.GONE
    }

    override fun onExchangeListRefreshed() {
        binding.ivExchangeList.visibility = View.VISIBLE
        binding.pbLoadingExchangeList.visibility = View.GONE
    }

    override fun onNetworkError(errorMessage: String) {
        try {
            Snackbar.make(binding.llSettings, errorMessage, Snackbar.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun onDestroyView() {
        _binding = null
        (activity as AppCompatActivity).setSupportActionBar(null)
        super.onDestroyView()
    }
}
