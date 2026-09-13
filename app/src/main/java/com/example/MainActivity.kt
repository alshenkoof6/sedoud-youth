package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.data.repository.YouthMeetingRepository
import com.example.ui.screens.MainAppScaffold
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val repository = remember { YouthMeetingRepository() }
        MainAppScaffold(
          repository = repository,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}

