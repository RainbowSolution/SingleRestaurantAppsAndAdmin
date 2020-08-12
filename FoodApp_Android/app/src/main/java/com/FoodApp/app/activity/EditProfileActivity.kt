package com.FoodApp.app.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.FoodApp.app.R
import com.FoodApp.app.api.SingleResponse
import com.FoodApp.app.base.BaseActivity
import com.FoodApp.app.model.ProfileModel
import com.FoodApp.app.utils.Common.alertErrorOrValidationDialog
import com.FoodApp.app.utils.Common.dismissLoadingProgress
import com.FoodApp.app.utils.Common.getLog
import com.FoodApp.app.utils.Common.isCheckNetwork
import com.FoodApp.app.utils.Common.isProfileEdit
import com.FoodApp.app.utils.Common.isProfileMainEdit
import com.FoodApp.app.utils.Common.setImageUpload
import com.FoodApp.app.utils.Common.setRequestBody
import com.FoodApp.app.utils.Common.settingDialog
import com.FoodApp.app.utils.Common.showLoadingProgress
import com.FoodApp.app.utils.SharePreference
import com.FoodApp.app.api.*
import com.FoodApp.app.utils.Common
import kotlinx.android.synthetic.main.activity_editprofile.*
import kotlinx.android.synthetic.main.dlg_externalstorage.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class EditProfileActivity:BaseActivity() {
    private val SELECT_FILE = 201
    private val REQUEST_CAMERA = 202
    private var mSelectedFileImg: File? = null
    override fun setLayout(): Int {
        return R.layout.activity_editprofile
    }

    override fun InitView() {
        if (isCheckNetwork(this@EditProfileActivity)) {
            val hasmap = HashMap<String, String>()
            hasmap.put("user_id", SharePreference.getStringPref(this@EditProfileActivity, SharePreference.userId)!!)
            callApiProfile(hasmap)
        } else {
            alertErrorOrValidationDialog(
                this@EditProfileActivity,
                resources.getString(R.string.no_internet)
            )
        }
    }

    fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.tvUpdate->{
                if (edUserName.text.toString().equals("")) {
                    alertErrorOrValidationDialog(this@EditProfileActivity, resources.getString(R.string.validation_name))
                } else {
                    if (isCheckNetwork(this@EditProfileActivity)) {
                        mCallApiEditProfile()
                    } else {
                        alertErrorOrValidationDialog(this@EditProfileActivity, resources.getString(R.string.no_internet))
                    }
                }
            }
            R.id.ivGellary->{
                getExternalStoragePermission()
            }

        }
    }

    private fun callApiProfile(hasmap: HashMap<String, String>) {
        showLoadingProgress(this@EditProfileActivity)
        val call = ApiClient.getClient.getProfile(hasmap)
        call.enqueue(object : Callback<RestResponse<ProfileModel>> {
            override fun onResponse(call: Call<RestResponse<ProfileModel>>, response: Response<RestResponse<ProfileModel>>) {
                if (response.code() == 200) {
                    val restResponce: RestResponse<ProfileModel> = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        dismissLoadingProgress()
                        val dataResponse: ProfileModel = restResponce.getData()!!
                        setProfileData(dataResponse)
                    } else if (restResponce.getData()!!.equals("0")) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@EditProfileActivity,
                            restResponce.getMessage()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<ProfileModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@EditProfileActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun setProfileData(dataResponse: ProfileModel) {
        edEmailAddress.setText(dataResponse.getEmail())
        edUserName.setText(dataResponse.getName())
        edMobileNumber.setText(dataResponse.getMobile())
        Glide.with(this@EditProfileActivity).load(dataResponse.getProfile_image()).placeholder(resources.getDrawable(R.drawable.ic_placeholder)).into(ivProfile)
    }


    /*-------------Image Upload Code-------------*/
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data)
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data!!)
            }
        }
    }

    fun getExternalStoragePermission() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        imageSelectDialog(this@EditProfileActivity)
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        Common.settingDialog(this@EditProfileActivity)
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()
    }

    @SuppressLint("InlinedApi")
    fun imageSelectDialog(act: Activity) {
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
            dialog.setCancelable(true)
            val m_inflater = LayoutInflater.from(act)
            val m_view = m_inflater.inflate(R.layout.dlg_externalstorage, null, false)

            val finalDialog: Dialog = dialog
            m_view.tvSetImageCamera.setOnClickListener {
                finalDialog.dismiss()
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(
                    intent,
                    REQUEST_CAMERA
                )
            }
            m_view.tvSetImageGallery.setOnClickListener {
                finalDialog.dismiss()
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_PICK
                //   intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE)
            }
            dialog.setContentView(m_view)
            if (!act.isFinishing) dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun onSelectFromGalleryResult(data: Intent?) {
        var bm: Bitmap? = null
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(
                    applicationContext.contentResolver,
                    data.data
                )
                val selectedImageUri = data.data
              //  bm=getCorrectlyOrientedImage(this@EditProfileActivity,selectedImageUri,340)
                val bytes = ByteArrayOutputStream()
                bm!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
                mSelectedFileImg = File(
                    Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis().toString() + ".jpg"
                )
                val fo: FileOutputStream
                try {
                    mSelectedFileImg!!.createNewFile()
                    fo = FileOutputStream(mSelectedFileImg!!)
                    fo.write(bytes.toByteArray())
                    fo.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        ivProfile.setImageBitmap(bm);
    }

    private fun onCaptureImageResult(data: Intent) {
        val thumbnail = data.extras!!["data"] as Bitmap?
        val bytes = ByteArrayOutputStream()
        thumbnail!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

        mSelectedFileImg = File(
            Environment.getExternalStorageDirectory(),
            System.currentTimeMillis().toString() + ".jpeg"
        )
        val fo: FileOutputStream
        try {
            mSelectedFileImg!!.createNewFile()
            fo = FileOutputStream(mSelectedFileImg)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Glide.with(this@EditProfileActivity)
            .load(Uri.parse("file://" + mSelectedFileImg!!.getPath()))
            .into(ivProfile)
    }



    private fun mCallApiEditProfile() {
        showLoadingProgress(this@EditProfileActivity)
        var call: Call<SingleResponse>?=null
        getLog("File_Path",mSelectedFileImg!!.absolutePath)
        getLog("File_Path",mSelectedFileImg!!.isFile.toString())
        if(mSelectedFileImg!=null){
            call= ApiClient.getClient.setProfile(setRequestBody(SharePreference.getStringPref(this@EditProfileActivity,SharePreference.userId)!!),setRequestBody(edUserName.text.toString()),setImageUpload("image",mSelectedFileImg!!))
        }else {
            call= ApiClient.getClient.setProfile(setRequestBody(SharePreference.getStringPref(this@EditProfileActivity,SharePreference.userId)!!),setRequestBody(edUserName.text.toString()),null)
        }
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(call: Call<SingleResponse>, response: Response<SingleResponse>) {
                val editProfileResponce: SingleResponse = response.body()!!
                if(editProfileResponce.getStatus().equals("1")){
                    dismissLoadingProgress()
                    isProfileEdit=true
                    isProfileMainEdit=true
                    successfulDialog(this@EditProfileActivity, editProfileResponce.getMessage())
                }else if(editProfileResponce.getStatus().equals("0")){
                    dismissLoadingProgress()
                    alertErrorOrValidationDialog(this@EditProfileActivity,editProfileResponce.getMessage())
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@EditProfileActivity,
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

    fun getCorrectlyOrientedImage(
        context: Context,
        photoUri: Uri?,
        maxWidth: Int
    ): Bitmap? {
        var `is`: InputStream = context.getContentResolver().openInputStream(photoUri!!)!!
        val dbo: BitmapFactory.Options = BitmapFactory.Options()
        dbo.inJustDecodeBounds = true
        BitmapFactory.decodeStream(`is`, null, dbo)
        `is`.close()
        val rotatedWidth: Int
        val rotatedHeight: Int
        val orientation = getOrientation(context, photoUri)
        if (orientation == 90 || orientation == 270) {
            Log.d("ImageUtil", "Will be rotated")
            rotatedWidth = dbo.outHeight
            rotatedHeight = dbo.outWidth
        } else {
            rotatedWidth = dbo.outWidth
            rotatedHeight = dbo.outHeight
        }
        var srcBitmap: Bitmap
        `is` = context.getContentResolver().openInputStream(photoUri)!!
        Log.d(
            "ImageUtil", String.format(
                "rotatedWidth=%s, rotatedHeight=%s, maxWidth=%s",
                rotatedWidth, rotatedHeight, maxWidth
            )
        )
        if (rotatedWidth > maxWidth || rotatedHeight > maxWidth) {
            val widthRatio =
                rotatedWidth.toFloat() / maxWidth.toFloat()
            val heightRatio =
                rotatedHeight.toFloat() / maxWidth.toFloat()
            val maxRatio = Math.max(widthRatio, heightRatio)
            Log.d(
                "ImageUtil", String.format(
                    "Shrinking. maxRatio=%s",
                    maxRatio
                )
            )

            // Create the bitmap from file
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inSampleSize = maxRatio.toInt()
            srcBitmap = BitmapFactory.decodeStream(`is`, null, options)!!
        } else {
            Log.d(
                "ImageUtil", String.format(
                    "No need for Shrinking. maxRatio=%s",
                    1
                )
            )
            srcBitmap = BitmapFactory.decodeStream(`is`)
            Log.d("ImageUtil", String.format("Decoded bitmap successful"))
        }
        `is`.close()

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */if (orientation > 0) {
            val matrix = Matrix()
            matrix.postRotate(orientation.toFloat())
            srcBitmap = Bitmap.createBitmap(
                srcBitmap, 0, 0, srcBitmap.width,
                srcBitmap.height, matrix, true
            )
        }
        return srcBitmap
    }

    fun getOrientation(context: Context, photoUri: Uri?): Int {
        val cursor: Cursor = context.getContentResolver().query(
            photoUri!!,
            arrayOf(MediaStore.Images.ImageColumns.ORIENTATION),
            null,
            null,
            null
        )!!
        if (cursor != null || cursor.getCount() !=1) {
            return 90 //Assuming it was taken portrait
        }
        cursor.moveToFirst()
        return cursor.getInt(0)
    }
}