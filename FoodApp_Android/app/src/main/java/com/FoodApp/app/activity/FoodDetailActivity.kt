package com.FoodApp.app.activity

import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.FoodApp.app.R
import com.FoodApp.app.adaptor.ImageSliderAdaptor
import com.FoodApp.app.api.SingleResponse
import com.FoodApp.app.base.BaseActivity
import com.FoodApp.app.base.BaseAdaptor
import com.FoodApp.app.model.IngredientsModel
import com.FoodApp.app.model.ItemDetailModel
import kotlinx.android.synthetic.main.activity_foodorderdetail.*
import java.util.*
import com.FoodApp.app.utils.Common.alertErrorOrValidationDialog
import com.FoodApp.app.utils.Common.dismissLoadingProgress
import com.FoodApp.app.utils.Common.isCheckNetwork
import com.FoodApp.app.utils.Common.showLoadingProgress
import com.FoodApp.app.utils.SharePreference.Companion.getStringPref
import com.FoodApp.app.utils.SharePreference.Companion.userId
import com.FoodApp.app.api.*
import kotlinx.android.synthetic.main.row_ingrediants.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class FoodDetailActivity: BaseActivity() {
    var timer: Timer? = null
    private var currentPage = 0
    private var imagelist: ArrayList<String>? = null
    private var imagelistPlaceHolder: ArrayList<Drawable>? = null
    var itemModel:ItemDetailModel?=null
    override fun setLayout(): Int {
        return R.layout.activity_foodorderdetail
    }
    override fun InitView() {
        imagelist = ArrayList()
        imagelistPlaceHolder = ArrayList()
        if(isCheckNetwork(this@FoodDetailActivity)){
            callApiFoodDetail(intent.getStringExtra("foodItemId")!!)
        }else{
            alertErrorOrValidationDialog(this@FoodDetailActivity, resources.getString(R.string.no_internet))
        }

        tvAddtoCart.setOnClickListener {
            if(isCheckNetwork(this@FoodDetailActivity)){
                callApiAddToCart(itemModel!!)
            }else{
                alertErrorOrValidationDialog(this@FoodDetailActivity, resources.getString(R.string.no_internet))
            }
        }

        ivBack.setOnClickListener {
            finish()
        }
        ivCart.setOnClickListener {
            openActivity(CartActivity::class.java)
            finish()
        }
    }

    private fun loadPagerImages(imageHase: ArrayList<*>) {
        val adapter = ImageSliderAdaptor(this@FoodDetailActivity, imageHase)
        tabLayout.setupWithViewPager(viewPager, true)
        viewPager.setAdapter(adapter)
        val handler = Handler()

        if (imageHase.size == 1) {
            tabLayout.setVisibility(View.GONE)
        }
        val Update = Runnable {
            if (currentPage == imageHase.size) {
                currentPage = 0
            }
            viewPager.setCurrentItem(currentPage++, true)
        }

        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            // task to be scheduled
            override fun run() {
                handler.post(Update)
            }
        }, 500, 3000)


        viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                currentPage = position
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onPause() {
        super.onPause()
        if (timer != null)
            timer!!.cancel()
    }


    override fun onResume() {
        super.onResume()
        timer = Timer()
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == imagelist!!.size) {
                currentPage = 0
            }
            viewPager.setCurrentItem(currentPage++, true)
        }

        if (imagelist!!.size == 1) {
            tabLayout.visibility = View.GONE
        }
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, 4000, 3000)
    }


    private fun callApiFoodDetail(strFoodId:String) {
        showLoadingProgress(this@FoodDetailActivity)
        val map = HashMap<String, String>()
        map.put("item_id", strFoodId)
        val call = ApiClient.getClient.setItemDetail(map)
        call.enqueue(object : Callback<RestResponse<ItemDetailModel>> {
            override fun onResponse(
                call: Call<RestResponse<ItemDetailModel>>,
                response: Response<RestResponse<ItemDetailModel>>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: RestResponse<ItemDetailModel> = response.body()!!
                    setRestaurantData(restResponce.getData())
                }
            }

            override fun onFailure(call: Call<RestResponse<ItemDetailModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@FoodDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiAddToCart(itemDetailModel: ItemDetailModel) {
        showLoadingProgress(this@FoodDetailActivity)
        val map = HashMap<String, String>()
        map.put("item_id",itemDetailModel.getId()!!)
        map.put("qty","1")
        map.put("price",itemDetailModel.getItem_price()!!)
        map.put("user_id", getStringPref(this@FoodDetailActivity,userId)!!)
        val call = ApiClient.getClient.setAddToCart(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: SingleResponse = response.body()!!
                    if(restResponce.getStatus().equals("1")){
                        alertErrorOrValidationDialog(
                            this@FoodDetailActivity,
                            restResponce.getMessage()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@FoodDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun setRestaurantData(restResponce: ItemDetailModel?) {
        itemModel=restResponce
        if(restResponce!!.getImages()!!.size>0){
            val listImage=ArrayList<String>()
            for(i in 0 until restResponce.getImages()!!.size){
                listImage.add(restResponce.getImages()!!.get(i).getItemimage()!!)
            }
            loadPagerImages(listImage)
        }else{
            imagelistPlaceHolder!!.add(resources.getDrawable(R.drawable.placeholder))
            loadPagerImages(imagelistPlaceHolder!!)
        }

        if(restResponce.getIngredients()!!.size>0){
            rvIngredients.visibility=View.VISIBLE
            tvNoDataFound.visibility=View.GONE
            setItemIngrdiantsAdaptor(restResponce.getIngredients()!!)
        }else{
            rvIngredients.visibility=View.GONE
            tvNoDataFound.visibility=View.VISIBLE
        }

        tvFoodName.text= restResponce.getItem_name()
        tvFoodPrice.text="$"+restResponce.getItem_price()
        tvFoodType.text=restResponce.getCategory_name()
        tvTime.text=restResponce.getDelivery_time()
        tvDetail.text=restResponce.getItem_description()
    }

    private fun setItemIngrdiantsAdaptor(ingredientsItemList: ArrayList<IngredientsModel>) {
        val cartItemAdapter = object : BaseAdaptor<IngredientsModel>(this@FoodDetailActivity, ingredientsItemList) {
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: IngredientsModel,
                position: Int
            ) {
                Glide.with(this@FoodDetailActivity).load(ingredientsItemList.get(position).getIngredients_image()).into(holder!!.itemView.ivIngrediants)
            }

            override fun setItemLayout(): Int {
                return R.layout.row_ingrediants
            }

            override fun setNoDataView(): TextView? {
                return null
            }
        }
        rvIngredients.adapter = cartItemAdapter
        rvIngredients.layoutManager = LinearLayoutManager(this@FoodDetailActivity,LinearLayoutManager.HORIZONTAL,false)
        rvIngredients.itemAnimator = DefaultItemAnimator()
        rvIngredients.isNestedScrollingEnabled = true
    }
}