package com.FoodApp.app.activity

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.FoodApp.app.R
import com.FoodApp.app.api.RestOrderDetailResponse
import com.FoodApp.app.base.BaseActivity
import com.FoodApp.app.base.BaseAdaptor
import com.FoodApp.app.model.OrderDetailModel
import com.FoodApp.app.utils.Common
import com.FoodApp.app.utils.Common.showLoadingProgress
import com.FoodApp.app.api.*
import com.FoodApp.app.utils.SharePreference.Companion.getStringPref
import com.FoodApp.app.utils.SharePreference.Companion.isCurrancy
import kotlinx.android.synthetic.main.activity_orderdetail.*
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvOrderDeliveryCharge
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvOrderTaxPrice
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvOrderTotalCharge
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvOrderTotalPrice
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetailActivity:BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_orderdetail
    }

    override fun InitView() {
        if(Common.isCheckNetwork(this@OrderDetailActivity)){
            callApiOrderDetail()
        }else{
            Common.alertErrorOrValidationDialog(
                this@OrderDetailActivity,
                resources.getString(R.string.no_internet)
            )
        }
        ivBack.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun callApiOrderDetail() {
        showLoadingProgress(this@OrderDetailActivity)
        val map = HashMap<String, String>()
        map.put("order_id",intent.getStringExtra("order_id")!!)
        val call = ApiClient.getClient.setgetOrderDetail(map)
        call.enqueue(object : Callback<RestOrderDetailResponse> {
            override fun onResponse(
                call: Call<RestOrderDetailResponse>,
                response: Response<RestOrderDetailResponse>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    val restResponce: RestOrderDetailResponse = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        if (restResponce.getData().size > 0) {
                            rvOrderItemFood.visibility = View.VISIBLE
                            setFoodDetailData(restResponce)
                        } else {
                            rvOrderItemFood.visibility = View.GONE
                        }
                    } else if (restResponce.getStatus().equals("0")) {
                        Common.dismissLoadingProgress()
                        rvOrderItemFood.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<RestOrderDetailResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@OrderDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setFoodDetailData(response: RestOrderDetailResponse) {
        if(response.getData().size>0){
            setFoodCategoryAdaptor(response.getData())
        }
        if(response.getSummery()!!.getPromocode()==null){
            rlDiscount.visibility=View.GONE
            tvOrderAddress.text=response.getAddress()
            tvOrderTotalPrice.text=getStringPref(this@OrderDetailActivity,isCurrancy)+response.getSummery()!!.getOrder_total()
            tvOrderTaxPrice.text=getStringPref(this@OrderDetailActivity,isCurrancy)+response.getSummery()!!.getTax()
            tvOrderDeliveryCharge.text=getStringPref(this@OrderDetailActivity,isCurrancy)+response.getSummery()!!.getDelivery_charge()

            val getTex:Int=(response.getSummery()!!.getOrder_total()!!.toInt()*response.getSummery()!!.getTax()!!.toInt())/100
            tvTitleTex.text="Tax (${response.getSummery()!!.getTax()}%)"
            tvOrderTaxPrice.text=getStringPref(this@OrderDetailActivity,isCurrancy)+getTex.toString()
            val totalprice=response.getSummery()!!.getOrder_total()!!.toInt()+getTex+response.getSummery()!!.getDelivery_charge()!!.toInt()
            tvOrderTotalCharge.text=getStringPref(this@OrderDetailActivity,isCurrancy)+totalprice.toString()
        }else{
            rlDiscount.visibility=View.VISIBLE
            tvOrderAddress.text=response.getAddress()
            tvOrderTotalPrice.text=getStringPref(this@OrderDetailActivity,isCurrancy)+response.getSummery()!!.getOrder_total()
            tvOrderTaxPrice.text=getStringPref(this@OrderDetailActivity,isCurrancy)+response.getSummery()!!.getTax()
            tvOrderDeliveryCharge.text=getStringPref(this@OrderDetailActivity,isCurrancy)+response.getSummery()!!.getDelivery_charge()

            val getTex:Int=(response.getSummery()!!.getOrder_total()!!.toInt()*response.getSummery()!!.getTax()!!.toInt())/100
            tvTitleTex.text="Tax (${response.getSummery()!!.getTax()}%)"
            tvOrderTaxPrice.text=getStringPref(this@OrderDetailActivity,isCurrancy)+getTex.toString()

            tvDiscountOffer.text ="-"+getStringPref(this@OrderDetailActivity,isCurrancy)+response.getSummery()!!.getDiscount_amount()
            tvPromoCodeApply.text =response.getSummery()!!.getPromocode()

            val totalprice=response.getSummery()!!.getOrder_total()!!.toInt()+getTex+response.getSummery()!!.getDelivery_charge()!!.toInt()
            val disTotal=totalprice-response.getSummery()!!.getDiscount_amount()!!.toInt()
            tvOrderTotalCharge.text=getStringPref(this@OrderDetailActivity,isCurrancy)+disTotal.toString()
        }
    }

    fun setFoodCategoryAdaptor(orderHistoryList: ArrayList<OrderDetailModel>) {
        val orderHistoryAdapter = object : BaseAdaptor<OrderDetailModel>(this@OrderDetailActivity, orderHistoryList) {
            @SuppressLint("SetTextI18n")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: OrderDetailModel,
                position: Int
            ) {

                val ivFoodItem: ImageView = holder!!.itemView.findViewById(R.id.ivFoodCart)
                val tvOrderFoodName: TextView = holder.itemView.findViewById(R.id.tvFoodName)
                val tvPrice: TextView = holder.itemView.findViewById(R.id.tvPrice)
                val tvQtyNumber: TextView = holder.itemView.findViewById(R.id.tvQtyPrice)

                tvOrderFoodName.text =orderHistoryList.get(position).getItem_name()
                tvPrice.text = getStringPref(this@OrderDetailActivity,isCurrancy)+orderHistoryList.get(position).getTotal_price()
                tvQtyNumber.text ="${orderHistoryList.get(position).getQty()} * ${getStringPref(this@OrderDetailActivity,isCurrancy)+orderHistoryList.get(position).getItem_price()}"

                Glide.with(this@OrderDetailActivity).load(orderHistoryList.get(position).getItemimage().getImage())
                    .placeholder(resources.getDrawable(R.drawable.placeholder)).centerCrop()
                    .into(ivFoodItem)
            }
            override fun setItemLayout(): Int {
                return R.layout.row_orderitem
            }
            override fun setNoDataView(): TextView? {
                return null
            }
        }
        rvOrderItemFood.adapter = orderHistoryAdapter
        rvOrderItemFood.layoutManager = LinearLayoutManager(this@OrderDetailActivity)
        rvOrderItemFood.itemAnimator = DefaultItemAnimator()
        rvOrderItemFood.isNestedScrollingEnabled = true
    }
}