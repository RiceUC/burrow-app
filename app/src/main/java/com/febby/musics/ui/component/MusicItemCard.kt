package com.febby.musics.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.febby.musics.model.MusicModel

@Composable
fun MusicItemCard(item: MusicModel, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Card(shape = CircleShape, elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
            Image(painter = painterResource(id = item.imageRes), contentDescription = item.title, modifier = Modifier.size(120.dp).clip(CircleShape), contentScale = ContentScale.Crop)
        }
        Text(
            text = item.title,
            color = Color.White
        )
    }
}

