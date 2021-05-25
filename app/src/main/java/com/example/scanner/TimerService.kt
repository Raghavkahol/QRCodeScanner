package com.example.scanner

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.provider.Settings.Global.getString
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData


class TimerService : Service() {
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
        startTime = intent?.getLongExtra(START_TIME, 0)
        calculateTime()
        val notification =
            notificationBuilder
                .setContentTitle(getString(R.string.scanner))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
        startForeground(NOTIFICATION_ID, notification)
        return START_REDELIVER_INTENT
    }

    private fun calculateTime() {
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
            notificationBuilder.setContentTitle(String.format(getString(R.string.session_timer), timerData))
            notificationManager?.notify(NOTIFICATION_ID, notificationBuilder.build())
            mHandler.postDelayed(this,  1000)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                TIMER_CHANNEL,
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