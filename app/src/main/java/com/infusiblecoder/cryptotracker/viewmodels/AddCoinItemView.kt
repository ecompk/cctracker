package com.infusiblecoder.cryptotracker.viewmodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelView
import com.infusiblecoder.cryptotracker.R
import com.infusiblecoder.cryptotracker.featurecomponents.ModuleItem

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class AddCoinItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val addCoinCard: View

    init {
        View.inflate(context, R.layout.dashboard_new_coin_module, this)
        addCoinCard = findViewById(R.id.addCoinCard)
    }

    @CallbackProp
    fun setAddCoinClickListener(listener: OnClickListener?) {
        addCoinCard.setOnClickListener(listener)
    }

    object AddCoinModuleItem : ModuleItem
}
