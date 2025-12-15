package com.clarice.burrow.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.clarice.burrow.R
import com.clarice.burrow.ui.model.journal.Journal
import com.clarice.burrow.ui.model.journal.MoodType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.clarice.burrow.ui.viewmodel.JournalViewModel
import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime

fun formatDateFromIso(isoDateString: String): String {
    return try {
        val zonedDateTime = ZonedDateTime.parse(isoDateString)
        val localDate = zonedDateTime.toLocalDate()
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        localDate.format(formatter)
    } catch (_: Exception) {
        isoDateString.substringBefore("T")
    }
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = this.then(
    Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = { onClick() })
    }
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun JournalListScreen(
    viewModel: JournalViewModel,
    userId: Int,
    onAdd: () -> Unit,
    onOpen: (Int) -> Unit
) {
    // Debug: Log fetch action BEFORE calling API
    LaunchedEffect(userId) {
        try {
            android.util.Log.d("JournalListScreen", "Fetching journals for userId: $userId")
            viewModel.fetchJournals(userId)
        } catch (e: Exception) {
            android.util.Log.e("JournalListScreen", "Error fetching journals: ${e.message}", e)
        }
    }

    val journals by viewModel.journals.collectAsState(initial = emptyList())
    
    // Debug log on data update
    LaunchedEffect(journals) {
        android.util.Log.d("JournalListScreen", "Journals updated: ${journals.size} items")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1B4B)) // Fallback background jika image fail
    ) {

        Image(
            painter = painterResource(id = R.drawable.journallistviewbg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay untuk contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E1B4B).copy(alpha = 0.4f),
                            Color(0xFF1E1B4B).copy(alpha = 0.7f)
                        )
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(modifier = Modifier.height(46.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "Journal",
                    fontSize = 34.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Empty your mind after a long day ^-^",
                    color = Color(0xFFBFC7FF)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(bottom = 140.dp, top = 10.dp)
            ) {
                if (journals.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "📝",
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No journals yet",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Click + to create your first entry!",
                                    color = Color(0xFFBFC7FF),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else {
                    items(items = journals, key = { it.journal_id }) { journal ->

                    val dismissState = rememberDismissState(confirmStateChange = { dismissValue ->
                        if (dismissValue == DismissValue.DismissedToStart) {
                            viewModel.deleteJournal(journal.journal_id, userId)
                        }
                        true
                    })

                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(DismissDirection.EndToStart),
                        background = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(end = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFFF5252),
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        },
                        dismissContent = {
                            JournalCard(journal, onClick = { onOpen(journal.journal_id) })
                        }
                    )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAdd,
            containerColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 26.dp, bottom = 40.dp)
                .size(70.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add",
                tint = Color(0xFF07102A),
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun BottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        tonalElevation = 12.dp,
        shadowElevation = 12.dp,
        color = Color(0xFF0C1A46),
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            BottomBarItem(
                title = "Tracker",
                selected = selectedTab == 0,
                icon = Icons.Filled.NightlightRound,
                onClick = { onTabSelected(0) }
            )

            BottomBarItem(
                title = "Journal",
                selected = selectedTab == 1,
                icon = Icons.AutoMirrored.Filled.LibraryBooks,
                onClick = { onTabSelected(1) }
            )

            BottomBarItem(
                title = "Music",
                selected = selectedTab == 2,
                icon = Icons.Filled.MusicNote,
                onClick = { onTabSelected(2) }
            )

            BottomBarItem(
                title = "Profile",
                selected = selectedTab == 3,
                icon = Icons.Filled.Person,
                onClick = { onTabSelected(3) }
            )
        }
    }
}

@Composable
fun BottomBarItem(
    title: String,
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {

    val bgColor =
        if (selected) Color(0xFF7B90FC) else Color.Transparent

    val contentColor =
        if (selected) Color.White else Color.LightGray.copy(alpha = 0.8f)

    Column(
        modifier = Modifier
            .width(70.dp)
            .height(70.dp)
            .background(bgColor, shape = RoundedCornerShape(18.dp))
            .padding(8.dp)
            .padding(top = 5.dp)
            .noRippleClickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor,
            modifier = Modifier.size(26.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(title, color = contentColor, fontSize = 12.sp)
    }
}

@Composable
fun JournalCard(journal: Journal, onClick: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF4856A4).copy(alpha = 0.95f))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = formatDateFromIso(journal.created_at),
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = journal.content,
                fontSize = 14.sp,
                color = Color(0xFFDFE6FF),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        val moodRes = when (journal.mood.uppercase()) {
            MoodType.HAPPY.name -> R.drawable.smiling
            MoodType.SAD.name -> R.drawable.crying
            MoodType.TIRED.name -> R.drawable.tired
            MoodType.ANGRY.name -> R.drawable.angry
            else -> R.drawable.smiling
        }

        Image(
            painter = painterResource(id = moodRes),
            contentDescription = null,
            modifier = Modifier.size(36.dp)
        )
    }
}
