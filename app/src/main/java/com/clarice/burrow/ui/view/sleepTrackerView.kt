package com.clarice.burrow.ui.view

import android.app.TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.clarice.burrow.R
import com.clarice.burrow.ui.model.sleep.SleepSession
import com.clarice.burrow.ui.viewmodel.SleepTrackerViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SleepTrackerView(
    currentRoute: String = "sleep_tracker",
    onNavigate: (String) -> Unit = {},
    onStatisticsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel = remember { SleepTrackerViewModel(context) }
    val sleepState = viewModel.sleepState

    var showSleepQualityDialog by remember { mutableStateOf(false) }
    var showSessionsDialog by remember { mutableStateOf(false) }

    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    // Debug: Log state changes
    LaunchedEffect(sleepState.currentSession) {
        android.util.Log.d("SleepTracker", "Current session changed: ${sleepState.currentSession}")
        android.util.Log.d("SleepTracker", "Start time display: ${viewModel.getStartTimeDisplay()}")
        android.util.Log.d("SleepTracker", "End time display: ${viewModel.getEndTimeDisplay()}")
        android.util.Log.d("SleepTracker", "Button text: ${viewModel.getButtonText()}")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
            // Background image
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
                                Color(0xFF1E1B4B).copy(alpha = 0.6f),
                                Color(0xFF1E1B4B).copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // View Sessions Button
                    Button(
                        onClick = { showSessionsDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B7FDB)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(
                            text = "History",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Statistics Button
                    Button(
                        onClick = onStatisticsClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6B5FC7)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(
                            text = "Statistics",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = ">", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                Text(
                    text = "Sleep Tracker",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "~ a safe place to relax after a long day ~",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Circular Clock with Bunny
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer circle with gradient border
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF6B5FC7),
                                        Color(0xFF9BB2FF)
                                    )
                                )
                            )
                            .padding(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2D2665))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E1B4B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    "12 AM",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    modifier = Modifier.align(Alignment.TopCenter)
                                )
                                Text(
                                    "6 PM",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    modifier = Modifier.align(Alignment.CenterStart)
                                )
                                Text(
                                    "6 AM",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    modifier = Modifier.align(Alignment.CenterEnd)
                                )
                                Text(
                                    "12 PM",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    modifier = Modifier.align(Alignment.BottomCenter)
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.bunnysleep),
                                    contentDescription = "Sleeping Bunny",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sleep Time Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SleepTimeCard(
                        modifier = Modifier.weight(1f),
                        icon = "🛏️",
                        label = "Start Sleep",
                        time = viewModel.getStartTimeDisplay()
                    )

                    SleepTimeCard(
                        modifier = Modifier.weight(1f),
                        icon = "🌙",
                        label = "End Sleep",
                        time = viewModel.getEndTimeDisplay()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reminder Time Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        viewModel.setReminderTime(
                                            java.time.LocalTime.of(hour, minute)
                                        )
                                    },
                                    sleepState.reminderTime.hour,
                                    sleepState.reminderTime.minute,
                                    false
                                ).show()
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (sleepState.isReminderEnabled) Color(0xFF6B5FC7)
                                        else Color.LightGray
                                    )
                                    .clickable {
                                        viewModel.setReminderEnabled(!sleepState.isReminderEnabled)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (sleepState.isReminderEnabled) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Enabled",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Reminder time",
                                fontSize = 15.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Text(
                            text = sleepState.reminderTime.format(timeFormatter),
                            fontSize = 15.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Button (Start / End / Reset)
                Button(
                    onClick = {
                        val buttonText = viewModel.getButtonText()
                        android.util.Log.d("SleepTracker", "Button clicked: $buttonText")

                        when (buttonText) {
                            "Start" -> {
                                android.util.Log.d("SleepTracker", "Calling startSleepSession")
                                viewModel.startSleepSession {
                                    android.util.Log.d("SleepTracker", "Start success callback")
                                }
                            }
                            "End" -> {
                                android.util.Log.d("SleepTracker", "Showing quality dialog")
                                showSleepQualityDialog = true
                            }
                            "Reset" -> {
                                android.util.Log.d("SleepTracker", "Calling resetSession")
                                viewModel.resetSession()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    enabled = !sleepState.isStarting && !sleepState.isEnding
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = when (viewModel.getButtonText()) {
                                        "End" -> listOf(Color(0xFFFF6B6B), Color(0xFFFF8E8E))
                                        "Reset" -> listOf(Color(0xFFFFA500), Color(0xFFFFB84D))
                                        else -> listOf(Color(0xFF9BB2FF), Color(0xFFB8C5FF))
                                    }
                                ),
                                shape = RoundedCornerShape(28.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (sleepState.isStarting || sleepState.isEnding) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = viewModel.getButtonText(),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Error Message
                sleepState.error?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFCDD2)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = error,
                            color = Color(0xFFB71C1C),
                            modifier = Modifier.padding(12.dp),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

    // Sleep Quality Dialog
    if (showSleepQualityDialog) {
        SleepQualityDialog(
            onDismiss = { showSleepQualityDialog = false },
            onConfirm = { quality ->
                showSleepQualityDialog = false
                viewModel.endSleepSession(sleepQuality = quality)
            }
        )
    }

    // Sleep Sessions History Dialog
    if (showSessionsDialog) {
        SleepSessionsDialog(
            sessions = sleepState.sessions,
            onDismiss = { showSessionsDialog = false },
            onDelete = { sessionId ->
                viewModel.deleteSleepSession(sessionId) {}
            }
        )
    }
}

@Composable
private fun SleepTimeCard(
    icon: String,
    label: String,
    time: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F3FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                fontSize = 13.sp,
                color = Color.Black.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = time,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun SleepQualityDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var quality by remember { mutableStateOf(3) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "How was your sleep?",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Rate from 1 (poor) to 5 (excellent)",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Quality Emoji Display
                Text(
                    text = getQualityEmoji(quality),
                    fontSize = 64.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = getQualityText(quality),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = getQualityColor(quality)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Quality Slider
                Slider(
                    value = quality.toFloat(),
                    onValueChange = { quality = it.toInt() },
                    valueRange = 1f..5f,
                    steps = 3,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF6B5FC7),
                        activeTrackColor = Color(0xFF6B5FC7),
                        inactiveTrackColor = Color(0xFFE0E0E0)
                    )
                )

                // Quality Labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Poor", fontSize = 12.sp, color = Color.Gray)
                    Text("Excellent", fontSize = 12.sp, color = Color.Gray)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(quality) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B5FC7)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Submit", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun SleepSessionsDialog(
    sessions: List<SleepSession>,
    onDismiss: () -> Unit,
    onDelete: (Int) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Sleep History",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1B4B)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (sessions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "😴",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No sleep sessions yet",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(sessions) { session ->
                            SleepSessionItem(
                                session = session,
                                onDelete = { onDelete(session.sessionId) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6B5FC7)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun SleepSessionItem(
    session: SleepSession,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatDate(session.startTime),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1B4B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${formatTime(session.startTime)} → ${formatTime(session.endTime ?: session.startTime)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Duration: ${formatDuration(session.durationMinutes)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    session.sleepQuality?.let { quality ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = getQualityEmoji(quality),
                            fontSize = 16.sp
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFFF6B6B)
                )
            }
        }
    }
}

// Helper Functions
private fun formatTime(isoDateTime: String): String {
    return try {
        val dateTime = LocalDateTime.parse(isoDateTime, DateTimeFormatter.ISO_DATE_TIME)
        dateTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
    } catch (e: Exception) {
        "--:--"
    }
}

private fun formatDate(isoDateTime: String): String {
    return try {
        val dateTime = LocalDateTime.parse(isoDateTime, DateTimeFormatter.ISO_DATE_TIME)
        dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    } catch (e: Exception) {
        "Unknown"
    }
}

private fun formatDuration(minutes: Int?): String {
    if (minutes == null || minutes == 0) return "0h 0m"
    val hours = minutes / 60
    val mins = minutes % 60
    return "${hours}h ${mins}m"
}

private fun getQualityEmoji(quality: Int): String {
    return when (quality) {
        5 -> "😴"
        4 -> "😊"
        3 -> "😐"
        2 -> "😕"
        1 -> "😫"
        else -> "❓"
    }
}

private fun getQualityText(quality: Int): String {
    return when (quality) {
        5 -> "Excellent"
        4 -> "Good"
        3 -> "Average"
        2 -> "Poor"
        1 -> "Very Poor"
        else -> "Unknown"
    }
}

private fun getQualityColor(quality: Int): Color {
    return when (quality) {
        5 -> Color(0xFF4CAF50)
        4 -> Color(0xFF8BC34A)
        3 -> Color(0xFFFFC107)
        2 -> Color(0xFFFF9800)
        1 -> Color(0xFFF44336)
        else -> Color.Gray
    }
}