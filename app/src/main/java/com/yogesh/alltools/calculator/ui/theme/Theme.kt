package com.yogesh.calculator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.yogesh.alltools.calculator.ui.theme.Typography

@Composable
fun SimpleCalcTheme(content: @Composable () -> Unit) {

    MaterialTheme(
        typography = Typography, content = content
    )
}