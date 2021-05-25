package com.example.scanner

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData


class TimerService : Service() {
    private val CHANNEL_ID = "500001"
    private var timerData : String? = null
    private var startTime : Long? = 0L
    private val mHandler: Handler = Handler()
    val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
    var notificationManager : NotificationManager? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        createNotificationChannel()
        startTime = intent?.getLongExtra("START_TIME", 0)
        calculateTime(startTime)
        val notification =
            notificationBuilder
                .setContentTitle("SCANNER")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
        startForeground(1, notification)
        return START_REDELIVER_INTENT
    }

    private fun calculateTime(startTime: Long?) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandler.postDelayed(mUpdateTimeTask, 0);
    }

    private val mUpdateTimeTask: Runnable = object : Runnable {
        override fun run() {
            val millis: Long = System.currentTimeMillis() - (startTime ?: 0L)
            var seconds = (millis / 1000).toInt()
            val minutes = seconds / 60
            seconds %= 60
            if (seconds < 10) {
                timerData = "$minutes:0$seconds"
            } else {
                timerData = "$minutes:$seconds"
            }
            notificationBuilder.setContentTitle("Library Session is in Progress -> $timerData")
            notificationManager?.notify(1, notificationBuilder.build())
            mHandler.postDelayed(this,  1000)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacks(mUpdateTimeTask);
    }
}