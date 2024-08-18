package com.skilrock.infinitylotoapp.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.ui.activity.HomeKenyaActivity
import com.skilrock.infinitylotoapp.utility.NOTIFICATION_CHANNEL_ALERT
import com.skilrock.infinitylotoapp.utility.NOTIFICATION_CHANNEL_PROMOTIONAL
import com.skilrock.infinitylotoapp.utility.NOTIFICATION_CHANNEL_TRANSACTIONAL
import com.skilrock.infinitylotoapp.utility.SharedPrefUtils

class SabanzuriFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)

        Log.d("log", "New Token: $newToken")
        SharedPrefUtils.setFcmToken(this, newToken)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data
        Log.d("log", "Title: " + data["title"])
        Log.d("log", "Notification Message Body: " + data["body"])

        val displayNotification = data["displayNotification"] ?: "YES"
        if (displayNotification.equals("YES", true))
            showNotification(this, data["title"] ?: "NA", data["body"] ?: "NA", data["channel"] ?: "NA")
    }

    private fun showNotification(context: Context, title: String, body: String, channel: String) {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1

        val channelId: String
        val channelName: String

        when (channel) {
            NOTIFICATION_CHANNEL_TRANSACTIONAL -> {
                channelId   = NOTIFICATION_CHANNEL_TRANSACTIONAL
                channelName = "These notifications are for transactional purpose."
            }
            NOTIFICATION_CHANNEL_ALERT -> {
                channelId   = NOTIFICATION_CHANNEL_ALERT
                channelName = "These notifications are for alert purpose."
            }
            else -> {
                channelId   = NOTIFICATION_CHANNEL_PROMOTIONAL
                channelName = "These notifications are for promotion purpose."
            }
        }

        val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.notification_tone)
        val attributes: AudioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, channelName, importance)
            mChannel.enableLights(true)
            mChannel.enableVibration(true)
            mChannel.setSound(sound, attributes)
            notificationManager.createNotificationChannel(mChannel)
        }

        val pendingIntent = Intent(this, HomeKenyaActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val notifyPendingIntent = PendingIntent.getActivity(this, 0, pendingIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.sabanzuri_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(notifyPendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, mBuilder.build())
    }
}