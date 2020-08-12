package com.FoodApp.app.base


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.FoodApp.app.R


abstract class BaseFragmnet : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(setView(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Init(view)
    }

    protected abstract fun setView(): Int
    protected abstract fun Init(view: View)

    open fun openActivity(destinationClass: Class<*>?) {
        startActivity(Intent(activity, destinationClass))
       // activity?.overridePendingTransition(R.anim.fad_in, R.anim.fad_out)
        //activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    open fun openClearTopActivity(destinationClass: Class<*>?) {
        val intent = Intent(activity, destinationClass)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity?.overridePendingTransition(R.anim.fad_in, R.anim.fad_out)
    }
}



