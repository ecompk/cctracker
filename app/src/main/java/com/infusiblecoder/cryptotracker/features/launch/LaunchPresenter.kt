package com.infusiblecoder.cryptotracker.features.launch

import LaunchContract
import com.infusiblecoder.cryptotracker.data.database.entities.WatchedCoin
import com.infusiblecoder.cryptotracker.features.BasePresenter
import com.infusiblecoder.cryptotracker.features.CryptoCompareRepository
import com.infusiblecoder.cryptotracker.features.getTop5CoinsToWatch
import com.infusiblecoder.cryptotracker.models.CCCoin
import com.infusiblecoder.cryptotracker.models.CoinInfo
import com.infusiblecoder.cryptotracker.models.getCoinFromCCCoin
import com.infusiblecoder.cryptotracker.utils.defaultExchange
import kotlinx.coroutines.launch
import timber.log.Timber



class LaunchPresenter(
    private val coinRepo: CryptoCompareRepository
) : BasePresenter<LaunchContract.View>(), LaunchContract.Presenter {

    private var coinList: ArrayList<CCCoin>? = null
    private var coinInfoMap: Map<String, CoinInfo>? = null

    override fun loadAllCoins() {
        launch {
            try {
                val allCoinsFromAPI = coinRepo.getAllCoinsFromAPI(coinList, coinInfoMap)
                coinList = allCoinsFromAPI.first
                coinInfoMap = allCoinsFromAPI.second
                currentView?.onCoinsLoaded()
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }

        loadExchangeFromAPI()
    }

    private fun loadExchangeFromAPI() {
        launch {
            try {
                coinRepo.insertExchangeIntoList(coinRepo.getExchangeInfo())
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }

    override fun getAllSupportedCoins(defaultCurrency: String) {
        launch {
            try {
                val allCoinsFromAPI = coinRepo.getAllCoinsFromAPI(coinList, coinInfoMap)
                val coinList: MutableList<WatchedCoin> = mutableListOf()
                val ccCoinList = allCoinsFromAPI.first

                ccCoinList.forEach { ccCoin ->
                    val coinInfo = allCoinsFromAPI.second[ccCoin.symbol.toLowerCase()]
                    coinList.add(getCoinFromCCCoin(ccCoin, defaultExchange, defaultCurrency, coinInfo))
                }

                coinRepo.insertCoinsInWatchList(coinList)

                val top5CoinsToWatch = getTop5CoinsToWatch()
                top5CoinsToWatch.forEach { coinId ->
                    coinRepo.updateCoinWatchedStatus(true, coinId)
                }

                Timber.d("Loaded all the coins and inserted in DB")
                currentView?.onAllSupportedCoinsLoaded()
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }
}
