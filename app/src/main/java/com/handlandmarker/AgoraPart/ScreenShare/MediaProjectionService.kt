package com.handlandmarker.AgoraPart.ScreenShare

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class MediaProjectionService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Obtain the media projection here
        val CHANNEL_ID = "My_Channel_ID"

// Notification channel name.
        val CHANNEL_NAME = "My_Channel_Name"

// Notification channel importance level.
        val CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT

// Create the notification channel.
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_IMPORTANCE)

// Register the notification channel with the system.
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

// Create the notification.
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Title")
            .setContentText("Content")

            .build()


        // Step 2: Start the service in foreground with the notification
        startForeground(1, notification)

        // Step 3: Introduce a 2-second delay
        Handler().postDelayed({
            // Proceed with media projection (startMediaProjection())
            // ...
        }, 2000) // 2 seconds delay
        Log.d("MediaProjectionService", "Media projection obtained")
        return START_NOT_STICKY
    }
}