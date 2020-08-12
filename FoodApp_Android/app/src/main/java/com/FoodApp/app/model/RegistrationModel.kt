package com.FoodApp.app.model

class RegistrationModel {
    private var profile_image: String? = null

    private var password: String? = null

    private var name: String? = null

    private var mobile: String? = null

    private var id: String? = null

    private var type: String? = null

    private var email: String? = null

    fun getProfile_image(): String? {
        return profile_image
    }

    fun setProfile_image(profile_image: String?) {
        this.profile_image = profile_image
    }

    fun getPassword(): String? {
        return password
    }

    fun setPassword(password: String?) {
        this.password = password
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getMobile(): String? {
        return mobile
    }

    fun setMobile(mobile: String?) {
        this.mobile = mobile
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getType(): String? {
        return type
    }

    fun setType(type: String?) {
        this.type = type
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }
}