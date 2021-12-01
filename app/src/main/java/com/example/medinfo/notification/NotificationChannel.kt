package com.example.medinfo.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

class NotificationChannel(val context: Context) {
    val CHANNEL_1 = "Channel1";
    val CHANNEL_2 = "Channel2";

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_0_1) {

            val channel1 =
                NotificationChannel(CHANNEL_1, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel1.description = "High Priority Channel";

            val channel2 =
                NotificationChannel(CHANNEL_2, "Channel 2", NotificationManager.IMPORTANCE_LOW);
            channel2.description = "Low Priority Channel";

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);

        }

    }

}