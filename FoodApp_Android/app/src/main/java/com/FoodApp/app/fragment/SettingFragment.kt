package com.FoodApp.app.fragment

import android.content.Intent
import android.net.Uri
import android.view.View
import com.FoodApp.app.R
import com.FoodApp.app.activity.ChangePasswordActivity
import com.FoodApp.app.activity.DashboardActivity
import com.FoodApp.app.activity.EditProfileActivity
import com.FoodApp.app.api.ApiClient
import com.FoodApp.app.base.BaseFragmnet
import kotlinx.android.synthetic.main.fragment_home.ivMenu
import kotlinx.android.synthetic.main.fragment_setting.*


class SettingFragment: BaseFragmnet() {
    override fun setView(): Int {
        return R.layout.fragment_setting
    }
    override fun Init(view: View) {
        ivMenu.setOnClickListener {
            (activity as DashboardActivity?)!!.onDrawerToggle()
        }

        cvBtnEditProfile.setOnClickListener {
            openActivity(EditProfileActivity::class.java)
        }

        cvBtnPassword.setOnClickListener {
            openActivity(ChangePasswordActivity::class.java)
        }

        cvPolicy.setOnClickListener {
            val httpIntent = Intent(Intent.ACTION_VIEW)
            httpIntent.data = Uri.parse(ApiClient.PrivicyPolicy)
            startActivity(httpIntent)
        }
    }


}