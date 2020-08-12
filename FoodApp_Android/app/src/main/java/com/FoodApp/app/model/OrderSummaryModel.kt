package com.FoodApp.app.model

class OrderSummaryModel {
    private var total_price: String? = null

    private var item_id: String? = null

    private var item_price: String? = null

    private var qty: String? = null

    private var item_name: String? = null

    private var id: String? = null

    fun getTotal_price(): String? {
        return total_price
    }

    fun setTotal_price(total_price: String?) {
        this.total_price = total_price
    }

    fun getItem_id(): String? {
        return item_id
    }

    fun setItem_id(item_id: String?) {
        this.item_id = item_id
    }

    fun getItem_price(): String? {
        return item_price
    }

    fun setItem_price(item_price: String?) {
        this.item_price = item_price
    }

    fun getQty(): String? {
        return qty
    }

    fun setQty(qty: String?) {
        this.qty = qty
    }

    fun getItem_name(): String? {
        return item_name
    }

    fun setItem_name(item_name: String?) {
        this.item_name = item_name
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }
}