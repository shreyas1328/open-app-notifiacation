package com.shreyas.open_app_notifiacation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.work.ListenableWorker.Result.Success
import androidx.work.Worker
import androidx.work.WorkerParameters

class ScheduleTask(private val context: Context, private val params:WorkerParameters) : Worker(context, params) {



    @SuppressLint("RestrictedApi")
    override fun doWork(): Result {

        Log.d("work_123", "doWork:  ")
        Handler(Looper.getMainLooper()).post {
            Prefs.showFlashMsg(context, R.layout.dummy, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, false)
        }
        return Success()
    }

}