package com.clarice.burrow.ui.navigation

import com.clarice.burrow.ui.view.SignInView
import androidx.compose.runtime.Composable
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clarice.burrow.ui.viewmodel.JournalViewModel
import com.clarice.burrow.ui.view.JournalEntryScreen
import com.clarice.burrow.ui.view.JournalListScreen
import com.clarice.burrow.data.repository.JournalRepository
import com.clarice.burrow.ui.view.MusicListView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clarice.burrow.ui.view.MusicPlayerView
import com.clarice.burrow.ui.viewmodel.MusicViewModel
import com.clarice.burrow.data.remote.RetrofitClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button

// ==================== MAIN APP SCAFFOLD - NAVBAR ALWAYS VISIBLE ====================
@Composable
fun MainAppScaffold(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            com.clarice.burrow.ui.view.BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            content()
        }
    }
}

// Temporary placeholder screen for unimplemented views
@Composable
fun PlaceholderScreen(
    title: String,
    currentRoute: String,
    onLogout: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "$title Screen",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Coming Soon...",
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
            )

            onLogout?.let {
                Button(
                    onClick = it
                ) {
                    Text("Logout")
                }
            }
        }
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val journalRepository = JournalRepository(context)
    val journalViewModel: JournalViewModel = viewModel<JournalViewModel>(factory = JournalViewModel.Factory(journalRepository))
    val musicViewModel: MusicViewModel = viewModel<MusicViewModel>()
    val userIdValue by authViewModel.userId.collectAsState()

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
                onLoginSuccess = {
                    // Navigate to main app on success
                    navController.navigate("sleep_tracker") {
                        popUpTo("welcome") { inclusive = true }
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
                onSignUpSuccess = {
                    // Navigate to sign in after successful registration
                    navController.navigate("sign_in") {
                        popUpTo("sign_up") { inclusive = true }
                    }
                }
            )
        }

        // ==================== MAIN APP SCREENS (WITH PERSISTENT NAVBAR) ====================

        // Sleep Tracker Screen (Main Screen)
        composable("sleep_tracker") {
            MainAppScaffold(
                currentRoute = "sleep_tracker",
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo("sleep_tracker") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
                SleepTrackerView(
                    currentRoute = "sleep_tracker",
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("sleep_tracker") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onStatisticsClick = {
                        navController.navigate("statistics")
                    }
                )
            }
        }

        // Statistics Screen
        composable("statistics") {
            MainAppScaffold(
                currentRoute = "statistics",
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo("sleep_tracker") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
                StatisticsView(
                    currentRoute = "statistics",
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("sleep_tracker") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onBack = {
                        navController.navigateUp()
                    }
                )
            }
        }

        // Journal List Screen
        composable("journal_list") {
            MainAppScaffold(
                currentRoute = "journal_list",
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo("sleep_tracker") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
                android.util.Log.d("NavGraph", "Journal list composable - userIdValue: $userIdValue")
                if (userIdValue != null) {
                    android.util.Log.d("NavGraph", "Rendering JournalListScreen with userId: $userIdValue")
                    JournalListScreen(
                        viewModel = journalViewModel,
                        userId = userIdValue!!,
                        onAdd = {
                            navController.navigate("journal_entry")
                        },
                        onOpen = { id: Int ->
                            navController.navigate("journal_entry/$id")
                        }
                    )
                } else {
                    android.util.Log.e("NavGraph", "userIdValue is null - JournalListScreen not rendered!")
                }
            }
        }

        // Journal Entry Screen (Create new)
        composable("journal_entry") {
            if (userIdValue != null) {
                JournalEntryScreen(
                    viewModel = journalViewModel,
                    userId = userIdValue!!,
                    journalId = null,
                    onBack = { navController.navigateUp() },
                    onSaved = { navController.navigateUp() }
                )
            }
        }

        // Journal Entry Screen (Edit existing)
        composable(
            route = "journal_entry/{journalId}",
            arguments = listOf(
                navArgument("journalId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val journalId = backStackEntry.arguments?.getInt("journalId")
            if (userIdValue != null) {
                JournalEntryScreen(
                    viewModel = journalViewModel,
                    userId = userIdValue!!,
                    journalId = journalId,
                    onBack = { navController.navigateUp() },
                    onSaved = { navController.navigateUp() }
                )
            }
        }

        // Music List Screen
        composable("music_list") {
            MainAppScaffold(
                currentRoute = "music_list",
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo("sleep_tracker") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
                PlaceholderScreen(
                    title = "Music",
                    currentRoute = "music_list"
                )
            }
            MusicListView(
                navController = navController,
                vm = musicViewModel
            )
        }

        // Music Player Screen
        composable("music_player") {
            MusicPlayerView(
                navController = navController,
                vm = musicViewModel
            )
        }

        // Edit Profile Screen
        composable("edit_profile") {
            MainAppScaffold(
                currentRoute = "edit_profile",
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo("sleep_tracker") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
                PlaceholderScreen(
                    title = "Profile",
                    currentRoute = "edit_profile",
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
}