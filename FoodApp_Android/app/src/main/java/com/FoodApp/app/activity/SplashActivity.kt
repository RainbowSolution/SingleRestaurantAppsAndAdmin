package com.FoodApp.app.activity

import android.os.Handler
import com.FoodApp.app.R
import com.FoodApp.app.base.BaseActivity
import com.FoodApp.app.utils.SharePreference
import com.FoodApp.app.utils.SharePreference.Companion.getBooleanPref


class SplashActivity : BaseActivity() {

    override fun setLayout(): Int {
       return R.layout.activity_splash
    }

    override fun InitView() {
      Handler().postDelayed({
          if(!getBooleanPref(this@SplashActivity,SharePreference.isTutorial)){
              openActivity(TutorialActivity::class.java)
              finish()
          }else{
              if(getBooleanPref(this@SplashActivity,SharePreference.isLogin)){
                  openActivity(DashboardActivity::class.java)
                  finish()

              }else{
                  openActivity(LoginActivity::class.java)
                  finish()

              }
          }
        },3000)
    }
}