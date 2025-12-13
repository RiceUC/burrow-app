package com.febby.musics.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.febby.musics.R
import com.febby.musics.viewmodel.MusicViewModel
import com.febby.musics.viewmodel.PlayerPhase

@Composable
fun MusicPlayerView(
    navController: NavController,
    vm: MusicViewModel
) {
    val music = vm.getCurrentMusic()
    val currentSec by vm.currentSec
    val totalSec by vm.totalSec
    val isPlaying by vm.isPlaying
    val phase by vm.phase

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.musicplayerview),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.close),
                    contentDescription = "close",
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = music?.title ?: "-",
                    color = Color.White,
                    fontSize = 28.sp
                )

                Text(
                    text = when (phase) {
                        PlayerPhase.ABOUT_SLEEP -> "About to sleep"
                        PlayerPhase.WHILE_SLEEP -> "While sleeping"
                        PlayerPhase.FINISHED -> "Finished"
                    },
                    color = Color(0xFFAFB8D0),
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(36.dp))

                PlayerControls(
                    isPlaying = isPlaying,
                    onPlayPause = { vm.togglePlay() },
                    onRewind = { vm.seekTo(currentSec - 15) },
                    onForward = { vm.seekTo(currentSec + 15) }
                )

                Spacer(modifier = Modifier.height(36.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Slider(
                        value = currentSec.toFloat(),
                        onValueChange = { vm.seekTo(it.toInt()) },
                        valueRange = 0f..totalSec.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFA8B3FF),
                            activeTrackColor = Color(0xFFA8B3FF),
                            inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(formatTime(currentSec), color = Color.White)
                        Text(formatTime(totalSec), color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onRewind: () -> Unit,
    onForward: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(R.drawable.rewind_15),
            contentDescription = "rewind",
            tint = Color.White,
            modifier = Modifier
                .size(42.dp)
                .clickable { onRewind() }
        )

        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFF3E4A82))
                .clickable { onPlayPause() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(
                    if (isPlaying) R.drawable.pause else R.drawable.play
                ),
                contentDescription = "play_pause",
                modifier = Modifier.size(46.dp)
            )
        }

        Icon(
            painter = painterResource(R.drawable.forward_15),
            contentDescription = "forward",
            tint = Color.White,
            modifier = Modifier
                .size(42.dp)
                .clickable { onForward() }
        )
    }
}

@Composable
fun PlayerSection(
    isPlaying: Boolean,
    onRewind: () -> Unit,
    onForward: () -> Unit,
    onTogglePlay: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.rewind_15),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onRewind() }
            )
            Text("15", color = Color.White, fontSize = 14.sp)
        }

        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFF3E4A82)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = if (isPlaying) R.drawable.pause else R.drawable.play),
                contentDescription = "playpause",
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onTogglePlay() }
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.forward_15),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onForward() }
            )
            Text("15", color = Color.White, fontSize = 14.sp)
        }
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}