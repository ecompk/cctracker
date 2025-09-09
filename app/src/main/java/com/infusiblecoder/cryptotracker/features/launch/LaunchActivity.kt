package com.infusiblecoder.cryptotracker.features.launch

import LaunchContract
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.snackbar.Snackbar
import com.infusiblecoder.cryptotracker.MyApplication
import com.infusiblecoder.cryptotracker.R
import com.infusiblecoder.cryptotracker.data.PreferenceManager
import com.infusiblecoder.cryptotracker.databinding.ActivityLaunchBinding
import com.infusiblecoder.cryptotracker.features.CryptoCompareRepository
import com.infusiblecoder.cryptotracker.features.HomeActivity
import com.infusiblecoder.cryptotracker.utils.CryptoExtendedCurrency
import com.infusiblecoder.cryptotracker.utils.ui.IntroPageTransformer
import com.mynameismidori.currencypicker.CurrencyPicker
import timber.log.Timber

class LaunchActivity : AppCompatActivity(), LaunchContract.View {

    private val coinRepo by lazy {
        CryptoCompareRepository(MyApplication.database)
    }

    private val launchPresenter: LaunchPresenter by lazy {
        LaunchPresenter(coinRepo)
    }

    private lateinit var binding: ActivityLaunchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        launchPresenter.attachView(this)
        lifecycle.addObserver(launchPresenter)

        // Determine if this is the first time, if yes then show the animations, else move away
        if (!PreferenceManager.getPreference(this, PreferenceManager.IS_LAUNCH_FTU_SHOWN, false)) {
            initializeUI()

            launchPresenter.loadAllCoins()
        } else {
            startActivity(HomeActivity.buildLaunchIntent(this))
            finish()
        }
    }

    private fun initializeUI() {
        // Set an Adapter on the ViewPager
        binding.introPager.adapter = IntroAdapter(supportFragmentManager)

        // Set a PageTransformer
        binding.introPager.setPageTransformer(false, IntroPageTransformer())
    }

    override fun onCoinsLoaded() {
        binding.splashGroup.visibility = View.GONE
        binding.viewpagerGroup.visibility = View.VISIBLE
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        Timber.i("Suppressing back press")
    }

    override fun onNetworkError(errorMessage: String) {
        try {
            Snackbar.make(binding.introPager, errorMessage, Snackbar.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun openCurrencyPicker() {
        try {
            val picker = CurrencyPicker.newInstance(getString(R.string.select_currency))

            picker.setCurrenciesList(CryptoExtendedCurrency.CURRENCIES)

            picker.setListener { name, code, _, _ ->
                Timber.d("Currency code selected $name,$code")
                PreferenceManager.setPreference(this, PreferenceManager.DEFAULT_CURRENCY, code)

                picker.dismiss()

                // Show loading screen
                val currentFragment = (binding.introPager.adapter as IntroAdapter).getCurrentFragment()
                if (currentFragment is IntroFragment) {
                    currentFragment.showLoadingScreen()
                }

                binding.introPager.beginFakeDrag()

                // Get list of all coins
                launchPresenter.getAllSupportedCoins(code)

                // Mark FTU as shown
                PreferenceManager.setPreference(this, PreferenceManager.IS_LAUNCH_FTU_SHOWN, true)
            }

            picker.show(supportFragmentManager, "CURRENCY_PICKER")
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    override fun onAllSupportedCoinsLoaded() {
        startActivity(HomeActivity.buildLaunchIntent(this))
        finish()
    }

    private inner class IntroAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private var currentFragment: Fragment? = null

        fun getCurrentFragment(): Fragment? {
            return currentFragment
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    val newInstance = IntroFragment.newInstance(
                        R.raw.smiley_stack, getString(R.string.intro_coin_title),
                        getString(R.string.intro_coin_message), position, false
                    )
                    currentFragment = newInstance
                    newInstance
                }

                1 -> {
                    val newInstance = IntroFragment.newInstance(
                        R.raw.graph, getString(R.string.intro_track_title),
                        getString(R.string.intro_track_message), position, false
                    )
                    currentFragment = newInstance
                    newInstance
                }

                else -> {
                    val newInstance = IntroFragment.newInstance(
                        R.raw.lock, getString(R.string.intro_secure_title),
                        getString(R.string.intro_secure_message), position, true
                    )
                    currentFragment = newInstance
                    newInstance
                }
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }
}
