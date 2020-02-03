package com.shreyas.open_app_notifiacation

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_usage.setOnClickListener(View.OnClickListener {
           openUsageSetting()
        })

        btn_screen_overlay.setOnClickListener(View.OnClickListener {
            Permissions.screenOverlay(this)
        })

        btn_autostart.setOnClickListener(View.OnClickListener {
            Permissions.autoStart(this)
        })

        btnStartService.setOnClickListener(View.OnClickListener {
            log("START THE FOREGROUND SERVICE ON DEMAND")
            actionOnService(Actions.START)
        })

        btnStopService.setOnClickListener(View.OnClickListener {
            log("STOP THE FOREGROUND SERVICE ON DEMAND")
            actionOnService(Actions.STOP )
        })
    }

    private fun openUsageSetting() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }

    private fun actionOnService(action: Actions) {
        log((getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP).toString())
        if (getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP) return
        Intent(this, EndlessService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                log("Starting the service in >=26 Mode")
                startForegroundService(it)
                return
            }
            log("Starting the service in < 26 Mode")
            startService(it)
        }
    }

    fun getForgroundPackage(): String? {
       //no inspection ResourceType
       val mUsageStatsManager =
           this.applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
       val time = System.currentTimeMillis()

// We get usage stats for the last 10 seconds
       val stats = mUsageStatsManager.queryUsageStats(
           UsageStatsManager.INTERVAL_DAILY,
           time - 1000 * 10,
           time
       )

// Sort the stats by the last time used
       if (stats != null) {
           val mySortedMap: SortedMap<Long, UsageStats> = TreeMap()
           for (usageStats in stats) {
               mySortedMap.put(usageStats.lastTimeUsed, usageStats)
           }
           if (mySortedMap != null && !mySortedMap.isEmpty()) {
               return mySortedMap.get(mySortedMap.lastKey())!!.getPackageName()
           }
       }

       return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}
