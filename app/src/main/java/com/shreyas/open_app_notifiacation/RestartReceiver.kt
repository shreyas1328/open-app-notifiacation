package com.shreyas.open_app_notifiacation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class RestartReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        Log.d("work_123", "sadas: "+intent!!.action)

        if (intent!!.action.equals("android.intent.action.BOOT_COMPLETED")) {
            if (intent.action == Intent.ACTION_BOOT_COMPLETED && getServiceState(context) == ServiceState.STARTED) {
                Intent(context, EndlessService::class.java).also {
                    it.action = Actions.START.name
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        log("Starting the service in >=26 Mode from a BroadcastReceiver")
                        context.startForegroundService(it)
                        return
                    }
                    log("Starting the service in < 26 Mode from a BroadcastReceiver")
                    context.startService(it)
                }
            }
        }
    }

}