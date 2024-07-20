package com.yash.servicesinandroid

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.lifecycle.lifecycleScope
import com.yash.servicesinandroid.ui.theme.ServicesInAndroidTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private var timeLeft: Long by mutableStateOf(1500000L) // Default to 25:00
    private lateinit var dataStoreManager: DataStoreManager
    private var extraTime: Int by mutableStateOf(0)
    private val timerViewModel: TimerViewModel by viewModels()

    private val timerUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            timeLeft = intent?.getLongExtra("TIME_LEFT", 1500000L) ?: 1500000L
            extraTime = intent?.getIntExtra("EXTRA_TIME", 0) ?: 0
            lifecycleScope.launch {
                dataStoreManager.saveTimeLeft(timeLeft)
                dataStoreManager.saveExtraTime(extraTime)
                timerViewModel.updateTimeLeft(timeLeft)
                timerViewModel.updateExtraTime(extraTime)
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataStoreManager = DataStoreManager(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

        registerReceiver(
            timerUpdateReceiver, IntentFilter("TIMER_UPDATE"),
            RECEIVER_EXPORTED
        )

        lifecycleScope.launch {
            dataStoreManager.timeLeftFlow.collect { savedTimeLeft ->
                timeLeft = savedTimeLeft
            }

        }
        lifecycleScope.launch {
            dataStoreManager.extraTime.collect { getExtraTime ->
                extraTime = getExtraTime
            }
        }

        setContent {
            ServicesInAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {

                    TimerScreen(this@MainActivity,viewModel = timerViewModel)
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(timerUpdateReceiver)
    }
}
