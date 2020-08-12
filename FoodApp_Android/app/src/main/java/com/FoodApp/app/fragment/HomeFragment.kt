package com.FoodApp.app.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.FoodApp.app.R
import com.FoodApp.app.activity.CartActivity
import com.FoodApp.app.activity.DashboardActivity
import com.FoodApp.app.activity.FoodDetailActivity
import com.FoodApp.app.activity.SearchActivity
import com.FoodApp.app.base.BaseAdaptor
import com.FoodApp.app.base.BaseFragmnet
import com.FoodApp.app.model.FoodCategoryModel
import com.FoodApp.app.model.FoodItemModel
import com.FoodApp.app.model.FoodItemResponseModel
import com.FoodApp.app.utils.Common.alertErrorOrValidationDialog
import com.FoodApp.app.utils.Common.dismissLoadingProgress
import com.FoodApp.app.utils.Common.isCheckNetwork
import com.FoodApp.app.utils.Common.showLoadingProgress
import com.FoodApp.app.api.*
import com.FoodApp.app.utils.Common.getCurrancy
import com.FoodApp.app.utils.SharePreference
import com.FoodApp.app.utils.SharePreference.Companion.isCurrancy
import com.FoodApp.app.utils.SharePreference.Companion.isLinearLayoutManager
import com.FoodApp.app.utils.SharePreference.Companion.setStringPref
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : BaseFragmnet() {
    var foodCategoryAdapter: BaseAdaptor<FoodCategoryModel>? = null
    private var foodCategoryList: ArrayList<FoodCategoryModel>? = null

    var foodAdapter: BaseAdaptor<FoodItemModel>? = null
    private var foodList: ArrayList<FoodItemModel>? = null
    var foodCategoryId="";
    var manager: LinearLayoutManager? = null
    var manager1: GridLayoutManager? = null
    var CurrentPageNo: Int = 1
    var TOTAL_PAGES: Int = 0

    override fun setView(): Int {
        return R.layout.fragment_home
    }
    override fun Init(view: View) {
        foodList= ArrayList()
        if(SharePreference.getBooleanPref(activity!!,isLinearLayoutManager)){
            manager=LinearLayoutManager(activity)
            rvFoodSubcategory.layoutManager=manager
            ic_grid.setImageDrawable(resources.getDrawable(R.drawable.ic_listitem))
        }else{
            manager1=GridLayoutManager(activity,2,GridLayoutManager.VERTICAL,false)
            rvFoodSubcategory.layoutManager=manager1
            ic_grid.setImageDrawable(resources.getDrawable(R.drawable.ic_grid))
        }
        if(isCheckNetwork(activity!!)){
            callApiCategoryFood()
        }else{
            alertErrorOrValidationDialog(activity!!, resources.getString(R.string.no_internet))
        }
        var visibleItemCount: Int = 0
        var totalItemCount: Int = 0
        var pastVisiblesItems: Int = 0
        rvFoodSubcategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    if(SharePreference.getBooleanPref(activity!!,isLinearLayoutManager)){
                        visibleItemCount = manager!!.getChildCount()
                        totalItemCount = manager!!.getItemCount()
                        pastVisiblesItems = manager!!.findFirstVisibleItemPosition()
                        if (CurrentPageNo < TOTAL_PAGES) {
                            if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                                CurrentPageNo += 1
                                if(isCheckNetwork(activity!!)){
                                    callApiFood(foodCategoryId,false,false,false)
                                }else{
                                    alertErrorOrValidationDialog(activity!!, resources.getString(R.string.no_internet))
                                }
                            }
                        }
                    }else{
                        visibleItemCount = manager1!!.getChildCount()
                        totalItemCount = manager1!!.getItemCount()
                        pastVisiblesItems = manager1!!.findFirstVisibleItemPosition()
                        if (CurrentPageNo < TOTAL_PAGES) {
                            if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                                CurrentPageNo += 1
                                if(isCheckNetwork(activity!!)){
                                    callApiFood(foodCategoryId,false,false,false)
                                }else{
                                    alertErrorOrValidationDialog(activity!!, resources.getString(R.string.no_internet))
                                }
                            }
                        }
                    }

                }
            }
        })

        ivMenu.setOnClickListener {
            (activity as DashboardActivity?)!!.onDrawerToggle()
        }
        ivCart.setOnClickListener {
            openActivity(CartActivity::class.java)
            activity!!.finish()
        }
        ivSearch.setOnClickListener {
            openActivity(SearchActivity::class.java)
            activity!!.finish()
        }
        ic_grid.setOnClickListener {
            if(SharePreference.getBooleanPref(activity!!,isLinearLayoutManager)){
                manager1=GridLayoutManager(activity,2,GridLayoutManager.VERTICAL,false)
                rvFoodSubcategory.layoutManager=manager1
                SharePreference.setBooleanPref(activity!!,isLinearLayoutManager,false)
                ic_grid.setImageDrawable(resources.getDrawable(R.drawable.ic_grid))
            }else{
                manager=LinearLayoutManager(activity)
                rvFoodSubcategory.layoutManager=manager
                SharePreference.setBooleanPref(activity!!,isLinearLayoutManager,true)
                ic_grid.setImageDrawable(resources.getDrawable(R.drawable.ic_listitem))
            }

        }
    }
    private fun callApiCategoryFood() {
        showLoadingProgress(activity!!)
        val call = ApiClient.getClient.getFoodCategory()
        call.enqueue(object : Callback<ListResponse<FoodCategoryModel>> {
            override fun onResponse(
                call: Call<ListResponse<FoodCategoryModel>>,
                response: Response<ListResponse<FoodCategoryModel>>
            ) {
                if (response.code() == 200) {
                    val restResponce: ListResponse<FoodCategoryModel> = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                       // dismissLoadingProgress()
                        if (restResponce.getData().size > 0) {
                            foodCategoryList = restResponce.getData()
                            foodCategoryId = foodCategoryList!!.get(0).getId()!!
                            foodCategoryList!!.get(0).setSelect(true)
                            callApiFood(foodCategoryId, true,false,true)
                        }
                    } else if (restResponce.getStatus().equals("0")) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            activity!!,
                            restResponce.getMessage()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ListResponse<FoodCategoryModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiFood(id: String?,isFirstTime: Boolean,isSelect:Boolean,isFristTimeSelect:Boolean) {
        if(!isFirstTime){
            showLoadingProgress(activity!!)
        }
        if(isSelect){
            showLoadingProgress(activity!!)
            foodList!!.clear()
        }
        val map = HashMap<String, String>()
        map.put("cat_id",id!!)
        map.put("user_id",SharePreference.getStringPref(activity!!,SharePreference.userId)!!)
        val call = ApiClient.getClient.getFoodItem(map,CurrentPageNo.toString())
        call.enqueue(object : Callback<RestResponse<FoodItemResponseModel>> {
            override fun onResponse(
                call: Call<RestResponse<FoodItemResponseModel>>,
                response: Response<RestResponse<FoodItemResponseModel>>
            ) {
                if (response.code() == 200) {
                    val restResponce: RestResponse<FoodItemResponseModel> = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                       dismissLoadingProgress()
                       val foodItemResponseModel:FoodItemResponseModel=restResponce.getData()!!
                       CurrentPageNo=foodItemResponseModel.getCurrent_page()!!.toInt()
                       TOTAL_PAGES=foodItemResponseModel.getLast_page()!!.toInt()
                       for(i in 0 until foodItemResponseModel.getData()!!.size){
                            val foodItemModel=FoodItemModel()
                            foodItemModel.setId(foodItemResponseModel.getData()!!.get(i).getId())
                            foodItemModel.setItem_name(foodItemResponseModel.getData()!!.get(i).getItem_name())
                            foodItemModel.setItem_price(foodItemResponseModel.getData()!!.get(i).getItem_price())
                            foodItemModel.setItemimage(foodItemResponseModel.getData()!!.get(i).getItemimage())
                            foodItemModel.setIs_favorite(foodItemResponseModel.getData()!!.get(i).getIs_favorite())
                            foodList!!.add(foodItemModel)
                       }
                       setStringPref(activity!!,isCurrancy,restResponce.getCurrency()!!.getCurrency()!!)
                       setFoodAdaptor(isFirstTime,isFristTimeSelect)
                    } else if (restResponce.getMessage().equals("0")) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            activity!!,
                            restResponce.getMessage()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<FoodItemResponseModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun setFoodCategoryAdaptor() {
        foodCategoryAdapter = object : BaseAdaptor<FoodCategoryModel>(activity!!, foodCategoryList!!) {
                @SuppressLint("ResourceType")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: FoodCategoryModel,
                    position: Int
                ) {
                    val tvFoodCategoryName: TextView = holder!!.itemView.findViewById(R.id.tvFoodCategoryName)
                    val ivFoodCategory: ImageView = holder.itemView.findViewById(R.id.ivFoodCategory)
                    val llBarger: LinearLayout = holder.itemView.findViewById(R.id.llBarger)
                    if(foodCategoryList!!.get(position).isSelect()!!){
                        llBarger.background=resources.getDrawable(R.drawable.bg_strock_orange5)
                    }else{
                        llBarger.background=null
                    }
                    tvFoodCategoryName.text = foodCategoryList!!.get(position).getCategory_name()
                    Glide.with(activity!!).load(foodCategoryList!!.get(position).getImage()).placeholder(activity!!.resources.getDrawable(R.drawable.placeholder)).into(ivFoodCategory)
                    holder.itemView.setOnClickListener {
                        for(i in 0 until foodCategoryList!!.size){
                            foodCategoryList!!.get(i).setSelect(false)
                        }
                        foodCategoryList!!.get(position).setSelect(true)
                        foodCategoryId=foodCategoryList!!.get(position).getId()!!
                        notifyDataSetChanged();
                        if(isCheckNetwork(activity!!)){
                            callApiFood(foodCategoryId,true,true,false)
                        }else{
                            alertErrorOrValidationDialog(activity!!, resources.getString(R.string.no_internet))
                        }
                    }
                }
                override fun setItemLayout(): Int {
                    return R.layout.row_foodcategory
                }
                override fun setNoDataView(): TextView? {
                    return null
                }
            }
        if(isAdded){
            rvFoodCategory.adapter = foodCategoryAdapter
            rvFoodCategory.layoutManager = LinearLayoutManager(activity!!,LinearLayoutManager.HORIZONTAL,false)
            rvFoodCategory.itemAnimator = DefaultItemAnimator()
            rvFoodCategory.isNestedScrollingEnabled = true
        }

    }

    fun setFoodAdaptor(isFirstTime: Boolean, fristTimeSelect: Boolean) {
       if(isFirstTime){
           if(fristTimeSelect){
               setFoodCategoryAdaptor()
           }
           foodAdapter = object : BaseAdaptor<FoodItemModel>(activity!!,foodList!!) {
               @SuppressLint("NewApi", "ResourceType")
               override fun onBindData(
                   holder: RecyclerView.ViewHolder?,
                   `val`: FoodItemModel,
                   position: Int
               ) {
                   val tvFoodName: TextView = holder!!.itemView.findViewById(R.id.tvFoodName)
                   val tvFoodPriceList: TextView = holder.itemView.findViewById(R.id.tvFoodPriceList)
                   val tvFoodPriceGrid: TextView = holder.itemView.findViewById(R.id.tvFoodPriceGrid)
                   val ivFood: ImageView = holder.itemView.findViewById(R.id.ivFood)
                   val icLike: ImageView = holder.itemView.findViewById(R.id.icLike)
                   tvFoodName.text = foodList!!.get(position).getItem_name()
                   tvFoodPriceList.text = getCurrancy(activity!!).plus(foodList!!.get(position).getItem_price())
                   tvFoodPriceGrid.text = getCurrancy(activity!!).plus(foodList!!.get(position).getItem_price())
                   Glide.with(activity!!).load(foodList!!.get(position).getItemimage()!!.getImage()).placeholder(activity!!.resources.getDrawable(R.drawable.placeholder)).into(ivFood)
                   holder.itemView.setOnClickListener {
                       startActivity(Intent(activity!!,FoodDetailActivity::class.java).putExtra("foodItemId",foodList!!.get(position).getId()))
                   }
                   if(SharePreference.getBooleanPref(activity!!,isLinearLayoutManager)){
                       tvFoodPriceList.visibility= View.VISIBLE
                       tvFoodPriceGrid.visibility= View.GONE
                   }else{
                       tvFoodPriceList.visibility= View.GONE
                       tvFoodPriceGrid.visibility= View.VISIBLE
                   }

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
                           map.put("user_id",SharePreference.getStringPref(activity!!,SharePreference.userId)!!)
                           if(isCheckNetwork(activity!!)){
                               callApiFavourite(map,position)
                           }else{
                               alertErrorOrValidationDialog(
                                   activity!!,
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
           if(isAdded){
               rvFoodSubcategory.adapter = foodAdapter
               rvFoodSubcategory.itemAnimator = DefaultItemAnimator()
               rvFoodSubcategory.isNestedScrollingEnabled = true
           }
       }else{
           foodAdapter!!.notifyDataSetChanged()
       }
    }

    private fun callApiFavourite(hasmap:HashMap<String, String>,pos:Int) {
        showLoadingProgress(activity!!)
        val call = ApiClient.getClient.setAddFavorite(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(call: Call<SingleResponse>, response: Response<SingleResponse>) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.getStatus().equals("1")) {
                        dismissLoadingProgress()
                        foodList!!.get(pos).setIs_favorite("1")
                        foodAdapter!!.notifyDataSetChanged()
                    } else if (restResponse.getStatus().equals("0")) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            activity!!,
                            restResponse.getMessage()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }
}