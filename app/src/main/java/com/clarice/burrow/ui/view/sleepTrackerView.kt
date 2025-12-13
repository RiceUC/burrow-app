package com.clarice.burrow.ui.view

import android.app.TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clarice.burrow.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun SleepTrackerView(
    currentRoute: String = "sleep_tracker",
    onNavigate: (String) -> Unit = {},
    onStatisticsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var startTime by remember { mutableStateOf(LocalTime.of(22, 0)) }
    var endTime by remember { mutableStateOf<LocalTime?>(null) }
    var reminderTime by remember { mutableStateOf(LocalTime.of(21, 30)) }
    var isReminderEnabled by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background image
            Image(
                painter = painterResource(id = R.drawable.trackerbg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay for better visibility
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
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Moon icon
                            Text(
                                text = "🌙",
                                fontSize = 28.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )

                            Column {
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
                            }
                        }
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

                Spacer(modifier = Modifier.height(24.dp))

                // Circular Clock with Bunny
                Box(
                    modifier = Modifier
                        .size(280.dp),
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
                        // Inner circle
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E1B4B)),
                            contentAlignment = Alignment.Center
                        ) {
                            // Clock numbers and bunny
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // 12 AM
                                Text(
                                    "12 AM",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    modifier = Modifier.align(Alignment.TopCenter)
                                )
                                // 6 PM
                                Text(
                                    "6 PM",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    modifier = Modifier.align(Alignment.CenterStart)
                                )
                                // 6 AM
                                Text(
                                    "6 AM",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    modifier = Modifier.align(Alignment.CenterEnd)
                                )
                                // 12 PM
                                Text(
                                    "12 PM",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    modifier = Modifier.align(Alignment.BottomCenter)
                                )

                                // Bunny in center
                                Image(
                                    painter = painterResource(id = R.drawable.bunnysleep),
                                    contentDescription = "Sleeping Bunny",
                                    modifier = Modifier
                                        .size(140.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Sleep Time Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Start Sleep Card
                    SleepTimeCard(
                        modifier = Modifier.weight(1f),
                        icon = "🛏️",
                        label = "Start Sleep",
                        time = startTime.format(timeFormatter),
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    startTime = LocalTime.of(hour, minute)
                                },
                                startTime.hour,
                                startTime.minute,
                                false
                            ).show()
                        }
                    )

                    // End Sleep Card
                    SleepTimeCard(
                        modifier = Modifier.weight(1f),
                        icon = "🌙",
                        label = "End Sleep",
                        time = endTime?.format(timeFormatter) ?: "--:-- AM",
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    endTime = LocalTime.of(hour, minute)
                                },
                                6,
                                0,
                                false
                            ).show()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Reminder Time Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
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
                                        reminderTime = LocalTime.of(hour, minute)
                                    },
                                    reminderTime.hour,
                                    reminderTime.minute,
                                    false
                                ).show()
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Checkbox
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isReminderEnabled) Color(0xFF6B5FC7)
                                        else Color.LightGray
                                    )
                                    .clickable {
                                        isReminderEnabled = !isReminderEnabled
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isReminderEnabled) {
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
                            text = reminderTime.format(timeFormatter),
                            fontSize = 15.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Start Button
                Button(
                    onClick = { /* Handle start sleep */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF9BB2FF),
                                        Color(0xFFB8C5FF)
                                    )
                                ),
                                shape = RoundedCornerShape(28.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Start",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SleepTimeCard(
    icon: String,
    label: String,
    time: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F3FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp
                )
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SleepTrackerPreview() {
    SleepTrackerView()
}