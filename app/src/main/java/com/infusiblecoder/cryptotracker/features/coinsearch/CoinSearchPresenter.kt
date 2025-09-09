package com.infusiblecoder.cryptotracker.features.coinsearch

import CoinSearchContract
import com.infusiblecoder.cryptotracker.features.BasePresenter
import com.infusiblecoder.cryptotracker.features.CryptoCompareRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber



class CoinSearchPresenter(
    private val coinRepo: CryptoCompareRepository
) : BasePresenter<CoinSearchContract.View>(),
    CoinSearchContract.Presenter {

    override fun loadAllCoins() {
        currentView?.showOrHideLoadingIndicator(true)

        launch {
            coinRepo.getAllCoins()
                ?.catch {
                    Timber.e(it)
                    currentView?.onNetworkError(it.localizedMessage)
                }
                ?.collect {
                    Timber.d("All Coins Loaded")
                    currentView?.showOrHideLoadingIndicator(false)
                    currentView?.onCoinsLoaded(it)
                }
        }
    }

    override fun updateCoinWatchedStatus(watched: Boolean, coinID: String, coinSymbol: String) {
        launch {
            try {
                coinRepo.updateCoinWatchedStatus(watched, coinID)
                Timber.d("Coin status updated")
                currentView?.onCoinWatchedStatusUpdated(watched, coinSymbol)
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
                currentView?.onNetworkError(ex.localizedMessage ?: "Error")
            }
        }
    }
}
