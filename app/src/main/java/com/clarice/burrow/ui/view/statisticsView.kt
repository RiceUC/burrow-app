package com.clarice.burrow.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.clarice.burrow.R
import com.clarice.burrow.ui.viewmodel.StatisticsViewModel
import com.clarice.burrow.ui.viewmodel.StatisticsPeriod
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun StatisticsView(
    currentRoute: String = "statistics",
    onNavigate: (String) -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel = remember { StatisticsViewModel(context) }
    val statsState = viewModel.statisticsState

    var selectedPeriod by remember { mutableStateOf(StatisticsPeriod.WEEK) }

    // Update period when changed
    LaunchedEffect(selectedPeriod) {
        viewModel.updatePeriod(selectedPeriod)
    }

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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with back button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🌙",
                        fontSize = 28.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Text(
                        text = "Statistics",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = "See how you've been sleeping ^-^",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Period Selector
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF2D2665)
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(
                            StatisticsPeriod.WEEK to "Week",
                            StatisticsPeriod.MONTH to "Month",
                            StatisticsPeriod.YEAR to "Year"
                        ).forEach { (period, label) ->
                            Button(
                                onClick = { selectedPeriod = period },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedPeriod == period)
                                        Color(0xFF6B5FC7)
                                    else Color.Transparent,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = if (selectedPeriod == period) 4.dp else 0.dp
                                )
                            ) {
                                Text(
                                    text = label,
                                    fontWeight = if (selectedPeriod == period)
                                        FontWeight.Bold
                                    else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date Range
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = viewModel.getDateRangeString(),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stats Cards
                if (statsState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else if (statsState.statistics != null) {
                    val stats = statsState.statistics

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Average Card
                        StatsCard(
                            modifier = Modifier.weight(1f),
                            label = "Average",
                            value = formatDurationToHours(stats.average_duration),
                            icon = "📊"
                        )

                        // Total Sessions Card
                        StatsCard(
                            modifier = Modifier.weight(1f),
                            label = "Sessions",
                            value = stats.total_sessions.toString(),
                            icon = "🌙"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Best Quality Card
                        StatsCard(
                            modifier = Modifier.weight(1f),
                            label = "Best Quality",
                            value = getQualityEmoji(stats.best_sleep_quality),
                            icon = "⭐",
                            showDot = true
                        )

                        // Average Quality Card
                        StatsCard(
                            modifier = Modifier.weight(1f),
                            label = "Avg Quality",
                            value = getQualityEmoji(stats.average_quality),
                            icon = "📈"
                        )
                    }
                } else {
                    // No data
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2D2665)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "😴",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No sleep data yet",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Chart Container
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2D2665)
                    )
                ) {
                    if (statsState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    } else if (statsState.recentSessions.isNotEmpty()) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Sleep Duration",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Y-axis labels and bars
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            ) {
                                // Y-axis
                                Column(
                                    modifier = Modifier
                                        .width(35.dp)
                                        .fillMaxHeight(),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text("10h", color = Color(0xFF8B7FDB), fontSize = 12.sp)
                                    Text("8h", color = Color(0xFF8B7FDB), fontSize = 12.sp)
                                    Text("6h", color = Color(0xFF8B7FDB), fontSize = 12.sp)
                                    Text("4h", color = Color(0xFF8B7FDB), fontSize = 12.sp)
                                    Text("0h", color = Color(0xFF8B7FDB), fontSize = 12.sp)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Bar Chart
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    // Get last 12 sessions (or fewer)
                                    val displaySessions = statsState.recentSessions.take(12)

                                    displaySessions.forEach { session ->
                                        val duration = session.durationMinutes ?: 0
                                        // Normalize to 0-1 range (10 hours = 600 minutes max)
                                        val normalizedHeight = (duration / 600f).coerceIn(0f, 1f)

                                        Box(
                                            modifier = Modifier
                                                .width(16.dp)
                                                .fillMaxHeight(if (normalizedHeight > 0) normalizedHeight else 0.05f)
                                                .clip(
                                                    RoundedCornerShape(
                                                        topStart = 4.dp,
                                                        topEnd = 4.dp
                                                    )
                                                )
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(
                                                            Color(0xFFC4B5FD),
                                                            Color(0xFF8B5CF6)
                                                        )
                                                    )
                                                )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // X-axis info
                            Text(
                                text = "Last ${statsState.recentSessions.take(12).size} sessions",
                                color = Color(0xFF8B7FDB),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 47.dp)
                            )
                        }
                    } else {
                        // No sessions to chart
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "📊",
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No data to display",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // Error Message
                statsState.error?.let { error ->
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
    }
}

@Composable
private fun StatsCard(
    label: String,
    value: String,
    icon: String? = null,
    showDot: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF2D2665)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (showDot) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF9BB2FF))
                    )
                }

                icon?.let {
                    Text(
                        text = it,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Text(
                    text = label,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Helper Functions
private fun formatDurationToHours(minutes: Int?): String {
    if (minutes == null || minutes == 0) return "0h"
    val hours = minutes / 60.0
    return if (hours >= 1) {
        String.format("%.1fh", hours)
    } else {
        "${minutes}m"
    }
}

private fun getQualityEmoji(quality: Int?): String {
    return when (quality) {
        5 -> "😴"
        4 -> "😊"
        3 -> "😐"
        2 -> "😕"
        1 -> "😫"
        0 -> "—"
        else -> "—"
    }
}