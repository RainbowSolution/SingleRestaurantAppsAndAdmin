package com.FoodApp.app.activity

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.FoodApp.app.R
import com.FoodApp.app.base.BaseActivity
import com.FoodApp.app.utils.Common
import com.FoodApp.app.api.*
import kotlinx.android.synthetic.main.activity_forgetpassword.edEmail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class ForgetPasswordActivity:BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_forgetpassword
    }
    override fun InitView() {

    }

    fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.tvSubmit -> {
                if(edEmail.text.toString().equals("")){
                    Common.alertErrorOrValidationDialog(
                        this@ForgetPasswordActivity,
                        resources.getString(R.string.validation_email)
                    )
                }else if (!Common.isValidEmail(edEmail.text.toString())) {
                    Common.alertErrorOrValidationDialog(
                        this@ForgetPasswordActivity,
                        resources.getString(R.string.validation_valid_email)
                    )
                }else{
                    val hasmap = HashMap<String, String>()
                    hasmap.put("email",edEmail.text.toString())
                    if(Common.isCheckNetwork(this@ForgetPasswordActivity)){
                        callApiForgetpassword(hasmap)
                    }else{
                        Common.alertErrorOrValidationDialog(
                            this@ForgetPasswordActivity,
                            resources.getString(R.string.no_internet)
                        )
                    }
                }
            }
        }
    }

    private fun callApiForgetpassword(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ForgetPasswordActivity)
        val call = ApiClient.getClient.setforgotPassword(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(call: Call<SingleResponse>, response: Response<SingleResponse>) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.getStatus().equals("1")) {
                        Common.dismissLoadingProgress()
                        successfulDialog(
                            this@ForgetPasswordActivity,
                            restResponse.getMessage()
                        )
                    }
                }else{
                    Common.dismissLoadingProgress()
                    Common.dismissLoadingProgress()
                    Common.alertErrorOrValidationDialog(
                        this@ForgetPasswordActivity,
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ForgetPasswordActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun successfulDialog(act: Activity, msg: String?) {
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
                finish()
            }
            dialog.setContentView(m_view)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}