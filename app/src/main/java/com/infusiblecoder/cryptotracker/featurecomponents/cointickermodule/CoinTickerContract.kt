import com.infusiblecoder.cryptotracker.features.BaseView
import com.infusiblecoder.cryptotracker.models.CryptoTicker


interface CoinTickerContract {

    interface View : BaseView {
        fun showOrHideLoadingIndicatorForTicker(showLoading: Boolean = true)
        fun onPriceTickersLoaded(tickerData: List<CryptoTicker>)
    }

    interface Presenter {
        fun getCryptoTickers(coinName: String)
    }
}
