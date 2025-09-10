import com.infusiblecoder.cryptotracker.data.database.entities.CoinTransaction
import com.infusiblecoder.cryptotracker.data.database.entities.WatchedCoin
import com.infusiblecoder.cryptotracker.features.BaseView
import com.infusiblecoder.cryptotracker.models.CoinPrice
import com.infusiblecoder.cryptotracker.models.CryptoCompareNews



interface CoinDashboardContract {

    interface View : BaseView {
        fun onWatchedCoinsAndTransactionsLoaded(watchedCoinList: List<WatchedCoin>, coinTransactionList: List<CoinTransaction>)
        fun onCoinPricesLoaded(coinPriceListMap: HashMap<String, CoinPrice>)
        fun onTopCoinsByTotalVolumeLoaded(topCoins: List<CoinPrice>)
        fun onCoinNewsLoaded(coinNews: List<CryptoCompareNews>)
    }

    interface Presenter {
        fun loadWatchedCoinsAndTransactions()
        fun loadCoinsPrices(fromCurrencySymbol: String, toCurrencySymbol: String)
        fun getTopCoinsByTotalVolume24hours(toCurrencySymbol: String)
        fun getLatestNewsFromCryptoCompare()
    }
}
