package com.example.aulalink.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Heading,
    background = BackgroundDark,
    onBackground = Heading,
    surface = CardSurface,
    onSurface = TextPrimary
)
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = Heading,
    background = DarkBackground,
    onBackground = Heading,
    surface = DarkSurface,
    onSurface = TextPrimary
)

@Composable
fun AulaLinkTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}
