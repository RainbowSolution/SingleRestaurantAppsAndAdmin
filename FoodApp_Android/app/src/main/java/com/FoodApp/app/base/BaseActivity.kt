package com.FoodApp.app.base


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.FoodApp.app.R
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(setLayout())
        InitView()
    }

    protected abstract fun setLayout(): Int
    protected abstract fun InitView()

    open fun openActivity(destinationClass: Class<*>) {
        startActivity(Intent(this@BaseActivity, destinationClass))
       // overridePendingTransition(R.anim.enter, R.anim.exit)
    }

    open fun openClearTopActivity(destinationClass: Class<*>) {
        val intent = Intent(this@BaseActivity, destinationClass)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent);
       // overridePendingTransition(R.anim.enter, R.anim.exit)
    }

    /* open fun openIntent(intent: Intent) {
         startActivity(intent)
         overridePendingTransition(R.anim.enter, R.anim.exit)
     }*/

    open fun openClearTopIntent(intent: Intent) {
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        overridePendingTransition(R.anim.enter, R.anim.exit)
    }

    open fun openIntent(destinationClass: Class<*>?) {
        val intent = Intent(this@BaseActivity, destinationClass)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    open fun getActivity(): Activity? {
        return this@BaseActivity
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(R.anim.fad_in, R.anim.fad_out)
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.fad_in, R.anim.fad_out)
    }
}