import com.infusiblecoder.cryptotracker.data.database.entities.WatchedCoin
import com.infusiblecoder.cryptotracker.features.BaseView



interface CoinDetailsPagerContract {

    interface View : BaseView {
        fun onWatchedCoinsLoaded(watchedCoinList: List<WatchedCoin>?)
    }

    interface Presenter {
        fun loadWatchedCoins()
    }
}
