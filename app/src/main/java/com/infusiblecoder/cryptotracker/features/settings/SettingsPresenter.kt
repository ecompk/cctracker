package com.infusiblecoder.cryptotracker.features.settings

import SettingsContract
import com.infusiblecoder.cryptotracker.data.database.entities.WatchedCoin
import com.infusiblecoder.cryptotracker.features.BasePresenter
import com.infusiblecoder.cryptotracker.features.CryptoCompareRepository
import com.infusiblecoder.cryptotracker.models.getCoinFromCCCoin
import com.infusiblecoder.cryptotracker.utils.defaultExchange
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale



class SettingsPresenter(
    private val coinRepo: CryptoCompareRepository
) : BasePresenter<SettingsContract.View>(), SettingsContract.Presenter {

    override fun refreshCoinList(defaultCurrency: String) {
        launch {
            try {
                val allCoinsFromAPI = coinRepo.getAllCoinsFromAPI()
                val coinList: MutableList<WatchedCoin> = mutableListOf()
                val ccCoinList = allCoinsFromAPI.first
                ccCoinList.forEach { ccCoin ->
                    val coinInfo = allCoinsFromAPI.second[ccCoin.symbol.lowercase(Locale.US)]
                    coinList.add(getCoinFromCCCoin(ccCoin, defaultExchange, defaultCurrency, coinInfo))
                }
                Timber.d("Inserted all coins in db with size ${coinList.size}")
                currentView?.onCoinListRefreshed()
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
                currentView?.onNetworkError(ex.localizedMessage ?: "")
            }
        }
    }

    override fun refreshExchangeList() {
        launch {
            try {
                coinRepo.insertExchangeIntoList(coinRepo.getExchangeInfo())
                Timber.d("all exchanges loaded and inserted into db")
                currentView?.onExchangeListRefreshed()
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
                currentView?.onNetworkError(ex.localizedMessage ?: "")
            }
        }
    }
}
