package com.FoodApp.app.api

import com.FoodApp.app.model.CurrencyModel

class RestResponse<T> {

    private var data:T?=null

    private var message: String? = null

    private var status: String? = null

    fun getData():T? {
        return data
    }

    fun setData(data:T?) {
        this.data = data
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    private var currency: CurrencyModel? = null

    fun getCurrency(): CurrencyModel? {
        return currency
    }

    fun setCurrency(currency: CurrencyModel?) {
        this.currency = currency
    }
}