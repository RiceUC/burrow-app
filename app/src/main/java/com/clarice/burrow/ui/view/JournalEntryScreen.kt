package com.kiara.journal.ui.journal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clarice.burrow.R
import com.clarice.burrow.ui.model.journal.MoodType
import android.util.Log
import com.clarice.burrow.ui.viewmodel.JournalViewModel
import kotlin.text.isEmpty
import kotlin.text.isNotEmpty

@Composable
fun JournalEntryScreen(
    viewModel: JournalViewModel,
    userId: Int,
    journalId: Int?,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    var selectedMood by remember { mutableStateOf(MoodType.HAPPY) }
    var content by remember { mutableStateOf("") }

    val isEditMode = journalId != null
    val currentJournal by viewModel.currentJournal.collectAsState()
    val journal by viewModel.currentJournal.collectAsState()


    // LaunchedEffect untuk load & untuk observe data
    LaunchedEffect(journalId) {
        if (isEditMode && journalId != null) {
            Log.d("JournalEntryScreen", "Triggering load for journalId=$journalId")
            viewModel.loadJournal(journalId)
        }
    }

    //update UI ketika currentJournal berubah
    LaunchedEffect(currentJournal) {
        if (currentJournal != null) {
            Log.d("JournalEntryScreen", "Updating UI with journal: ${currentJournal!!.content}, mood=${currentJournal!!.mood}")
            content = currentJournal!!.content
            selectedMood = try {
                MoodType.valueOf(currentJournal!!.mood)
            } catch (e: Exception) {
                Log.e("JournalEntryScreen", "Invalid mood: ${currentJournal!!.mood}")
                MoodType.HAPPY
            }
            Log.d("JournalEntryScreen", "After update: content=$content, selectedMood=$selectedMood")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.journalentryviewbg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onBack() }
                )

                Text(
                    text = if (isEditMode) "Update" else "Save",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        if (content.isNotEmpty()) {
                            if (isEditMode && journalId != null) {
                                Log.d("JournalEntryScreen", "Updating journal id=$journalId, mood=$selectedMood")
                                viewModel.updateJournalData(
                                    journalId = journalId,
                                    userId = userId,
                                    content = content,
                                    mood = selectedMood
                                )
                            } else {
                                Log.d("JournalEntryScreen", "Creating new journal, mood=$selectedMood")
                                viewModel.addJournal(
                                    content = content,
                                    mood = selectedMood,
                                    userId = userId
                                )
                            }
                            onSaved()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (isEditMode) "Edit Entry" else "Today's Entry",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(36.dp))

            MoodRow(
                selectedMood = selectedMood,
                onMoodSelected = { selectedMood = it }
            )

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF4856A4).copy(alpha = 0.90f))
                    .padding(20.dp)
            ) {

                if (content.isEmpty()) {
                    Text(
                        "How was your day?",
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 16.sp
                    )
                }

                BasicTextField(
                    value = content,
                    onValueChange = { content = it },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}


@Composable
fun MoodRow(
    selectedMood: MoodType,
    onMoodSelected: (MoodType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MoodButton(R.drawable.smiling, MoodType.HAPPY, selectedMood, onMoodSelected)
        MoodButton(R.drawable.crying, MoodType.SAD, selectedMood, onMoodSelected)
        MoodButton(R.drawable.tired, MoodType.TIRED, selectedMood, onMoodSelected)
        MoodButton(R.drawable.angry, MoodType.ANGRY, selectedMood, onMoodSelected)
    }
}

@Composable
fun MoodButton(
    iconRes: Int,
    type: MoodType,
    selectedMood: MoodType,
    onMoodSelected: (MoodType) -> Unit
) {
    val isSelected = selectedMood == type
    val bg = if (isSelected) Color(0xFF7B90FC) else Color(0xFF4856A4)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(70.dp)
            .clickable { onMoodSelected(type) }
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(34.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
    }
}
