package com.FoodApp.app.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.FoodApp.app.R
import com.FoodApp.app.activity.DashboardActivity
import com.FoodApp.app.activity.LoginActivity
import com.FoodApp.app.utils.Common.getLog
import com.FoodApp.app.utils.SharePreference.Companion.getBooleanPref
import com.FoodApp.app.utils.SharePreference.Companion.isLogin
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService() : FirebaseMessagingService() {

    var TAG = "NotificationService"

    private val REQUEST_CODE = 1
    private var NOTIFICATION_ID = 6578
    var ctx: Context? = null
    var Notification_message: String? = null

    @SuppressLint("WrongThread")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        //        activity = (Activity) getApplicationContext();
        ctx = applicationContext
        getLog(TAG, "onMessageReceived: $remoteMessage")
        getLog(
            TAG,
            "onMessageReceived: From" + remoteMessage.from
        )
        val Notification_title = remoteMessage.data["title"]
        Notification_message = remoteMessage.data["message"]
        val Notification_data = remoteMessage.data["data"]
        val Notification_status = remoteMessage.data["status"]
        getLog(
            "onMessageReceived=== Notification_title",
            "" + Notification_title
        )
        getLog(
            "onMessageReceived=== Notification_message",
            "" + Notification_message
        )
        getLog(
            "onMessageReceived=== Notification_data",
            "" + Notification_data
        )
        getLog(
            "onMessageReceived=== Notification_status",
            "" + Notification_status
        )

        // showNotifications(Notification_title, Notification_message, "");

        //onIncomingCalling("","","");
        onResponseBody(Notification_title)
    }

    fun onResponseBody(strResponseBody: String?) {
        showNotifications(strResponseBody!!, Notification_message)
    }


    private fun showNotifications(
        strTitle: String,
        strMessage: String?
    ) {
        var intent: Intent? = null
        intent = if (getBooleanPref(this, isLogin))
            {
                Intent(this, DashboardActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
        val channelId = "channel-01"
        val channelName = "Channel Name"
        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel =
                NotificationChannel(channelId, channelName, importance)
            manager.createNotificationChannel(mChannel)
            val mBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(getIcon())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle(strTitle)
                .setContentText(strMessage)
            val stackBuilder =
                TaskStackBuilder.create(this)
            stackBuilder.addNextIntent(intent)
            val resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            mBuilder.setContentIntent(resultPendingIntent)
            manager.notify(NOTIFICATION_ID, mBuilder.build())
        } else {
            val pendingIntent = PendingIntent.getActivity(
                this, REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notification = NotificationCompat.Builder(this)
                .setContentTitle(strTitle)
                .setContentText(strMessage)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .setSmallIcon(getIcon())
                .build()
            manager.notify(NOTIFICATION_ID, notification)
        }
        NOTIFICATION_ID++
    }

    private fun getIcon(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) R.drawable.ic_small_notification else R.drawable.ic_small_notification
    }
}