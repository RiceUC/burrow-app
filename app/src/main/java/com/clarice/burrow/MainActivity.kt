package com.clarice.burrow

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.clarice.burrow.ui.navigation.NavGraph
import com.clarice.burrow.ui.theme.BurrowTheme
import com.clarice.burrow.ui.viewmodel.AuthViewModel
import com.clarice.burrow.utils.NotificationPermissionHelper

class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request notification permission for Android 13+
        NotificationPermissionHelper.requestNotificationPermission(this)

        setContent {
            BurrowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel = AuthViewModel(applicationContext)

                    NavGraph(
                        navController = navController,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }

    /**
     * Handle permission request result
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            NotificationPermissionHelper.NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    // Permission granted - notifications will work
                    android.util.Log.d("MainActivity", "Notification permission granted")
                } else {
                    // Permission denied - notifications won't work
                    android.util.Log.w("MainActivity", "Notification permission denied")
                }
            }
        }
    }
}