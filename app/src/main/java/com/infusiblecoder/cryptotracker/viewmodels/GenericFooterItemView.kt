package com.infusiblecoder.cryptotracker.viewmodels

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.infusiblecoder.cryptotracker.R
import com.infusiblecoder.cryptotracker.databinding.DashboardCoinModuleBinding
import com.infusiblecoder.cryptotracker.databinding.GenericFooterModuleBinding
import com.infusiblecoder.cryptotracker.featurecomponents.ModuleItem
import com.infusiblecoder.cryptotracker.utils.openCustomTab

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class GenericFooterItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attributeSet, defStyle) {

    private var binding: GenericFooterModuleBinding


    init {
        // Initialize ViewBinding
        binding = GenericFooterModuleBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
    }

    @ModelProp(options = [ModelProp.Option.IgnoreRequireHashCode])
    fun setFooterContent(footerModuleData: FooterModuleData) {
        binding.tvFooter.text = footerModuleData.footerText

        if (footerModuleData.footerUrlLink.isNotEmpty()) {
            binding.clFooter.setOnClickListener {
                openCustomTab(footerModuleData.footerUrlLink, context)
            }
        } else {
            binding.tvFooter.visibility = View.GONE
        }
    }

    data class FooterModuleData(val footerText: String = "", val footerUrlLink: String = "") : ModuleItem
}
