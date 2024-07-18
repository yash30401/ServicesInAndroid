package com.yash.servicesinandroid

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat

class TimerService : Service() {

    private var countDownTimer: CountDownTimer? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> {
                val timeLeft = intent.getIntExtra("TIME_LEFT",1500000)
                startTimer(timeLeft)
            }
            Actions.FINISH.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("ForegroundServiceType")
    private fun startTimer(timeLeft: Int) {
        val notification = NotificationCompat.Builder(this, "101")
            .setContentTitle("Timer")
            .setContentText("Time remaining: 25:00")
            .setSmallIcon(R.drawable.baseline_timer_24)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        startForeground(1, notification)

        countDownTimer = object : CountDownTimer(timeLeft.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val updatedNotification = NotificationCompat.Builder(this@TimerService, "101")
                    .setContentTitle("Timer")
                    .setContentText("Time remaining: ${formatTime(millisUntilFinished)}")
                    .setSmallIcon(R.drawable.baseline_timer_24)
                    .setSilent(true)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()

                startForeground(1, updatedNotification)
            }

            override fun onFinish() {
                stopSelf()
            }

        }.start()
    }

    private fun formatTime(millis: Long): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }

    companion object {
        fun startService(context: Context, timeLeft: Int) {
            val startIntent = Intent(context, TimerService::class.java).apply {
                action = Actions.START.toString()
                putExtra("time_left", timeLeft)
            }
            context.startService(startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, TimerService::class.java).apply {
                action = Actions.FINISH.toString()
            }
            context.startService(stopIntent)
        }
    }

    enum class Actions {
        START, FINISH
    }
}