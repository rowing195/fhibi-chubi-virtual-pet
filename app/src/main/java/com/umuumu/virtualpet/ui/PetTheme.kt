package com.umuumu.virtualpet.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Day = lightColorScheme(
    primary = Color(0xFF536344), onPrimary = Color(0xFFFFFDF8),
    primaryContainer = Color(0xFFF0E4BF), onPrimaryContainer = Color(0xFF342F29),
    secondary = Color(0xFF536344), onSecondary = Color(0xFFFFFDF8),
    secondaryContainer = Color(0xFFEEE9DC), onSecondaryContainer = Color(0xFF342F29),
    background = Color(0xFFF8F5ED), onBackground = Color(0xFF342F29),
    surface = Color(0xFFFFFDF8), onSurface = Color(0xFF342F29),
    surfaceVariant = Color(0xFFEEE9DC), onSurfaceVariant = Color(0xFF6D6558),
    surfaceTint = Color(0xFF536344),
    surfaceDim = Color(0xFFE7E1D5), surfaceBright = Color(0xFFFFFDF8),
    surfaceContainerLowest = Color(0xFFFFFDF8), surfaceContainerLow = Color(0xFFF8F5ED),
    surfaceContainer = Color(0xFFF3EFE4), surfaceContainerHigh = Color(0xFFEEE9DC),
    surfaceContainerHighest = Color(0xFFE7E1D5),
    outline = Color(0xFF8B8373), outlineVariant = Color(0xFFDFD9CC),
    error = Color(0xFFA74437),
)
private val Night = darkColorScheme(
    primary = Color(0xFFC0D09D), onPrimary = Color(0xFF25281F),
    primaryContainer = Color(0xFF42402B), onPrimaryContainer = Color(0xFFF5F0DF),
    secondary = Color(0xFFC0D09D), onSecondary = Color(0xFF25281F),
    secondaryContainer = Color(0xFF343329), onSecondaryContainer = Color(0xFFF5F0DF),
    background = Color(0xFF252720), onBackground = Color(0xFFF5F0DF),
    surface = Color(0xFF30322A), onSurface = Color(0xFFF5F0DF),
    surfaceVariant = Color(0xFF343329), onSurfaceVariant = Color(0xFFC0BAA8),
    surfaceTint = Color(0xFFC0D09D),
    surfaceDim = Color(0xFF252720), surfaceBright = Color(0xFF42443A),
    surfaceContainerLowest = Color(0xFF20221C), surfaceContainerLow = Color(0xFF292B23),
    surfaceContainer = Color(0xFF30322A), surfaceContainerHigh = Color(0xFF34362D),
    surfaceContainerHighest = Color(0xFF3B3D33),
    outline = Color(0xFF918D79), outlineVariant = Color(0xFF49483C),
    error = Color(0xFFFFB4A4),
)

@Composable
fun PetTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) Night else Day,
        typography = Typography(
            headlineMedium = TextStyle(fontSize = 28.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold),
            titleLarge = TextStyle(fontSize = 19.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold),
            titleMedium = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
            bodyLarge = TextStyle(fontSize = 15.sp, lineHeight = 24.sp),
            bodyMedium = TextStyle(fontSize = 13.sp, lineHeight = 20.sp),
            labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
        ),
        shapes = Shapes(medium = RoundedCornerShape(18.dp), large = RoundedCornerShape(18.dp), extraLarge = RoundedCornerShape(28.dp)),
        content = content,
    )
}
