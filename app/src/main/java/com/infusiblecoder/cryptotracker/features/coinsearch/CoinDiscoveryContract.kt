import com.infusiblecoder.cryptotracker.features.BaseView
import com.infusiblecoder.cryptotracker.models.CoinPair
import com.infusiblecoder.cryptotracker.models.CoinPrice
import com.infusiblecoder.cryptotracker.models.CryptoCompareNews



interface CoinDiscoveryContract {

    interface View : BaseView {
        fun onTopCoinsByTotalVolumeLoaded(topCoins: List<CoinPrice>)
        fun onTopCoinListByPairVolumeLoaded(topPair: List<CoinPair>)
        fun onCoinNewsLoaded(coinNews: List<CryptoCompareNews>)
    }

    interface Presenter {
        fun getTopCoinListByMarketCap(toCurrencySymbol: String)
        fun getTopCoinListByPairVolume()
        fun getCryptoCurrencyNews()
    }
}
