package com.clarice.burrow.ui.view.music

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clarice.burrow.R
import com.clarice.burrow.ui.model.music.MusicCategory
import com.clarice.burrow.ui.model.music.MusicTrack
import com.clarice.burrow.ui.viewmodel.MusicViewModel

@Composable
fun MusicListView(
    viewModel: MusicViewModel
) {
    val tracks by viewModel.musicTracks.collectAsState()
    val selectedTrack by viewModel.selectedTrack.collectAsState()

    val beforeSleepTracks = tracks.filter { it.category == MusicCategory.BEFORE_SLEEP }
    val duringSleepTracks = tracks.filter { it.category == MusicCategory.DURING_SLEEP }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1B4B))
    ) {
        // Background image (optional - uses same pattern as other screens)
        Image(
            painter = painterResource(id = R.drawable.trackerbg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E1B4B).copy(alpha = 0.7f),
                            Color(0xFF1E1B4B).copy(alpha = 0.9f)
                        )
                    )
                )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            contentPadding = PaddingValues(bottom = if (selectedTrack != null) 320.dp else 100.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(30.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Music",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "~ relax your mind with soothing sounds ~",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Before Sleep Section
            item {
                SectionHeader(
                    title = "Before Sleep",
                    icon = Icons.Default.Nightlight,
                    accentColor = Color(0xFF6B5FC7)
                )
            }

            items(beforeSleepTracks) { track ->
                MusicTrackCard(
                    track = track,
                    accentColor = Color(0xFF6B5FC7)
                ) {
                    viewModel.selectTrack(track)
                }
            }

            // During Sleep Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "During Sleep",
                    icon = Icons.Default.WbSunny,
                    accentColor = Color(0xFF9BB2FF)
                )
            }

            items(duringSleepTracks) { track ->
                MusicTrackCard(
                    track = track,
                    accentColor = Color(0xFF9BB2FF)
                ) {
                    viewModel.selectTrack(track)
                }
            }
        }

        // Player Overlay
        if (selectedTrack != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                shadowElevation = 16.dp,
                tonalElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White
            ) {
                MusicPlayerView(
                    track = selectedTrack!!,
                    viewModel = viewModel,
                    onClose = { viewModel.clearSelectedTrack() }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(accentColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Composable
fun MusicTrackCard(
    track: MusicTrack,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4856A4).copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = track.duration,
                    fontSize = 13.sp,
                    color = Color(0xFFDFE6FF)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accentColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
