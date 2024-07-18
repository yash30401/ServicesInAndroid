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
    private var timeLeft: Long = 0

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> {
                timeLeft = intent.getLongExtra("TIME_LEFT", 1500000L)
                startTimer(timeLeft)
            }
            Actions.FINISH.toString() -> stopSelf()
        }
        return START_STICKY
    }

    @SuppressLint("ForegroundServiceType")
    private fun startTimer(timeLeft: Long) {
        val notification = NotificationCompat.Builder(this, "101")
            .setContentTitle("Timer")
            .setContentText("Time remaining: 25:00")
            .setSmallIcon(R.drawable.baseline_timer_24)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        startForeground(1, notification)

        countDownTimer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                this@TimerService.timeLeft = millisUntilFinished

                val updatedNotification = NotificationCompat.Builder(this@TimerService, "101")
                    .setContentTitle("Timer")
                    .setContentText("Time remaining: ${formatTime(millisUntilFinished)}")
                    .setSmallIcon(R.drawable.baseline_timer_24)
                    .setSilent(true)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()

                startForeground(1, updatedNotification)

                val broadcastIntent = Intent("TIMER_UPDATE")
                broadcastIntent.putExtra("TIME_LEFT", millisUntilFinished)
                sendBroadcast(broadcastIntent)
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
        getSharedPreferences("TIMER_PREFS", Context.MODE_PRIVATE)
            .edit()
            .putLong("TIME_LEFT", timeLeft)
            .apply()
        super.onDestroy()
    }

    companion object {
        fun startService(context: Context, timeLeft: Long) {
            val startIntent = Intent(context, TimerService::class.java).apply {
                action = Actions.START.toString()
                putExtra("TIME_LEFT", timeLeft)
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
