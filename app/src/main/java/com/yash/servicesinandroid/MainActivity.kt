package com.yash.servicesinandroid

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.yash.servicesinandroid.ui.theme.ServicesInAndroidTheme

class MainActivity : ComponentActivity() {

    private var timeLeft: Long by mutableStateOf(1500000L) // Default to 25:00
    private val timerUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            timeLeft = intent?.getLongExtra("TIME_LEFT", 1500000L) ?: 1500000L
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

        registerReceiver(timerUpdateReceiver, IntentFilter("TIMER_UPDATE"),
            RECEIVER_EXPORTED)

        val sharedPreferences = getSharedPreferences("TIMER_PREFS", Context.MODE_PRIVATE)
        timeLeft = sharedPreferences.getLong("TIME_LEFT", 1500000L)

        setContent {
            ServicesInAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    var timeLeftState by remember { mutableStateOf(timeLeft) }

                    LaunchedEffect(Unit) {
                        val savedTimeLeft = sharedPreferences.getLong("TIME_LEFT", 1500000L)
                        timeLeftState = savedTimeLeft
                    }

                    LaunchedEffect(timeLeft) {
                        timeLeftState = timeLeft
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val minutes = (timeLeftState / 1000) / 60
                        val seconds = (timeLeftState / 1000) % 60
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            modifier = Modifier.padding(bottom = 20.dp)
                        )

                        Button(onClick = {
                            TimerService.startService(applicationContext, timeLeftState)
                        }) {
                            Text(text = "Start Timer")
                        }
                        Button(onClick = {
                            TimerService.stopService(applicationContext)
                        }) {
                            Text(text = "Stop Timer")
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(timerUpdateReceiver)
    }
}
