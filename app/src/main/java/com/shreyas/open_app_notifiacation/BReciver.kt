package com.shreyas.open_app_notifiacation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.WindowManager
import androidx.work.*
import java.util.concurrent.TimeUnit

class BReciver :BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("sdad", "sads:   "+intent!!.action )
        Prefs.showFlashMsg(context, R.layout.flash_msg, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true)

        scheduleTask(context)
    }

    companion object {
         fun scheduleTask(context: Context) {

            val wm = WorkManager.getInstance(context)
            val request = OneTimeWorkRequest.Builder(ScheduleTask::class.java)
                .addTag("MYTASK")
                .setInitialDelay(5, TimeUnit.MINUTES)
                .build()
            wm.enqueue(request)
        }
    }
}