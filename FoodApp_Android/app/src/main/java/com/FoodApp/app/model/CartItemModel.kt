package com.FoodApp.app.model

class CartItemModel {
    private var itemimage: FoodItemImageModel? = null
    private var item_id: String? = null
    private var price: String? = null
    private var qty: String? = null
    private var item_name: String? = null
    private var id: String? = null

    fun getItemimage(): FoodItemImageModel? {
        return itemimage
    }

    fun setItemimage(itemimage: FoodItemImageModel?) {
        this.itemimage = itemimage
    }

    fun getItem_id(): String? {
        return item_id
    }

    fun setItem_id(item_id: String?) {
        this.item_id = item_id
    }

    fun getPrice(): String? {
        return price
    }

    fun setPrice(price: String?) {
        this.price = price
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