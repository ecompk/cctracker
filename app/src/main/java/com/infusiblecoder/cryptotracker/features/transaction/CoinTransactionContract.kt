import com.infusiblecoder.cryptotracker.data.database.entities.CoinTransaction
import com.infusiblecoder.cryptotracker.features.BaseView
import com.infusiblecoder.cryptotracker.models.ExchangePair
import java.math.BigDecimal
import java.util.HashMap



interface CoinTransactionContract {

    interface View : BaseView {
        fun onAllSupportedExchangesLoaded(exchangeCoinMap: HashMap<String, MutableList<ExchangePair>>)
        fun onCoinPriceLoaded(prices: MutableMap<String, BigDecimal>)
        fun onTransactionAdded()
    }

    interface Presenter {
        fun getAllSupportedExchanges()
        fun getPriceForPair(fromCoin: String, toCoin: String, exchange: String, timeStamp: String)
        fun addTransaction(transaction: CoinTransaction)
    }
}
