package com.varitas.gokulpos.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.varitas.gokulpos.R
import com.varitas.gokulpos.activity.DashboardActivity
import com.varitas.gokulpos.activity.LoginActivity
import com.varitas.gokulpos.activity.MainApp
import com.varitas.gokulpos.utilities.PreferenceData
import com.varitas.gokulpos.utilities.SharedPreferencesKeys
import com.varitas.gokulpos.utilities.Utils

class SellerFirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("SellerFirebaseService ", "Refreshed token :: $token") // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) { // TODO : send token to tour server
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.e("SellerFirebaseService ", "Message :: $message")

        if (message.notification != null) {
            showNotification(message.notification!!.title!!, message.notification!!.body!!)
        }
    }

    //region To show notifications
    private fun showNotification(title: String, message: String) {
        try {

            // Pass the intent to switch to the MainActivity
            val isLogin = PreferenceData.getPrefBoolean(key = SharedPreferencesKeys.IS_LOGIN, defaultValue = false)
            val intent = Intent(MainApp.mainApp!!, if (isLogin) DashboardActivity::class.java else LoginActivity::class.java) // Assign channel ID
            val channelId = "notification_channel"

            // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear // the activities present in the activity stack, // on the top of the Activity that is to be launched
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            // Pass the intent to PendingIntent to start the
            // next Activity
            val pendingIntent = PendingIntent.getActivity(MainApp.mainApp!!, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

            // Create a Builder object using NotificationCompat
            // class. This will allow control over all the flags
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(MainApp.mainApp!!, channelId).setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.ic_gss_logo).setAutoCancel(true).setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000)).setOnlyAlertOnce(true).setContentIntent(pendingIntent)

            // A customized design for the notification can be
            // set only for Android versions 4.1 and above. Thus
            // condition for the same is checked here.
            // builder = builder.setContent(getCustomDesign(title, message))
            // Create an object of NotificationManager class to
            // notify the
            // user of events that happen in the background.
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            // Check if the Android Version is greater than Oreo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(channelId, "web_app", NotificationManager.IMPORTANCE_HIGH)
                notificationManager!!.createNotificationChannel(notificationChannel)
            }
            notificationManager!!.notify(0, builder.build())
        } catch (ex: Exception) {
            Utils.printAndWriteException(ex)
        }
    } //endregion
}