package com.clarice.burrow.ui.route

sealed class Screen(val route: String) {
    // Auth screens
    object Welcome : Screen("welcome")
    object SignIn : Screen("sign_in")
    object SignUp : Screen("sign_up")

    // Main screens
    object SleepTracker : Screen("sleep_tracker")
    object Statistics : Screen("statistics")
    object JournalList : Screen("journal_list")
    object JournalEntry : Screen("journal_entry/{id}") {
        fun createRoute(id: Int) = "journal_entry/$id"
    }
    object MusicList : Screen("music_list")
    object MusicPlayer : Screen("music_player/{musicId}") {
        fun createRoute(musicId: Int) = "music_player/$musicId"
    }
    object EditProfile : Screen("edit_profile")
}

// Bottom navigation items
val bottomNavItems = listOf(
    Screen.SleepTracker,
    Screen.Statistics,
    Screen.JournalList,
    Screen.MusicList
)