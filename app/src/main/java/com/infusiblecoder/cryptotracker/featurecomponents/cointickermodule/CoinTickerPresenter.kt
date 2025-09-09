package com.infusiblecoder.cryptotracker.featurecomponents.cointickermodule

import CoinTickerContract
import com.infusiblecoder.cryptotracker.R
import com.infusiblecoder.cryptotracker.features.BasePresenter
import com.infusiblecoder.cryptotracker.utils.resourcemanager.AndroidResourceManager
import kotlinx.coroutines.launch
import timber.log.Timber



class CoinTickerPresenter(
    private val coinTickerRepository: CoinTickerRepository,
    private val androidResourceManager: AndroidResourceManager
) : BasePresenter<CoinTickerContract.View>(), CoinTickerContract.Presenter {

    /**
     * Load the crypto ticker from the crypto panic api
     */
    override fun getCryptoTickers(coinName: String) {
        try {
            var updatedCoinName = coinName

            if (coinName.equals("XRP", true)) {
                updatedCoinName = "ripple"
            }

            currentView?.showOrHideLoadingIndicatorForTicker(true)

            launch {
                try {
                        val cryptoTickers = coinTickerRepository.getCryptoTickers(updatedCoinName)
                    if (cryptoTickers != null) {
                        currentView?.onPriceTickersLoaded(cryptoTickers)
                    }
                } catch (ex: Exception) {
                    Timber.e(ex.localizedMessage)
                    currentView?.onNetworkError(androidResourceManager.getString(R.string.error_ticker))
                } finally {
                    currentView?.showOrHideLoadingIndicatorForTicker(false)
                }
            }
        } catch (e: Exception) {
        }
    }
}
