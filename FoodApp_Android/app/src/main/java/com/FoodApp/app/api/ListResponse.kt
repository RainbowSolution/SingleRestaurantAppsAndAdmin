package com.FoodApp.app.api

class ListResponse<T> {
    private var data: ArrayList<T>?=null

    private var message: String? = null

    private var status: String? = null

    fun getData(): ArrayList<T> {
        return data!!
    }

    fun setData(data: ArrayList<T>) {
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
}