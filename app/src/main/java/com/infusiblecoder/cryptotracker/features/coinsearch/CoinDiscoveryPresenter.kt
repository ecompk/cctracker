package com.infusiblecoder.cryptotracker.features.coinsearch

import CoinDiscoveryContract
import com.infusiblecoder.cryptotracker.features.BasePresenter
import com.infusiblecoder.cryptotracker.features.CryptoCompareRepository
import kotlinx.coroutines.launch
import timber.log.Timber



class CoinDiscoveryPresenter(
    private val coinRepo: CryptoCompareRepository
) : BasePresenter<CoinDiscoveryContract.View>(),
    CoinDiscoveryContract.Presenter {

    override fun getTopCoinListByMarketCap(toCurrencySymbol: String) {
        launch {
            try {
                currentView?.onTopCoinsByTotalVolumeLoaded(coinRepo.getTopCoinsByTotalVolume(toCurrencySymbol))
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }

    override fun getTopCoinListByPairVolume() {

        launch {
            try {
                currentView?.onTopCoinListByPairVolumeLoaded(coinRepo.getTopPairsByTotalVolume("BTC"))
                Timber.d("Top coins by pair Loaded")
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }

    override fun getCryptoCurrencyNews() {
        launch {
            try {
                val topNewsFromCryptoCompare = coinRepo.getTopNewsFromCryptoCompare()
                currentView?.onCoinNewsLoaded(topNewsFromCryptoCompare)
                Timber.d("All news Loaded")
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }
}
