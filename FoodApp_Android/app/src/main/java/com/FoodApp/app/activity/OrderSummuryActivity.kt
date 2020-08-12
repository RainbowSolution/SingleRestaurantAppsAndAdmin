package com.FoodApp.app.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.ClipboardManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.FoodApp.app.R
import com.FoodApp.app.api.ApiClient
import com.FoodApp.app.api.ListResponse
import com.FoodApp.app.api.RestResponse
import com.FoodApp.app.api.RestSummaryResponse
import com.FoodApp.app.base.BaseActivity
import com.FoodApp.app.base.BaseAdaptor
import com.FoodApp.app.model.GetPromocodeModel
import com.FoodApp.app.model.OrderSummaryModel
import com.FoodApp.app.model.PromocodeModel
import com.FoodApp.app.model.SummaryModel
import com.FoodApp.app.utils.Common
import com.FoodApp.app.utils.Common.alertErrorOrValidationDialog
import com.FoodApp.app.utils.Common.dismissLoadingProgress
import com.FoodApp.app.utils.SharePreference
import com.FoodApp.app.utils.SharePreference.Companion.getStringPref
import com.FoodApp.app.utils.SharePreference.Companion.isCurrancy
import com.FoodApp.app.utils.SharePreference.Companion.userId
import kotlinx.android.synthetic.main.activity_orderdetail.*
import kotlinx.android.synthetic.main.activity_yoursorderdetail.*
import kotlinx.android.synthetic.main.activity_yoursorderdetail.ivBack
import kotlinx.android.synthetic.main.activity_yoursorderdetail.rvOrderItemFood
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvDiscountOffer
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvOrderDeliveryCharge
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvOrderTaxPrice
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvOrderTotalCharge
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvOrderTotalPrice
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvPromoCodeApply
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvTitleTex
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrderSummuryActivity:BaseActivity() {
    var summaryModel=SummaryModel()
    var promocodeList:ArrayList<PromocodeModel>?=null
    var discountAmount=""
    var discountPer=""

    override fun setLayout(): Int {
        return R.layout.activity_yoursorderdetail
    }

    @SuppressLint("SetTextI18n")
    override fun InitView() {
        promocodeList=ArrayList()
       // rlApplyPromocode.visibility=View.GONE
        rlOffer.visibility=View.GONE
        if(Common.isCheckNetwork(this@OrderSummuryActivity)){
            callApiOrderSummary()
        }else{
            alertErrorOrValidationDialog(this@OrderSummuryActivity,resources.getString(R.string.no_internet))
        }

        tvProceedToPaymnet.setOnClickListener {
             if(edAddress.text.toString().equals("")){
                 alertErrorOrValidationDialog(
                     this@OrderSummuryActivity,
                     resources.getString(R.string.validation_address)
                 )
             }else{
                 val intent=Intent(this@OrderSummuryActivity,PaymentPayActivity::class.java)
                 val strTotalCharge=tvOrderTotalCharge.text.toString().replace("$","")
                 val orderTax:Int=(summaryModel.getOrder_total()!!.toInt()*summaryModel.getTax()!!.toInt())/100
                 intent.putExtra("getAmount",strTotalCharge)
                 intent.putExtra("getAddress",edAddress.text.toString())
                 intent.putExtra("getTax",summaryModel.getTax())
                 intent.putExtra("getTaxAmount",orderTax.toString())
                 intent.putExtra("delivery_charge",summaryModel.getDelivery_charge())
                 intent.putExtra("promocode",tvPromoCodeApply.text.toString())
                 intent.putExtra("discount_pr",discountPer)
                 intent.putExtra("discount_amount",discountAmount)
                 startActivity(intent)
             }
        }
        ivBack.setOnClickListener {
            finish()
        }


        tvbtnPromocode.setOnClickListener {
            if(Common.isCheckNetwork(this@OrderSummuryActivity)){
                callApiPromocode()
            }else{
                alertErrorOrValidationDialog(this@OrderSummuryActivity,resources.getString(R.string.no_internet))
            }
        }

        tvApply.setOnClickListener {
            if(tvApply.text.toString().equals("Apply")){
                if(!edPromocode.text.toString().equals("")){
                    callApiCheckPromocode()
                }
            }else if(tvApply.text.toString().equals("Remove")) {
                tvPromoCodeApply.text=""
                tvDiscountOffer.text=""
                edPromocode.setText("")
                tvApply.text="Apply"
                rlOffer.visibility=View.GONE
                val orderTax:Int=(summaryModel.getOrder_total()!!.toInt()*summaryModel.getTax()!!.toInt())/100
                tvOrderTotalPrice.text=getStringPref(this@OrderSummuryActivity,isCurrancy)+summaryModel.getOrder_total()
                tvOrderTaxPrice.text=getStringPref(this@OrderSummuryActivity,isCurrancy)+orderTax.toString()
                tvTitleTex.text="Tax (${summaryModel.getTax()}%)"
                tvOrderDeliveryCharge.text=getStringPref(this@OrderSummuryActivity,isCurrancy)+summaryModel.getDelivery_charge()
                val totalprice=summaryModel.getOrder_total()!!.toInt()+orderTax+summaryModel.getDelivery_charge()!!.toInt()
                tvOrderTotalCharge.text=getStringPref(this@OrderSummuryActivity,isCurrancy)+totalprice.toString()
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun callApiOrderSummary() {
        Common.showLoadingProgress(this@OrderSummuryActivity)
        val map = HashMap<String, String>()
        map.put("user_id", getStringPref(this@OrderSummuryActivity,userId)!!)
        val call = ApiClient.getClient.setSummary(map)
        call.enqueue(object : Callback<RestSummaryResponse> {
            override fun onResponse(
                call: Call<RestSummaryResponse>,
                response: Response<RestSummaryResponse>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: RestSummaryResponse = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        if (restResponce.getData().size > 0) {
                            rvOrderItemFood.visibility = View.VISIBLE
                            val foodCategoryList = restResponce.getData()
                            val summary = restResponce.getSummery()
                            setFoodCategoryAdaptor(foodCategoryList,summary)
                        } else {
                            rvOrderItemFood.visibility = View.GONE
                        }
                    } else if (restResponce.getStatus().equals("0")) {
                        dismissLoadingProgress()
                        rvOrderItemFood.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<RestSummaryResponse>, t: Throwable) {
                dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@OrderSummuryActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiPromocode() {
        Common.showLoadingProgress(this@OrderSummuryActivity)
        val map = HashMap<String, String>()
        map.put("user_id", getStringPref(this@OrderSummuryActivity,userId)!!)
        val call = ApiClient.getClient.getPromoCodeList()
        call.enqueue(object : Callback<ListResponse<PromocodeModel>> {
            override fun onResponse(
                call: Call<ListResponse<PromocodeModel>>,
                response: Response<ListResponse<PromocodeModel>>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: ListResponse<PromocodeModel> = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        if (restResponce.getData().size > 0) {
                            rvOrderItemFood.visibility = View.VISIBLE
                            promocodeList = restResponce.getData()
                            openDialogPromocode()
                        } else {
                            rvOrderItemFood.visibility = View.GONE
                        }
                    } else if (restResponce.getStatus().equals("0")) {
                        dismissLoadingProgress()
                        rvOrderItemFood.visibility = View.GONE
                    }
                }
            }
            override fun onFailure(call: Call<ListResponse<PromocodeModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@OrderSummuryActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiCheckPromocode() {
        Common.showLoadingProgress(this@OrderSummuryActivity)
        val map = HashMap<String, String>()
        map.put("offer_code", edPromocode.text.toString())
        val call = ApiClient.getClient.setApplyPromocode(map)
        call.enqueue(object : Callback<RestResponse<GetPromocodeModel>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<RestResponse<GetPromocodeModel>>,
                response: Response<RestResponse<GetPromocodeModel>>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: RestResponse<GetPromocodeModel> = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        rlOffer.visibility=View.VISIBLE
                        tvDiscountOffer.text="-"+restResponce.getData()!!.getOffer_amount()+"%"
                        tvPromoCodeApply.text=restResponce.getData()!!.getOffer_code()
                        val subtotalCharge=(summaryModel.getOrder_total()!!.toInt()*restResponce.getData()!!.getOffer_amount()!!.toInt())/100
                        val total=summaryModel.getOrder_total()!!.toInt()-subtotalCharge
                        val totalChargeWithText=(summaryModel.getOrder_total()!!.toInt()*summaryModel.getTax()!!.toInt())/100
                        val mainTotal=totalChargeWithText+total+summaryModel.getDelivery_charge()!!.toInt()
                        tvDiscountOffer.text="-"+getStringPref(this@OrderSummuryActivity,isCurrancy)+subtotalCharge
                        tvOrderTotalCharge.text=getStringPref(this@OrderSummuryActivity,isCurrancy)+mainTotal.toString()
                        discountAmount=subtotalCharge.toString()
                        discountPer=restResponce.getData()!!.getOffer_amount()!!
                        tvApply.text="Remove"
                    } else if (restResponce.getStatus().equals("0")) {
                        dismissLoadingProgress()
                        edPromocode.setText("")
                        rlOffer.visibility=View.GONE
                        tvApply.text="Apply"
                        alertErrorOrValidationDialog(
                            this@OrderSummuryActivity,
                            restResponce.getMessage()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<GetPromocodeModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@OrderSummuryActivity,
                    resources.getString(R.string.error_msg)
                )
            }


        })
    }

    @SuppressLint("SetTextI18n")
    private fun setFoodCategoryAdaptor(foodCategoryList: ArrayList<OrderSummaryModel>, summary: SummaryModel?) {
        if(foodCategoryList.size>0){
            setFoodCategoryAdaptor(foodCategoryList)
        }
        summaryModel=summary!!
        val orderTax:Int=(summary.getOrder_total()!!.toInt()*summary.getTax()!!.toInt())/100
        tvOrderTotalPrice.text=getStringPref(this@OrderSummuryActivity,isCurrancy)+summary.getOrder_total()
        tvOrderTaxPrice.text=getStringPref(this@OrderSummuryActivity,isCurrancy)+orderTax.toString()
        tvTitleTex.text="Tax (${summary.getTax()}%)"
        tvOrderDeliveryCharge.text=getStringPref(this@OrderSummuryActivity,isCurrancy)+summary.getDelivery_charge()
        val totalprice=summary.getOrder_total()!!.toInt()+orderTax+summary.getDelivery_charge()!!.toInt()
        tvOrderTotalCharge.text=getStringPref(this@OrderSummuryActivity,isCurrancy)+totalprice.toString()
    }

    fun setFoodCategoryAdaptor(orderHistoryList: ArrayList<OrderSummaryModel>) {
        val orderHistoryAdapter = object : BaseAdaptor<OrderSummaryModel>(this@OrderSummuryActivity, orderHistoryList) {
                @SuppressLint("SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: OrderSummaryModel,
                    position: Int
                ) {
                    val tvOrderFoodName: TextView = holder!!.itemView.findViewById(R.id.tvFoodName)
                    val tvPrice: TextView = holder.itemView.findViewById(R.id.tvPrice)
                    val tvQtyNumber: TextView = holder.itemView.findViewById(R.id.tvQtyPrice)

                    tvOrderFoodName.text = orderHistoryList.get(position).getItem_name()
                    tvPrice.text = getStringPref(this@OrderSummuryActivity,isCurrancy)+orderHistoryList.get(position).getTotal_price()
                    tvQtyNumber.text ="${orderHistoryList.get(position).getQty()} * ${getStringPref(this@OrderSummuryActivity,isCurrancy)+orderHistoryList.get(position).getItem_price()}"

                }

                override fun setItemLayout(): Int {
                    return R.layout.row_orderfoodbig
                }

                override fun setNoDataView(): TextView? {
                    return null
                }
            }
        rvOrderItemFood.adapter = orderHistoryAdapter
        rvOrderItemFood.layoutManager = LinearLayoutManager(this@OrderSummuryActivity)
        rvOrderItemFood.itemAnimator = DefaultItemAnimator()
        rvOrderItemFood.isNestedScrollingEnabled = true
    }

    fun openDialogPromocode() {
        val dialog: Dialog = Dialog(this@OrderSummuryActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.windowAnimations = R.style.DialogAnimation
        dialog.window!!.attributes = lp
        dialog.setContentView(R.layout.dlg_procode)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val ivCancel = dialog.findViewById<ImageView>(R.id.ivCancel)
        val rvPromocode = dialog.findViewById<RecyclerView>(R.id.rvPromoCode)
        setPromocodeAdaptor(promocodeList!!,rvPromocode,dialog)
        ivCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
   /* var dialog: Dialog?=null
    fun openDialogApplyCode() {
        dialog = Dialog(this@OrderSummuryActivity)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(true)
      *//*  val lp = WindowManager.LayoutParams()
        lp.windowAnimations = R.style.DialogAnimation
        dialog!!.window!!.attributes = lp*//*
        dialog!!.setContentView(R.layout.dlg_actiondone)
        dialog!!.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvDone = dialog!!.findViewById<TextView>(R.id.tvDone)
        tvDone.setOnClickListener {
            dialog!!.dismiss()
            callApiCheckPromocode()
        }
        dialog!!.show()
    }*/

    open fun setPromocodeAdaptor(
        promocodeList: ArrayList<PromocodeModel>,
        rvPromocode: RecyclerView,
        dialog: Dialog
    ) {
        val orderHistoryAdapter = object : BaseAdaptor<PromocodeModel>(this@OrderSummuryActivity, promocodeList) {
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: PromocodeModel,
                position: Int
            ) {
                val tvTitleOrderNumber: TextView = holder!!.itemView.findViewById(R.id.tvTitleOrderNumber)
                val tvPromocode: TextView = holder.itemView.findViewById(R.id.tvPromocode)
                val tvPromocodeDescription: TextView = holder.itemView.findViewById(R.id.tvPromocodeDescription)
                val tvCopyCode: TextView = holder.itemView.findViewById(R.id.tvCopyCode)

                tvTitleOrderNumber.text = promocodeList.get(position).getOffer_name()
                tvPromocode.text =promocodeList.get(position).getOffer_code()
                tvPromocodeDescription.text =promocodeList.get(position).getDescription()

                tvCopyCode.setOnClickListener {
                    dialog.dismiss()
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.text = promocodeList.get(position).getOffer_code()
                }
            }

            override fun setItemLayout(): Int {
                return R.layout.row_promocode
            }

            override fun setNoDataView(): TextView? {
                return null
            }
        }
        rvPromocode.adapter = orderHistoryAdapter
        rvPromocode.layoutManager = LinearLayoutManager(this@OrderSummuryActivity)
        rvPromocode.itemAnimator = DefaultItemAnimator()
        rvPromocode.isNestedScrollingEnabled = true
    }
}