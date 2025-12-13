package com.febby.musics.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.febby.musics.ui.view.MusicListView
import com.febby.musics.ui.view.MusicPlayerView
import com.febby.musics.viewmodel.MusicViewModel

object Routes {
    const val LIST = "list"
    const val PLAYER = "player"
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    musicViewModel: MusicViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LIST,
        modifier = modifier
    ) {

        composable(Routes.LIST) {
            MusicListView(
                navController = navController,
                vm = musicViewModel
            )
        }

        composable(Routes.PLAYER) {
            MusicPlayerView(
                navController = navController,
                vm = musicViewModel
            )
        }
    }
}