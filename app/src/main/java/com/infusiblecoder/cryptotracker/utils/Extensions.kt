package com.infusiblecoder.cryptotracker.utils

import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.ColorInt


fun RadioGroup.changeChildrenColor(@ColorInt color: Int) {
    val childCount = this.childCount
    var i = 0
    while (i < childCount) {
        val radioButton = this.getChildAt(i) as RadioButton
        if (!radioButton.isChecked) {
            radioButton.setTextColor(color)
        }
        i++
    }
}
