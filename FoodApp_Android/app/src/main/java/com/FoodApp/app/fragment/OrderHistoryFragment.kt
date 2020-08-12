package com.FoodApp.app.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.FoodApp.app.R
import com.FoodApp.app.activity.DashboardActivity
import com.FoodApp.app.activity.OrderDetailActivity
import com.FoodApp.app.base.BaseAdaptor
import com.FoodApp.app.base.BaseFragmnet
import com.FoodApp.app.model.OrderHistoryModel
import com.FoodApp.app.utils.Common
import com.FoodApp.app.utils.Common.alertErrorOrValidationDialog
import com.FoodApp.app.utils.Common.dismissLoadingProgress
import com.FoodApp.app.utils.Common.showLoadingProgress
import com.FoodApp.app.utils.SharePreference.Companion.getStringPref
import com.FoodApp.app.utils.SharePreference.Companion.userId
import com.FoodApp.app.api.*
import com.FoodApp.app.utils.SharePreference
import kotlinx.android.synthetic.main.fragment_orderhistory.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryFragment : BaseFragmnet() {
    override fun setView(): Int {
        return R.layout.fragment_orderhistory
    }

    override fun Init(view: View) {
        if (Common.isCheckNetwork(activity!!)) {
            callApiOrderHistory()
        } else {
            alertErrorOrValidationDialog(activity!!, resources.getString(R.string.no_internet))
        }

        ivMenu.setOnClickListener {
            (activity as DashboardActivity?)!!.onDrawerToggle()
        }
    }

    private fun callApiOrderHistory() {
        showLoadingProgress(activity!!)
        val map = HashMap<String, String>()
        map.put("user_id", getStringPref(activity!!, userId)!!)
        val call = ApiClient.getClient.getOrderHistory(map)
        call.enqueue(object : Callback<ListResponse<OrderHistoryModel>> {
            override fun onResponse(
                call: Call<ListResponse<OrderHistoryModel>>,
                response: Response<ListResponse<OrderHistoryModel>>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: ListResponse<OrderHistoryModel> = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        if (restResponce.getData().size > 0) {
                            rvOrderHistory.visibility = View.VISIBLE
                            tvNoDataFound.visibility = View.GONE
                            val foodCategoryList = restResponce.getData()
                            setFoodCategoryAdaptor(foodCategoryList)
                        } else {
                            rvOrderHistory.visibility = View.GONE
                            tvNoDataFound.visibility = View.VISIBLE
                        }

                    } else if (restResponce.getStatus().equals("0")) {
                        dismissLoadingProgress()
                        rvOrderHistory.visibility = View.GONE
                        tvNoDataFound.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<ListResponse<OrderHistoryModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun setFoodCategoryAdaptor(orderHistoryList: ArrayList<OrderHistoryModel>) {
        val orderHistoryAdapter =
            object : BaseAdaptor<OrderHistoryModel>(activity!!, orderHistoryList) {
                @SuppressLint("SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: OrderHistoryModel,
                    position: Int
                ) {
                    val tvOrderNumber: TextView = holder!!.itemView.findViewById(R.id.tvOrderNumber)
                    val tvPrice: TextView = holder.itemView.findViewById(R.id.tvPrice)
                    val tvQtyNumber: TextView = holder.itemView.findViewById(R.id.tvQtyNumber)
                    val tvOrderStatus: TextView = holder.itemView.findViewById(R.id.tvOrderStatus)
                    val tvPaymentType: TextView = holder.itemView.findViewById(R.id.tvPaymentType)

                    tvOrderNumber.text = orderHistoryList.get(position).getOrder_number()
                    tvPrice.text =getStringPref(activity!!,SharePreference.isCurrancy)+orderHistoryList.get(position).getTotal_price()
                    tvQtyNumber.text = orderHistoryList.get(position).getQty()
                    if(orderHistoryList.get(position).getStatus().equals("1")){
                        tvOrderStatus.text="Order received"
                    }else if(orderHistoryList.get(position).getStatus().equals("2")) {
                        tvOrderStatus.text="On the way"
                    }else if(orderHistoryList.get(position).getStatus().equals("3")){
                        tvOrderStatus.text="Order Delivered"
                    }else if(orderHistoryList.get(position).getStatus().equals("4")){
                        tvOrderStatus.text="Order canclled"
                    }

                    if(orderHistoryList.get(position).getPayment_type()!!.toInt()==0){
                        tvPaymentType.text = "COD"
                    }else{
                        tvPaymentType.text = "ONLINE PAY"
                    }

                    holder.itemView.setOnClickListener {
                      startActivity(Intent(activity!!,OrderDetailActivity::class.java).putExtra("order_id",orderHistoryList.get(position).getId()))
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_orderdelivery
                }

                override fun setNoDataView(): TextView? {
                    return null
                }
            }
        rvOrderHistory.adapter = orderHistoryAdapter
        rvOrderHistory.layoutManager = LinearLayoutManager(activity!!)
        rvOrderHistory.itemAnimator = DefaultItemAnimator()
        rvOrderHistory.isNestedScrollingEnabled = true
    }
}