package com.FoodApp.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Environment
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.webkit.URLUtil
import android.widget.TextView
import android.widget.Toast
import com.FoodApp.app.R
import com.FoodApp.app.activity.LoginActivity
import com.FoodApp.app.utils.SharePreference.Companion.getStringPref
import com.FoodApp.app.utils.SharePreference.Companion.isCurrancy
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.dlg_setting.view.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

object Common {
    var isProfileEdit:Boolean=false
    var isProfileMainEdit:Boolean=false
    var isUploadTrue:Boolean=false

    fun getToast(activity: Activity, strTxtToast: String) {
        Toast.makeText(activity, strTxtToast, Toast.LENGTH_SHORT).show()
    }

    fun getLog(strKey: String, strValue: String) {
        Log.e(">>>---  $strKey  ---<<<", strValue)
    }

    fun isValidEmail(strPattern: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(strPattern).matches();
    }

    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun isValidUrl(urlString: String): Boolean {
        try {
            val url = URL(urlString)
            return URLUtil.isValidUrl(url.toString()) && Patterns.WEB_URL.matcher(url.toString())
                .matches()
        } catch (e: MalformedURLException) {
        }
        return false
    }

    fun isCheckNetwork(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun openActivity(activity: Activity, destinationClass: Class<*>?) {
        activity.startActivity(Intent(activity, destinationClass))
        activity.overridePendingTransition(R.anim.fad_in, R.anim.fad_out)
    }

    open var dialog: Dialog? = null

    open fun dismissLoadingProgress() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
            dialog = null
        }
    }

    open fun showLoadingProgress(context: Activity) {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setContentView(R.layout.dlg_progress)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    fun alertErrorOrValidationDialog(act: Activity, msg: String?) {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
                dialog = null
            }
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val m_inflater = LayoutInflater.from(act)
            val m_view = m_inflater.inflate(R.layout.dlg_validation, null, false)
            val textDesc: TextView = m_view.findViewById(R.id.tvMessage)
            textDesc.text = msg
            val tvOk: TextView = m_view.findViewById(R.id.tvOk)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(m_view)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun settingDialog(act: Activity) {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
                dialog = null
            }
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val m_inflater = LayoutInflater.from(act)
            val m_view = m_inflater.inflate(R.layout.dlg_setting, null, false)

            val finalDialog: Dialog = dialog
            m_view.tvOkSetting.setOnClickListener {
                var i = Intent()
                i.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                i.addCategory(Intent.CATEGORY_DEFAULT)
                i.setData(android.net.Uri.parse("package:" + act.getPackageName()))
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                act.startActivity(i)
                dialog.dismiss()
                finalDialog.dismiss()
            }
            dialog.setContentView(m_view)
            if (!act.isFinishing) dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun setLogout(activity: Activity) {
        val isTutorialsActivity:Boolean=SharePreference.getBooleanPref(activity!!,SharePreference.isTutorial)
        val preference = SharePreference(activity)
        preference.mLogout()
        SharePreference.setBooleanPref(activity,SharePreference.isTutorial,isTutorialsActivity)
        val intent = Intent(activity, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.startActivity(intent);
        activity.finish()
    }

    @SuppressLint("NewApi", "SimpleDateFormat")
    fun getDayAndMonth(strDate: String): String {
        val sd = SimpleDateFormat("dd-MM-yyyy")
        val sdout = SimpleDateFormat("dd-MMMM-yyyy")
        val sdday = SimpleDateFormat("EEEE")
        val date: Date = sd.parse(strDate)!!
        val getDay = sdday.format(date)
        val getDate = sdout.format(date)
        val stringArray = getDate.split("-").toTypedArray()
        val strDay = stringArray.get(0).plus("th")
        return getDay.plus(" ".plus(stringArray.get(1))).plus(" ".plus(strDay))
    }

    fun setImageUpload(strParameter:String,mSelectedFileImg: File): MultipartBody.Part{
      return  MultipartBody.Part.createFormData(strParameter, mSelectedFileImg.getName(), RequestBody.create("image/*".toMediaType(), mSelectedFileImg))
    }

    fun setRequestBody(bodyData:String):RequestBody{
        return bodyData.toRequestBody("text/plain".toMediaType())
    }

     var mProgressDialog: ProgressDialog? = null
    fun showLoading(message: String,activity: Activity) {
        mProgressDialog = ProgressDialog(activity)
        mProgressDialog!!.setMessage(message)
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.show()
    }

     fun hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }

    fun getCacheFolder(context: Context): File? {
        var cacheDir: File? = null
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            cacheDir = File(
                Environment.getExternalStorageDirectory(),
                "gravityinfotech"
            )
            if (!cacheDir.isDirectory) {
                cacheDir.mkdirs()
            }
        }
        if (!cacheDir!!.isDirectory) {
            cacheDir = context.cacheDir //get system cache folder
        }
        return cacheDir
    }

    fun setCommanLogin(activity: Activity){
        val intent = Intent(activity, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.startActivity(intent)
        activity.finish()
        activity.finishAffinity()
    }

   fun getFcmToken():String{
       var token=""
       FirebaseInstanceId.getInstance().instanceId
           .addOnCompleteListener(OnCompleteListener { task ->
               if (!task.isSuccessful) {
                   return@OnCompleteListener
               }
               token = task.result?.token!!
           })
       return token
   }

   fun getCurrancy(act:Activity):String{
       return getStringPref(act,isCurrancy)!!
   }


}