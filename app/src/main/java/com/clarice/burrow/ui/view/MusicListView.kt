package com.clarice.burrow.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.clarice.burrow.R
import com.clarice.burrow.ui.model.music.MusicModel
import com.clarice.burrow.ui.viewmodel.MusicViewModel

@Composable
fun MusicListView(navController: NavController, vm: MusicViewModel) {

    val musicList by vm.musicList.collectAsState()
    val aboutTimer by vm.aboutDuration.collectAsState(initial = 30)
    val whileTimer by vm.whileDuration.collectAsState(initial = 10)
    val durations = vm.timerOptions

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.musiclistview),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF5B66B8))
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Button(
                    enabled = vm.canPlay(),
                    onClick = {
                        navController.navigate("player")
                        vm.startPlayer()
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5B66B8),
                        disabledContainerColor = Color(0xFF3F447A)
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Play", color = Color.White)
                        Spacer(Modifier.width(6.dp))
                        Text(">", color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sleep Music", color = Color.White)
                Text(
                    "Music will play when you start the sleep tracker ~",
                    color = Color(0xFFAFB8D0)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text("About to sleep", color = Color.White)
            Spacer(Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(musicList.filter { it.category == "About to sleep" }
                ) { item ->
                    MusicItemCard(item = item) {
                        vm.selectAboutMusic(item)
                    }
                }
            }

            DurationRow(
                title = "Play for",
                selected = aboutTimer ?: 30,
                options = durations,
                onSelect = vm::setAboutDuration
            )

            Spacer(Modifier.height(20.dp))

            Text("While sleeping", color = Color.White)
            Spacer(Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(musicList.filter { it.category == "While sleeping" }) { item ->
                    MusicItemCard(item = item) {
                        vm.selectWhileMusic(item)
                    }
                }
            }

            DurationRow(
                title = "Play for",
                selected = whileTimer ?: 10,
                options = durations,
                onSelect = vm::setWhileDuration
            )
        }
    }
}

@Composable
fun MusicItemCard(item: MusicModel, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Card(
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Image(
                painter = painterResource(item.imageRes),
                contentDescription = item.title,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Text(item.title, color = Color.White)
    }
}
@Composable
fun DurationRow(
    title: String,
    selected: Int,
    options: List<Int>,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(title, color = Color.White)

        Spacer(Modifier.width(12.dp))

        Box {

            Box(
                modifier = Modifier
                    .height(42.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF8998D9))
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$selected Minutes", color = Color.White)
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        imageVector = if (expanded)
                            Icons.Filled.KeyboardArrowUp
                        else
                            Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(180.dp)
                    .background(Color(0xFF2B2F55))
            ) {
                options.forEach { minute ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                "$minute Minutes",
                                color = Color.White
                            )
                        },
                        onClick = {
                            onSelect(minute)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}