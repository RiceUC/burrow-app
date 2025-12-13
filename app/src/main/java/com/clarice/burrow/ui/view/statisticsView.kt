package com.clarice.burrow.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clarice.burrow.R

@Composable
fun StatisticsView(
    currentRoute: String = "statistics",
    onNavigate: (String) -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf("Week") }
    var currentDateRange by remember { mutableStateOf("1 Jan - 7 Jan 2026") }

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
                // Header with back button (aligned to right like SleepTrackerView)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
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

                // Title section (centered like SleepTrackerView)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                // Period Selector (Week/Month/Year)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF2D2665)
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Week", "Month", "Year").forEach { period ->
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
                                    text = period,
                                    fontWeight = if (selectedPeriod == period)
                                        FontWeight.Bold
                                    else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date Range with navigation arrows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            // Handle previous date range
                            // TODO: Implement date navigation logic
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text(
                            text = "<",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = currentDateRange,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )

                    IconButton(
                        onClick = {
                            // Handle next date range
                            // TODO: Implement date navigation logic
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text(
                            text = ">",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stats Cards (Average and Max)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Average Card
                    StatsCard(
                        modifier = Modifier.weight(1f),
                        label = "Average",
                        value = "8h"
                    )

                    // Max Card
                    StatsCard(
                        modifier = Modifier.weight(1f),
                        label = "Max",
                        value = "10h",
                        showDot = true
                    )
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
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        // Y-axis labels and bars
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            // Y-axis with better spacing
                            Column(
                                modifier = Modifier
                                    .width(35.dp)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text("10h", color = Color(0xFF8B7FDB), fontSize = 12.sp)
                                Text("8h", color = Color(0xFF8B7FDB), fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
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
                                // Sample data - replace with actual data
                                val barHeights = listOf(0.8f, 0.6f, 0.7f, 0.9f, 0.7f, 0.8f, 0.75f, 0.85f, 0.65f, 0.95f, 0.7f, 0.8f)

                                barHeights.forEach { height ->
                                    Box(
                                        modifier = Modifier
                                            .width(16.dp)
                                            .fillMaxHeight(height)
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
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

                        Spacer(modifier = Modifier.height(8.dp))

                        // X-axis labels
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 47.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("5", color = Color(0xFF8B7FDB), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text("15", color = Color(0xFF8B7FDB), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text("30", color = Color(0xFF8B7FDB), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
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
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StatisticsViewPreview() {
    StatisticsView()
}