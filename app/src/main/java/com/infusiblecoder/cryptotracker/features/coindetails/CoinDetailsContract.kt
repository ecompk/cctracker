import com.infusiblecoder.cryptotracker.data.database.entities.WatchedCoin
import com.infusiblecoder.cryptotracker.features.BaseView



interface CoinDetailsContract {

    interface View : BaseView {
        fun showOrHideLoadingIndicator(showLoading: Boolean = true)
        fun onWatchedCoinLoaded(coin: WatchedCoin?)
    }

    interface Presenter {
        fun getWatchedCoinFromSymbol(symbol: String)
    }
}
