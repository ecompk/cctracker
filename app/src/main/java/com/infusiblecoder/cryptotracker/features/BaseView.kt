package com.infusiblecoder.cryptotracker.features


interface BaseView {

    /**
     * Callback to signal a network error
     **/
    fun onNetworkError(errorMessage: String)
}
