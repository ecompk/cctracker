package com.infusiblecoder.cryptotracker.featurecomponents.cryptonewsmodule

import CryptoNewsContract
import com.infusiblecoder.cryptotracker.features.BasePresenter
import kotlinx.coroutines.launch
import timber.log.Timber



class CryptoNewsPresenter(private val cryptoNewsRepository: CryptoNewsRepository) :
    BasePresenter<CryptoNewsContract.View>(), CryptoNewsContract.Presenter {

    /**
     * Load the crypto news from the crypto panic api
     */
    override fun getCryptoNews(coinSymbol: String) {
        try {
            currentView?.showOrHideLoadingIndicator(true)

            launch {
                try {
                    val cryptoPanicNews = cryptoNewsRepository.getCryptoPanicNews(coinSymbol)
                    currentView?.onNewsLoaded(cryptoPanicNews)
                } catch (ex: Exception) {
                    Timber.e(ex.localizedMessage)
                } finally {
                    currentView?.showOrHideLoadingIndicator(false)
                }
            }
        } catch (e: Exception) {
        }
    }
}
