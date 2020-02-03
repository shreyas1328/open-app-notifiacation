package com.shreyas.open_app_notifiacation

import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class EndlessService : Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand executed with startId: $startId")
        if (intent != null) {
            val action = intent.action
            log("using an intent with action $action")
            when (action) {
                Actions.START.name -> startService(intent, startId)
                Actions.STOP.name -> stopService()
                else -> {
                    log("This should never happen. No action in the received intent")
                    startService(intent, startId)
                    setServiceState(this, ServiceState.STARTED)
                }
            }
        } else {
            log(
                "with a null intent. It has been probably restarted by the system."
            )
        }
        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        log("The service has been created".toUpperCase())
        val notification = createNotification()
        startForeground(1, notification)

    }

    private fun createNotification(): Notification? {
        val notificationChannelId = "ENDLESS SERVICE CHANNEL"


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
            val channel = NotificationChannel(
                notificationChannelId,
                "Endless Service notifications channel",
                NotificationManager.IMPORTANCE_LOW
            ).let {
                it.description = "App is up and running"
                it.enableLights(true)
                it.lightColor = Color.RED
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val builder: Notification.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
                this,
                notificationChannelId
            ) else Notification.Builder(this)

        return builder
            .setContentTitle("App is up and running")
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setTicker("Ticker text")
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()

    }

    override fun onDestroy() {
        super.onDestroy()
        log("The service has been destroyed".toUpperCase())
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show()
    }

    //
    private fun startService(intent: Intent, startId: Int) {
        if (isServiceStarted) return
        log("Starting the foreground service task")
        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show()
        isServiceStarted = true
        setServiceState(this, ServiceState.STARTED)

        // when phone sleeps
//        wakeLock =
//            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
//                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
//                    acquire()
//                }
//            }

        // we are starting a loop in a coroutine
        GlobalScope.launch(Dispatchers.IO) {
            while (isServiceStarted) {
                launch(Dispatchers.IO) {
                    pingFakeServer(intent)
                }
                //initating the service depending upon delay. It plays a major role
                delay(1000)
            }
            log("End of the loop for the service")
        }
    }

    private fun stopService() {
        log("Stopping the foreground service")
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show()
        try {
//            wakeLock?.let {
//                if (it.isHeld) {
//                    it.release()
//                }
//            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            log("Service stopped without being started: ${e.message}")
        }
        isServiceStarted = false
        setServiceState(this, ServiceState.STOPPED)
    }

    private fun pingFakeServer(intent: Intent) {
        detials()
    }

    private fun detials() {
        getForgroundPackage()
        Log.d("test_999","name: "+Prefs.getAppValue(this))
        Log.d("test_999","name: "+(Prefs.checkList(Prefs.getAppValue(this), Prefs.exceptions)))

        Prefs.reset(this, Prefs.getAppValue(this))

        val phoneState = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (phoneState.isDeviceLocked) {
            try {
                WorkManager.getInstance(this).cancelAllWorkByTag("MYTASK")
                Prefs.setNEWAppValue(this, "null")
            }catch (e: java.lang.Exception) {
                Log.d("work_123", "sda:  "+e.message)
            }
        }else {
            startBroadcast()
        }
    }

    private fun startBroadcast() {
        Log.d("tet554","sd: "+(Prefs.getAppValue(this).equals(Prefs.NULL) ||
                Prefs.getAppValue(this).equals(Prefs.getNEWAppValueLive(this))
                ))
        if (Prefs.getAppValue(this).equals(Prefs.NULL) ||
            Prefs.getAppValue(this).equals(Prefs.getNEWAppValueLive(this))
        ) {

            Log.d("tet55", "pass: ")
        } else {
            Log.d("tet554", "pass:checasdjjksandnjasnjndjasnn: "+(!Prefs.checkList(Prefs.getAppValue(this), Prefs.exceptions)))
            if (!Prefs.checkList(Prefs.getAppValue(this), Prefs.exceptions)) {
                try {
                    WorkManager.getInstance(this).cancelAllWorkByTag("MYTASK")
                }catch (e:java.lang.Exception) {
                    Log.d("work_123", "excedtion222:  "+e.message)
                }

            }else {
                Prefs.setNEWAppValue(this, Prefs.getAppValue(this))
                createBroascat()
            }

        }
    }

    private fun createBroascat() {
        WorkManager.getInstance(this).cancelAllWork()
        val br = BReciver()
        Log.d("result_1234", "detials:")
        var filter = IntentFilter()
        filter.addAction("android.intent.action.open_app_notifiacation")
        registerReceiver(br, filter)

        sendBroadcast(Intent(this, BReciver::class.java))
    }

    fun getForgroundPackage() {
        if (Build.VERSION.SDK_INT >= 22) {
            val mUsageStatsManager =
                this.applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()

            val stats = mUsageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                time - 1000 * 10,
                time
            )

            if (stats != null) {
                val mySortedMap: SortedMap<Long, UsageStats> = TreeMap()
                for (usageStats in stats) {
                    mySortedMap.put(usageStats.lastTimeUsed, usageStats)
                }

                if (!stats.isEmpty()) {
                    Prefs.setAppValue(this, mySortedMap.get(mySortedMap.lastKey())!!.packageName)
                }
            }
        }else {
            Toast.makeText(this, "Not appplicable currently", Toast.LENGTH_SHORT).show()
        }
    }
}

