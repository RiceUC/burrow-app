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

    // Journal Entry (Create new)
    object JournalEntryCreate : Screen("journal_entry")

    // Journal Entry (Edit existing)
    object JournalEntry : Screen("journal_entry/{journalId}") {
        fun createRoute(journalId: Int) = "journal_entry/$journalId"
    }

    object MusicList : Screen("music_list")
    object MusicPlayer : Screen("music_player")
    object EditProfile : Screen("edit_profile")
}

// Bottom navigation items
val bottomNavItems = listOf(
    Screen.SleepTracker,
    Screen.Statistics,
    Screen.JournalList,
    Screen.MusicList
)