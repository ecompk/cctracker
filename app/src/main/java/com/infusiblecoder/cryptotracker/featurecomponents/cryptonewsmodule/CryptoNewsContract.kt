import com.infusiblecoder.cryptotracker.features.BaseView
import com.infusiblecoder.cryptotracker.models.CryptoPanicNews



interface CryptoNewsContract {

    interface View : BaseView {
        fun showOrHideLoadingIndicator(showLoading: Boolean = true)
        fun onNewsLoaded(cryptoPanicNews: CryptoPanicNews)
    }

    interface Presenter {
        fun getCryptoNews(coinSymbol: String)
    }
}
