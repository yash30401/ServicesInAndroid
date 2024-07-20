package com.yash.servicesinandroid

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TimerScreen(context: Context,viewModel: TimerViewModel) {
    val timeLeft by viewModel.timeLeft.collectAsState()
    val extraTime by viewModel.extraTime.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val minutes = (timeLeft / 1000) / 60
        val seconds = (timeLeft / 1000) % 60
        Text(
            text = String.format("%02d:%02d", minutes, seconds),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        if (extraTime > 0) {
            val extraMinutes = (extraTime / 60)
            val extraSeconds = (extraTime % 60)
            Text(
                text = String.format("%02d:%02d", extraMinutes, extraSeconds),
                modifier = Modifier.padding(top = 20.dp)
            )
        }

        Button(onClick = {
            TimerService.startService(context.applicationContext, 1 * 60 * 1000)
        }) {
            Text(text = "Start Timer")
        }
        Button(onClick = {
            TimerService.stopService(context.applicationContext)
        }) {
            Text(text = "Stop Timer")
        }
    }
}
