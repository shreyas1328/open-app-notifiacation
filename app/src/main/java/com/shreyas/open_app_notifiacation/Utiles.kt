package com.shreyas.open_app_notifiacation

import android.content.Context
import android.util.Log

val SHAREDNAME:String = "MY_APPS"
val KEY_TIME:String = "STOREDTIME"


fun log(msg: String) {
    Log.d("ENDLESS-SERVICE", msg)
}

fun setStoreTime(context:Context, sec: Long) {
    val prefernce = context.getSharedPreferences(SHAREDNAME, 0)
    val editor = prefernce.edit()
    editor.putLong(KEY_TIME, sec)
    editor.apply()
}

fun getStoreTime(context: Context):Long {
    val preferences = context.getSharedPreferences(SHAREDNAME, 0)
    return  preferences.getLong(KEY_TIME, 60000)
}