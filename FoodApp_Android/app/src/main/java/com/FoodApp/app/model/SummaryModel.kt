package com.FoodApp.app.model

class SummaryModel {
    private var delivery_charge: String? = null

    private var discount_amount: String? = null

    private var promocode: String? = null

    private var order_total: String? = null

    private var tax: String? = null

    fun getDelivery_charge(): String? {
        return delivery_charge
    }

    fun setDelivery_charge(delivery_charge: String?) {
        this.delivery_charge = delivery_charge
    }

    fun getDiscount_amount(): String? {
        return discount_amount
    }

    fun setDiscount_amount(discount_amount: String?) {
        this.discount_amount = discount_amount
    }

    fun getPromocode(): String? {
        return promocode
    }

    fun setPromocode(promocode: String?) {
        this.promocode = promocode
    }

    fun getOrder_total(): String? {
        return order_total
    }

    fun setOrder_total(order_total: String?) {
        this.order_total = order_total
    }

    fun getTax(): String? {
        return tax
    }

    fun setTax(tax: String?) {
        this.tax = tax
    }

}