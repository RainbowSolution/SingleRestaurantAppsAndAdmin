package com.FoodApp.app.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.FoodApp.app.R
import com.FoodApp.app.api.SingleResponse
import com.FoodApp.app.base.BaseActivity
import com.FoodApp.app.utils.Common.alertErrorOrValidationDialog
import com.FoodApp.app.utils.Common.dismissLoadingProgress
import com.FoodApp.app.utils.Common.isCheckNetwork
import com.FoodApp.app.utils.Common.showLoadingProgress
import com.FoodApp.app.utils.SharePreference.Companion.getStringPref
import com.FoodApp.app.utils.SharePreference.Companion.userEmail
import com.FoodApp.app.utils.SharePreference.Companion.userId
import com.FoodApp.app.utils.SharePreference.Companion.userMobile
import com.FoodApp.app.api.*
import com.FoodApp.app.utils.SharePreference
import com.FoodApp.app.utils.SharePreference.Companion.userName
import kotlinx.android.synthetic.main.activity_payment.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentPayActivity:BaseActivity(),PaymentResultListener {
    override fun setLayout(): Int {
       return R.layout.activity_payment
    }

    override fun InitView() {
        var strGetData="COD"
        Checkout.preload(this@PaymentPayActivity)
        tvCOD.setOnClickListener {
            setPaymentType(0)
            strGetData="COD"
        }
        tvRAZORPAY.setOnClickListener {
            setPaymentType(1)
            strGetData="ONLINE PAY"
        }
        ivBack.setOnClickListener {
            finish()
        }
        tvPaynow.setOnClickListener {
            if(strGetData.equals("COD")){
                if (isCheckNetwork(this@PaymentPayActivity)) {
                    showLoadingProgress(this@PaymentPayActivity)
                    val hasmap = HashMap<String, String>()
                    hasmap.put("user_id",getStringPref(this@PaymentPayActivity,userId)!!)
                    hasmap.put("order_total",intent.getStringExtra("getAmount")!!)
                    hasmap.put("razorpay_payment_id","")
                    hasmap.put("payment_type","0")
                    hasmap.put("address",intent.getStringExtra("getAddress")!!)
                    hasmap.put("promocode",intent.getStringExtra("promocode")!!)
                    hasmap.put("discount_amount",intent.getStringExtra("discount_amount")!!)
                    hasmap.put("discount_pr",intent.getStringExtra("discount_pr")!!)
                    hasmap.put("tax",intent.getStringExtra("getTax")!!)
                    hasmap.put("tax_amount",intent.getStringExtra("getTaxAmount")!!)
                    hasmap.put("delivery_charge",intent.getStringExtra("delivery_charge")!!)
                    callApiOrder(hasmap)
                } else {
                    alertErrorOrValidationDialog(
                        this@PaymentPayActivity,
                        resources.getString(R.string.no_internet)
                    )
                }
            }else  if(strGetData.equals("ONLINE PAY")){
                showLoadingProgress(this@PaymentPayActivity)
                startPayment()
            }
        }

    }

    override fun onBackPressed() {
        finish()
    }

    fun setPaymentType(type:Int){
        val color=resources.getColor(R.color.black)
        tvRAZORPAY.setTextColor(color)
        tvCOD.setTextColor(color)
        tvCOD.setBackgroundColor(Color.TRANSPARENT)
        tvRAZORPAY.setBackgroundColor(Color.TRANSPARENT)
        when(type){
            0->{
                tvCOD.setTextColor(resources.getColor(R.color.white))
                tvCOD.background=resources.getDrawable(R.drawable.bg_select_btn)
            }
            1->{
                tvRAZORPAY.setTextColor(resources.getColor(R.color.white))
                tvRAZORPAY.background=resources.getDrawable(R.drawable.bg_select_btn)
            }
        }
    }

    private fun startPayment() {
        val amount=intent.getStringExtra("getAmount")+"00"
        val activity:Activity = this
        val co = Checkout()
        try {
            val options = JSONObject()
            options.put("name",getStringPref(this@PaymentPayActivity,userName))
            options.put("description","Food App Payment pay now For Trusted")
            options.put("image", getStringPref(this@PaymentPayActivity,SharePreference.userProfile))
            options.put("currency","INR")
            options.put("amount",intent.getStringExtra("getAmount")+"00")
            val prefill = JSONObject()
            prefill.put("email",getStringPref(this@PaymentPayActivity,userEmail))
            prefill.put("contact",getStringPref(this@PaymentPayActivity,userMobile))
            options.put("prefill",prefill)
            val theme = JSONObject()
            theme.put("color","#FE734C")
            options.put("theme",theme)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        try{
            Toast.makeText(this,"Payment failed $errorCode \n $response",Toast.LENGTH_LONG).show()
            dismissLoadingProgress()
        }catch (e: Exception){
            Log.e("Exception","Exception in onPaymentSuccess", e)
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        try{
            val hasmap = HashMap<String, String>()
            hasmap.put("user_id",getStringPref(this@PaymentPayActivity,userId)!!)
            hasmap.put("order_total",intent.getStringExtra("getAmount")!!)
            hasmap.put("razorpay_payment_id",razorpayPaymentId!!)
            hasmap.put("payment_type","1")
            hasmap.put("address",intent.getStringExtra("getAddress")!!)
            hasmap.put("promocode",intent.getStringExtra("promocode")!!)
            hasmap.put("discount_amount",intent.getStringExtra("discount_amount")!!)
            hasmap.put("discount_pr",intent.getStringExtra("discount_pr")!!)
            hasmap.put("tax",intent.getStringExtra("getTax")!!)
            hasmap.put("tax_amount",intent.getStringExtra("getTaxAmount")!!)
            hasmap.put("delivery_charge",intent.getStringExtra("delivery_charge")!!)
            callApiOrder(hasmap)
        }catch (e: Exception){
            Log.e("Exception","Exception in onPaymentSuccess", e)
        }
    }

    fun callApiOrder(map:HashMap<String,String>){
        val call = ApiClient.getClient.setOrderPayment(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(call: Call<SingleResponse>, response: Response<SingleResponse>) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.getStatus().equals("1")) {
                        dismissLoadingProgress()
                        successfulDialog(
                            this@PaymentPayActivity,
                            restResponse.getMessage()
                        )
                    } else if (restResponse.getStatus().equals("0")) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@PaymentPayActivity,
                            restResponse.getMessage()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@PaymentPayActivity,
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
                startActivity(Intent(this@PaymentPayActivity,DashboardActivity::class.java).putExtra("pos","2"))
                finish()
                finishAffinity()
            }
            dialog.setContentView(m_view)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}