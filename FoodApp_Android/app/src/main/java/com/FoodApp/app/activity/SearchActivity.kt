package com.FoodApp.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.FoodApp.app.R
import com.FoodApp.app.api.ApiClient
import com.FoodApp.app.api.RestResponse
import com.FoodApp.app.api.SingleResponse
import com.FoodApp.app.base.BaseActivity
import com.FoodApp.app.base.BaseAdaptor
import com.FoodApp.app.model.FoodItemModel
import com.FoodApp.app.model.FoodItemResponseModel
import com.FoodApp.app.utils.Common
import com.FoodApp.app.utils.SharePreference
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : BaseActivity() {
    var strSearch: String = ""
    var CURRENT_PAGES: Int = 1
    var TOTAL_PAGES: Int = 0
    var foodAdapter: BaseAdaptor<FoodItemModel>? = null
    var foodList: ArrayList<FoodItemModel>? = null
    var manager1: GridLayoutManager? = null
    override fun setLayout(): Int {
        return R.layout.activity_search
    }

    override fun InitView() {
        foodList= ArrayList()
        manager1= GridLayoutManager(this@SearchActivity,2, GridLayoutManager.VERTICAL,false)
        rvSearchOrder.layoutManager=manager1
        ivBack.setOnClickListener {
            val intent =
                Intent(this@SearchActivity, DashboardActivity::class.java).putExtra("pos", "1")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        edSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(v.text.toString())
                return@OnEditorActionListener true
            }
            false
        })

        var visibleItemCount: Int = 0
        var totalItemCount: Int = 0
        var pastVisiblesItems: Int = 0

        rvSearchOrder.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = manager1!!.getChildCount()
                    totalItemCount = manager1!!.getItemCount()
                    pastVisiblesItems = manager1!!.findFirstVisibleItemPosition()
                    if (CURRENT_PAGES < TOTAL_PAGES) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            CURRENT_PAGES += 1
                            if (Common.isCheckNetwork(this@SearchActivity)) {
                                callApiSearchFood(strSearch, false)
                            } else {
                                Common.alertErrorOrValidationDialog(
                                    this@SearchActivity,
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        }
                    }
                }
            }
        })


    }

    private fun performSearch(strSearch: String) {
        this.strSearch = strSearch
        if (Common.isCheckNetwork(this@SearchActivity)) {
            callApiSearchFood(strSearch, true)
        } else {
            Common.alertErrorOrValidationDialog(
                this@SearchActivity,
                resources.getString(R.string.no_internet)
            )
        }
    }

    private fun callApiSearchFood(strSearch: String?, isFirstTime: Boolean?) {
        if(isFirstTime!!){
            foodList!!.clear()
        }
        Common.showLoadingProgress(this@SearchActivity)
        val map = HashMap<String, String>()
        map.put("keyword", strSearch!!)
        map.put("user_id", SharePreference.getStringPref(this@SearchActivity, SharePreference.userId)!!)
        val call = ApiClient.getClient.setSearch(map, CURRENT_PAGES.toString())
        call.enqueue(object : Callback<RestResponse<FoodItemResponseModel>> {
            override fun onResponse(
                call: Call<RestResponse<FoodItemResponseModel>>,
                response: Response<RestResponse<FoodItemResponseModel>>
            ) {
                if (response.code() == 200) {
                    val restResponce: RestResponse<FoodItemResponseModel> = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        tvNoDataFound.visibility=View.GONE
                        rvSearchOrder.visibility=View.VISIBLE
                        Common.dismissLoadingProgress()
                        val foodItemResponseModel: FoodItemResponseModel = restResponce.getData()!!
                        CURRENT_PAGES = foodItemResponseModel.getCurrent_page()!!.toInt()
                        TOTAL_PAGES = foodItemResponseModel.getLast_page()!!.toInt()
                        if(foodItemResponseModel.getData()!!.size>0){
                            for (i in 0 until foodItemResponseModel.getData()!!.size) {
                                val foodItemModel = FoodItemModel()
                                foodItemModel.setId(foodItemResponseModel.getData()!!.get(i).getId())
                                foodItemModel.setItem_name(
                                    foodItemResponseModel.getData()!!.get(i).getItem_name()
                                )
                                foodItemModel.setItem_price(
                                    foodItemResponseModel.getData()!!.get(i).getItem_price()
                                )
                                foodItemModel.setItemimage(
                                    foodItemResponseModel.getData()!!.get(i).getItemimage()
                                )
                                foodItemModel.setIs_favorite(
                                    foodItemResponseModel.getData()!!.get(i).getIs_favorite()
                                )
                                foodList!!.add(foodItemModel)
                            }
                            setFoodAdaptor(isFirstTime!!)
                        }else{
                           tvNoDataFound.visibility=View.VISIBLE
                           rvSearchOrder.visibility=View.GONE
                        }
                    } else if (restResponce.getStatus().equals("0")) {
                        Common.dismissLoadingProgress()
                        /*Common.alertErrorOrValidationDialog(
                            this@SearchActivity,
                            restResponce.getMessage()
                        )*/
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<FoodItemResponseModel>>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@SearchActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this@SearchActivity, DashboardActivity::class.java).putExtra("pos", "1")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    fun setFoodAdaptor(isFirstTime: Boolean) {
        if (isFirstTime) {
            foodAdapter = object : BaseAdaptor<FoodItemModel>(this@SearchActivity, foodList!!) {
                @SuppressLint("NewApi")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: FoodItemModel,
                    position: Int
                ) {
                    val tvFoodName: TextView = holder!!.itemView.findViewById(R.id.tvFoodName)
                    val tvFoodPriceList: TextView =
                        holder.itemView.findViewById(R.id.tvFoodPriceList)
                    val tvFoodPriceGrid: TextView =
                        holder.itemView.findViewById(R.id.tvFoodPriceGrid)
                    val ivFood: ImageView = holder.itemView.findViewById(R.id.ivFood)
                    val icLike: ImageView = holder.itemView.findViewById(R.id.icLike)
                    tvFoodName.text = foodList!!.get(position).getItem_name()
                    tvFoodPriceList.text = Common.getCurrancy(this@SearchActivity)
                        .plus(foodList!!.get(position).getItem_price())
                    tvFoodPriceGrid.text = Common.getCurrancy(this@SearchActivity)
                        .plus(foodList!!.get(position).getItem_price())
                    Glide.with(this@SearchActivity)
                        .load(foodList!!.get(position).getItemimage()!!.getImage())
                        .placeholder(resources.getDrawable(R.drawable.placeholder)).into(ivFood)
                    holder.itemView.setOnClickListener {
                        startActivity(
                            Intent(
                                this@SearchActivity,
                                FoodDetailActivity::class.java
                            ).putExtra("foodItemId", foodList!!.get(position).getId())
                        )
                    }
                    tvFoodPriceList.visibility= View.GONE
                    tvFoodPriceGrid.visibility= View.VISIBLE

                    if(foodList!!.get(position).getIs_favorite().equals("0")){
                        icLike.setImageDrawable(resources.getDrawable(R.drawable.ic_unlike))
                        icLike.imageTintList= ColorStateList.valueOf(Color.WHITE)
                    }else{
                        icLike.setImageDrawable(resources.getDrawable(R.drawable.ic_favourite_like))
                        icLike.imageTintList= ColorStateList.valueOf(Color.WHITE)
                    }

                    icLike.setOnClickListener {
                        if(foodList!!.get(position).getIs_favorite().equals("0")){
                            val map = HashMap<String, String>()
                            map.put("item_id",foodList!!.get(position).getId()!!)
                            map.put("user_id",SharePreference.getStringPref(this@SearchActivity,SharePreference.userId)!!)
                            if(Common.isCheckNetwork(this@SearchActivity)){
                                callApiFavourite(map,position)
                            }else{
                                Common.alertErrorOrValidationDialog(
                                    this@SearchActivity,
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        }
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_foodsubcategory
                }

                override fun setNoDataView(): TextView? {
                    return null
                }
            }
            rvSearchOrder.adapter = foodAdapter
            rvSearchOrder.itemAnimator = DefaultItemAnimator()
            rvSearchOrder.isNestedScrollingEnabled = true
        } else {
            foodAdapter!!.notifyDataSetChanged()
        }
    }

    private fun callApiFavourite(hasmap:HashMap<String, String>,pos:Int) {
        Common.showLoadingProgress(this@SearchActivity)
        val call = ApiClient.getClient.setAddFavorite(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(call: Call<SingleResponse>, response: Response<SingleResponse>) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.getStatus().equals("1")) {
                        Common.dismissLoadingProgress()
                        foodList!!.get(pos).setIs_favorite("1")
                        foodAdapter!!.notifyDataSetChanged()

                    } else if (restResponse.getStatus().equals("0")) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@SearchActivity,
                            restResponse.getMessage()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@SearchActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }


}