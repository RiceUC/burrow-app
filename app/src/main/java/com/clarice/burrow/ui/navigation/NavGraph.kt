package com.clarice.burrow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.clarice.burrow.ui.screens.SignUpView
import com.clarice.burrow.ui.screens.WelcomeView
import com.clarice.burrow.ui.view.SleepTrackerView
import com.clarice.burrow.ui.view.StatisticsView
import com.clarice.burrow.ui.viewmodel.AuthViewModel
import SignInView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val context = LocalContext.current

    // Determine start destination based on login status
    val startDestination = if (isLoggedIn) "sleep_tracker" else "welcome"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ==================== AUTH SCREENS ====================

        // Welcome Screen
        composable("welcome") {
            WelcomeView(
                onGetStarted = {
                    navController.navigate("sign_in") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        // Sign In Screen
        composable("sign_in") {
            SignInView(
                onBack = {
                    navController.navigateUp()
                },
                onLogin = { email, password ->
                    authViewModel.updateLoginUsername(email)
                    authViewModel.updateLoginPassword(password)
                    authViewModel.login {
                        // Navigate to main app on success
                        navController.navigate("sleep_tracker") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                },
                onSignUp = {
                    navController.navigate("sign_up")
                }
            )
        }

        // Sign Up Screen
        composable("sign_up") {
            SignUpView(
                onBack = {
                    navController.navigateUp()
                },
                onSignIn = {
                    navController.navigate("sign_in") {
                        popUpTo("sign_up") { inclusive = true }
                    }
                },
                onSignUp = { name, username, dob, password, gender ->
                    authViewModel.updateRegisterName(name)
                    authViewModel.updateRegisterUsername(username)
                    authViewModel.updateRegisterPassword(password)
                    authViewModel.updateRegisterBirthdate(dob)
                    authViewModel.updateRegisterGender(gender)

                    authViewModel.register {
                        // Navigate to sign in after successful registration
                        navController.navigate("sign_in") {
                            popUpTo("sign_up") { inclusive = true }
                        }
                    }
                }
            )
        }

        // ==================== MAIN APP SCREENS ====================

        // Sleep Tracker Screen (Main Screen)
        composable("sleep_tracker") {
            SleepTrackerView(
                currentRoute = "sleep_tracker",
                onNavigate = { route ->
                    navController.navigate(route) {
                        // Avoid multiple copies of same destination
                        popUpTo("sleep_tracker") {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onStatisticsClick = {
                    navController.navigate("statistics")
                }
            )
        }

        // Statistics Screen
        composable("statistics") {
            StatisticsView(
                currentRoute = "statistics",
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo("sleep_tracker") {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBack = {
                    navController.navigateUp()
                }
            )
        }

        // Journal List Screen
        composable("journal_list") {
            // TODO: Create JournalListView
            PlaceholderScreen(
                title = "Journal",
                currentRoute = "journal_list",
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo("sleep_tracker") {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // Journal Entry Screen (with ID parameter)
        composable(
            route = "journal_entry/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val journalId = backStackEntry.arguments?.getInt("id") ?: 0
            // TODO: Create JournalEntryView
            PlaceholderScreen(
                title = "Journal Entry #$journalId",
                currentRoute = "journal_entry",
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        // Music List Screen
        composable("music_list") {
            // TODO: Create MusicListView
            PlaceholderScreen(
                title = "Music",
                currentRoute = "music_list",
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo("sleep_tracker") {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // Music Player Screen
        composable("music_player") {
            // TODO: Create MusicPlayerView
            PlaceholderScreen(
                title = "Music Player",
                currentRoute = "music_player",
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        // Edit Profile Screen
        composable("edit_profile") {
            // TODO: Create EditProfileView
            PlaceholderScreen(
                title = "Profile",
                currentRoute = "edit_profile",
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo("sleep_tracker") {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onLogout = {
                    authViewModel.logout {
                        navController.navigate("welcome") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}

// Temporary placeholder screen for unimplemented views
@Composable
private fun PlaceholderScreen(
    title: String,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: (() -> Unit)? = null
) {
    androidx.compose.material3.Scaffold(
        bottomBar = {
            com.clarice.burrow.ui.view.BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.material3.Text(
                    text = "$title Screen",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
                )
                androidx.compose.material3.Text(
                    text = "Coming Soon...",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                )

                onLogout?.let {
                    androidx.compose.material3.Button(
                        onClick = it
                    ) {
                        androidx.compose.material3.Text("Logout")
                    }
                }
            }
        }
    }
}