package com.FoodApp.app.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.FoodApp.app.R
import com.FoodApp.app.base.BaseActivity
import com.FoodApp.app.model.ProfileModel
import com.FoodApp.app.utils.Common
import com.FoodApp.app.utils.SharePreference
import com.FoodApp.app.utils.SharePreference.Companion.getStringPref
import com.FoodApp.app.utils.SharePreference.Companion.setStringPref
import com.FoodApp.app.utils.SharePreference.Companion.userName
import com.FoodApp.app.utils.SharePreference.Companion.userProfile
import com.FoodApp.app.api.*
import com.FoodApp.app.fragment.*
import kotlinx.android.synthetic.main.dlg_confomation.view.*
import kotlinx.android.synthetic.main.dlg_logout.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity: BaseActivity() {
    var drawer_layout: DrawerLayout? = null
    var nav_view: LinearLayout? = null
    var tvName: TextView?=null
    var ivProfile: ImageView?=null
    var temp=1
    var dataResponse: ProfileModel?=null
    override fun setLayout(): Int {
        return R.layout.activity_dashboard
    }

    override fun InitView() {
        drawer_layout = findViewById(R.id.drawer_layout)
        nav_view = findViewById(R.id.nav_view)
        tvName=drawer_layout!!.findViewById(R.id.tv_NevProfileName)!!
        ivProfile=drawer_layout!!.findViewById(R.id.ivProfile)!!

        if(intent.getStringExtra("pos")!=null){
            setFragment(intent.getStringExtra("pos")!!.toInt())
            temp=intent.getStringExtra("pos")!!.toInt()
        }else{
            if(SharePreference.getBooleanPref(this@DashboardActivity,SharePreference.isLogin)){
                if (Common.isCheckNetwork(this@DashboardActivity)) {
                    val hasmap = HashMap<String, String>()
                    hasmap.put("user_id", SharePreference.getStringPref(this@DashboardActivity, SharePreference.userId)!!)
                    callApiProfile(hasmap,false)
                } else {
                    Common.alertErrorOrValidationDialog(
                        this@DashboardActivity,
                        resources.getString(R.string.no_internet)
                    )
                }
            }
        }

    }


    open fun onDrawerToggle() {
        drawer_layout!!.openDrawer(nav_view!!)
    }

    override fun onBackPressed() {
        mExitDialog()
    }

    fun onClick(v: View?) {
        when (v!!.id) {
            R.id.rl_home -> {
                drawer_layout!!.closeDrawers()
                if(temp!=1){
                    setFragment(1)
                    temp=1
                }
            }
            R.id.rl_category->{
                drawer_layout!!.closeDrawers()
                if(temp!=2){
                    setFragment(2)
                    temp=2
                }
            }
            R.id.rl_favourite->{
                drawer_layout!!.closeDrawers()
                if(temp!=3){
                    setFragment(3)
                    temp=3
                }
            }
            R.id.rl_ratting -> {
                drawer_layout!!.closeDrawers()
                if(temp!=4){
                    setFragment(4)
                    temp=4
                }
            }
            R.id.rl_setting -> {
                drawer_layout!!.closeDrawers()
                if(temp!=5){
                    setFragment(5)
                    temp=5
                }

            }
            R.id.rl_Logout -> {
                drawer_layout!!.closeDrawers()
                if(temp!=6){
                    setFragment(6)
                    temp=6
                }
            }
        }
    }


    @SuppressLint("WrongConstant")
    fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.FramFragment, fragment)
        fragmentTransaction.addToBackStack(fragment.toString())
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
        fragmentTransaction.commit()
    }


    private fun callApiProfile(hasmap: HashMap<String, String>,isProfile:Boolean) {
        Common.showLoadingProgress(this@DashboardActivity)
        val call = ApiClient.getClient.getProfile(hasmap)
        call.enqueue(object : Callback<RestResponse<ProfileModel>> {
            override fun onResponse(call: Call<RestResponse<ProfileModel>>, response: Response<RestResponse<ProfileModel>>) {
                if (response.code() == 200) {
                    val restResponce: RestResponse<ProfileModel> = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        Common.dismissLoadingProgress()
                        if(isProfile){
                            Common.isProfileMainEdit=false
                        }
                        dataResponse = restResponce.getData()!!
                        setStringPref(this@DashboardActivity,userName, dataResponse!!.getName()!!)
                        setStringPref(this@DashboardActivity, userProfile, dataResponse!!.getProfile_image()!!)
                        setProfileData(isProfile)
                    } else if (restResponce.getData()!!.equals("0")) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@DashboardActivity,
                            restResponce.getMessage()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<ProfileModel>>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@DashboardActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun setProfileData(
        profile: Boolean
    ) {
        tvName!!.text = getStringPref(this@DashboardActivity,userName)
        Glide.with(this@DashboardActivity).load(getStringPref(this@DashboardActivity, userProfile))
            .placeholder(resources.getDrawable(R.drawable.ic_placeholder)).into(ivProfile!!)
        if(!profile){
            replaceFragment(HomeFragment())
        }
    }

    override fun onResume() {
        super.onResume()
        if(Common.isProfileMainEdit){
            if (Common.isCheckNetwork(this@DashboardActivity)) {
                val hasmap = HashMap<String, String>()
                hasmap.put("user_id", SharePreference.getStringPref(this@DashboardActivity, SharePreference.userId)!!)
                callApiProfile(hasmap,true)
            } else {
                Common.alertErrorOrValidationDialog(
                    this@DashboardActivity,
                    resources.getString(R.string.no_internet)
                )
            }
        }else{
            setProfileData(true)
        }
    }

    fun setFragment(pos:Int){
        when (pos){
            1->{
                replaceFragment(HomeFragment())
            }
            2->{
                replaceFragment(OrderHistoryFragment())
            }
            3->{
                replaceFragment(FavouriteFragment())
            }
            4->{
                replaceFragment(RattingFragment())
            }
            5->{
                replaceFragment(SettingFragment())
            }
            6->{
                alertLogOutDialog()
            }
        }
    }

    fun alertLogOutDialog() {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
                dialog = null
            }
            dialog = Dialog(this@DashboardActivity, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val m_inflater = LayoutInflater.from(this@DashboardActivity)
            val m_view = m_inflater.inflate(R.layout.dlg_logout, null, false)

            val finalDialog: Dialog = dialog
            m_view.tvLogout.setOnClickListener {
                finalDialog.dismiss()
                Common.setLogout(this@DashboardActivity)

            }
            m_view.tvCancel.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(m_view)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun mExitDialog() {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
                dialog = null
            }
            dialog = Dialog(this@DashboardActivity, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val m_inflater = LayoutInflater.from(this@DashboardActivity)
            val m_view = m_inflater.inflate(R.layout.dlg_confomation, null, false)

            val finalDialog: Dialog = dialog
            m_view.tvYes.setOnClickListener {
                finalDialog.dismiss()
                ActivityCompat.finishAfterTransition(this@DashboardActivity)
                ActivityCompat.finishAffinity(this@DashboardActivity);
                finish()
            }
            m_view.tvNo.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(m_view)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


}