package com.oficialexdev.musclefire.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource

@Composable
fun MuscleFireTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme)
                darkColorScheme(
                    primary = colorResource(id = android.R.color.system_accent1_300),
                    onPrimary = Color.Black,
                    secondary = colorResource(id = android.R.color.system_accent1_100),
                    tertiary = colorResource(id = android.R.color.system_accent1_500)
                )
            else lightColorScheme(
                primary = colorResource(id = android.R.color.system_accent1_400),
                onPrimary = Color.White,
                secondary = colorResource(id = android.R.color.system_accent1_800),
                tertiary = colorResource(id = android.R.color.system_accent1_600)
            )
        }

        darkTheme -> darkColorScheme(
            primary = Light80,
            secondary = LightGrey80,
            tertiary = LightS80
        )

        else -> lightColorScheme(
            primary = Dark40,
            secondary = DarkGrey40,
            tertiary = DarkS40
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}