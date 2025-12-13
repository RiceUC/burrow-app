package com.febby.musics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.MaterialTheme
import com.febby.musics.navigation.AppNavigation
import com.febby.musics.viewmodel.MusicViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val vm: MusicViewModel = viewModel()
                AppNavigation(
                    navController = navController,
                    musicViewModel = vm
                )
            }
        }
    }
}