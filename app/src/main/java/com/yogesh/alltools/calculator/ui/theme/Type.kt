package com.yogesh.alltools.calculator.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yogesh.alltools.R

val MontserratBold = FontFamily(Font(R.font.montserrat_bold))
val MontserratLight = FontFamily(Font(R.font.montserrat_light))

val Typography = Typography(
    displayMedium = TextStyle(
        fontFamily = MontserratBold,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    ),
    displayLarge = TextStyle(
        fontFamily = MontserratBold,
        fontWeight = FontWeight.Bold,
        fontSize = 90.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = MontserratLight,
        fontWeight = FontWeight.Thin,
        fontSize = 20.sp
    )
)