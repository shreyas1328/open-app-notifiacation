package com.shreyas.open_app_notifiacation

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.util.Log

enum class ServiceState {
    STARTED,
    STOPPED,
}

private const val name = "SPYSERVICE_KEY"
private const val key = "SPYSERVICE_STATE"

fun setServiceState(context: Context, state: ServiceState) {
    val sharedPrefs = getPreferences(context)
    sharedPrefs.edit().let {
        it.putString(key, state.name)
        it.apply()
    }
}

fun getServiceState(context: Context): ServiceState {
    val sharedPrefs = getPreferences(context)
    val value = sharedPrefs.getString(key, ServiceState.STOPPED.name)
    return ServiceState.valueOf(value!!)
}

private fun getPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(name, 0)
}


 fun createNotification1(context: Context): Notification {
    val notificationChannelId = "CALL CHANNEL ID"

     Log.d("vvv34", "notify:   ")

    // depending on the Android API that we're dealing with we will have
    // to use a specific method to create the notification
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationManager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
        val channel = NotificationChannel(
            notificationChannelId,
            "Call Service notifications channel",
            NotificationManager.IMPORTANCE_HIGH
        ).let {
            it.description = "Call Service channel"
            it.enableLights(true)
            it.lightColor = Color.RED
            it
        }
        notificationManager.createNotificationChannel(channel)
    }

    val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java).let { notificationIntent ->
        PendingIntent.getActivity(context, 0, notificationIntent, 0)
    }

    val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
        context,
        notificationChannelId
    ) else Notification.Builder(context)

    return builder
        .setContentTitle("Times up")
        .setAutoCancel(false)
        .setContentText("your time is upp")
        .setContentIntent(pendingIntent)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setTicker("Ticker text")
        .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
        .build()
 }