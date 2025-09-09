package com.infusiblecoder.cryptotracker.viewmodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.ContentLoadingProgressBar
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.infusiblecoder.cryptotracker.R
import com.infusiblecoder.cryptotracker.featurecomponents.ModuleItem
import com.infusiblecoder.cryptotracker.models.CryptoCompareNews

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class ShortNewsItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val pbLoading: ContentLoadingProgressBar
    private val tvNewsTitle: TextView
    private val clNewsArticleContainer: View

    init {
        View.inflate(context, R.layout.dashboard_news_module, this)
        pbLoading = findViewById(R.id.pbLoading)
        tvNewsTitle = findViewById(R.id.tvNewsTitle)
        clNewsArticleContainer = findViewById(R.id.clNewsArticleContainer)
    }

    @TextProp
    fun setNewsDate(news: CharSequence) {
        pbLoading.visibility = View.GONE
        tvNewsTitle.text = news
    }

    @CallbackProp
    fun setItemClickListener(listener: OnClickListener?) {
        clNewsArticleContainer.setOnClickListener(listener)
    }

    data class ShortNewsModuleData(val news: CryptoCompareNews) : ModuleItem
}
