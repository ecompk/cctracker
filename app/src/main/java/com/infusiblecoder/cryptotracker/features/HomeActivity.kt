package com.infusiblecoder.cryptotracker.features

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.infusiblecoder.cryptotracker.R
import com.infusiblecoder.cryptotracker.databinding.ActivityHomeBinding
import com.infusiblecoder.cryptotracker.features.coinsearch.CoinDiscoveryFragment
import com.infusiblecoder.cryptotracker.features.dashboard.CoinDashboardFragment
import com.infusiblecoder.cryptotracker.features.settings.SettingsFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.infusiblecoder.cryptotracker.utils.AdsManager
import com.suddenh4x.ratingdialog.AppRating
import com.suddenh4x.ratingdialog.preferences.RatingThreshold

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    companion object {
        @JvmStatic
        fun buildLaunchIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }

        const val FRAGMENT_HOME = "FRAGMENT_HOME"
        const val FRAGMENT_OTHER = "FRAGMENT_OTHER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        switchToDashboard(savedInstanceState)

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.actionHome -> {
                    switchToDashboard(savedInstanceState)
                }

                R.id.actionSearch -> {
                    switchToSearch(savedInstanceState)
                }

                R.id.actionSettings -> {
                    switchToSettings(savedInstanceState)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                finish()
            } else if (!supportFragmentManager.fragments.isNullOrEmpty()) {
                when (supportFragmentManager.fragments[0]) {
                    is CoinDashboardFragment -> binding.bottomNavigation.menu.getItem(0).isChecked = true
                    is CoinDiscoveryFragment -> binding.bottomNavigation.menu.getItem(1).isChecked = true
                    is SettingsFragment -> binding.bottomNavigation.menu.getItem(2).isChecked = true
                }
            }
        }

        FirebaseCrashlytics.getInstance().log("HomeScreen")

        AppRating.Builder(this)
            .setMinimumLaunchTimes(5)
            .setMinimumDays(7)
            .setMinimumLaunchTimesToShowAgain(5)
            .setMinimumDaysToShowAgain(10)
            .setRatingThreshold(RatingThreshold.FOUR)
            .showIfMeetsConditions()

        AdsManager.loadBannerAd(this@HomeActivity, binding.bannerContainer)
    }

    private fun switchToDashboard(savedInstanceState: Bundle?) {

        val coinDashboardFragment = supportFragmentManager.findFragmentByTag(CoinDashboardFragment.TAG)
            ?: CoinDashboardFragment()

        supportFragmentManager.popBackStack(FRAGMENT_OTHER, POP_BACK_STACK_INCLUSIVE)

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerLayout, coinDashboardFragment, CoinDashboardFragment.TAG)
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .addToBackStack(FRAGMENT_HOME)
            .commit()
    }

    private fun switchToSearch(savedInstanceState: Bundle?) {

        val coinDiscoveryFragment = supportFragmentManager.findFragmentByTag(CoinDiscoveryFragment.TAG)
            ?: CoinDiscoveryFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerLayout, coinDiscoveryFragment, CoinDiscoveryFragment.TAG)
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .addToBackStack(FRAGMENT_OTHER)
            .commit()
    }

    private fun switchToSettings(savedInstanceState: Bundle?) {

        val settingsFragment = supportFragmentManager.findFragmentByTag(SettingsFragment.TAG)
            ?: SettingsFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerLayout, settingsFragment, SettingsFragment.TAG)
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .addToBackStack(FRAGMENT_OTHER)
            .commit()
    }
}
