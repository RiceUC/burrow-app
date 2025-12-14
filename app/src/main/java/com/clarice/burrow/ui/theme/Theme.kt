package com.kiara.journal.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.kiara.journal.R
import androidx.compose.ui.unit.sp

// warna dari kamu
val PurpleBase = Color(0xFF4856A4)   // card & mood base
val PurpleActive = Color(0xFF7B90FC) // mood active
val BackgroundDeep = Color(0xFF07102A) // fallback

private val DarkColorPalette = darkColors(
    primary = PurpleActive,
    primaryVariant = PurpleBase,
    background = BackgroundDeep,
    surface = PurpleBase,
    onSurface = Color.White
)

val HelveticaNeue = FontFamily(
    Font(R.font.helveticaneueblack, weight = FontWeight.Black),
    Font(R.font.helveticaneuebold, weight = FontWeight.Bold),
    Font(R.font.helveticaneuelight, weight = FontWeight.Light)
)

private val AppTypography = Typography(
    h4 = androidx.compose.ui.text.TextStyle(fontFamily = HelveticaNeue, fontWeight = FontWeight.Bold, fontSize = 28.sp),
    h6 = androidx.compose.ui.text.TextStyle(fontFamily = HelveticaNeue, fontWeight = FontWeight.Bold, fontSize = 20.sp),
    body1 = androidx.compose.ui.text.TextStyle(fontFamily = HelveticaNeue, fontWeight = FontWeight.Light, fontSize = 16.sp)
)

@Composable
fun JournalTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = AppTypography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}
