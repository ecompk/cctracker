package com.infusiblecoder.cryptotracker.features.transaction

import CoinTransactionContract
import com.infusiblecoder.cryptotracker.data.database.entities.CoinTransaction
import com.infusiblecoder.cryptotracker.features.BasePresenter
import com.infusiblecoder.cryptotracker.features.CryptoCompareRepository
import kotlinx.coroutines.launch
import timber.log.Timber



class CoinTransactionPresenter(
    private val coinRepo: CryptoCompareRepository
) : BasePresenter<CoinTransactionContract.View>(), CoinTransactionContract.Presenter {

    override fun getAllSupportedExchanges() {
        launch {
            try {
                currentView?.onAllSupportedExchangesLoaded(coinRepo.getAllSupportedExchanges())
                Timber.d("All Exchange Loaded")
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }

    // to coins is , separated multiple coin list.
    override fun getPriceForPair(fromCoin: String, toCoin: String, exchange: String, timeStamp: String) {
        if (exchange.isNotEmpty()) {
            launch {
                try {
                    currentView?.onCoinPriceLoaded(coinRepo.getCoinPriceForTimeStamp(fromCoin, toCoin, exchange, timeStamp))
                } catch (ex: Exception) {
                    Timber.e(ex.localizedMessage)
                }
            }
        }
    }

    override fun addTransaction(transaction: CoinTransaction) {
        launch {
            try {
                coinRepo.insertTransaction(transaction)
                Timber.d("Coin Transaction Added")
                currentView?.onTransactionAdded()
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }
}
