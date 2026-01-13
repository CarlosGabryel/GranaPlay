package com.example.granaplay.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.granaplay.R

// 1. Definição da Família da Fonte Baloo
val BalooFontFamily = FontFamily(
    Font(R.font.baloo_regular, FontWeight.Normal)
)

val Baloo2FontFamily = FontFamily(
    Font(R.font.baloo2_regular, FontWeight.Normal),
    Font(R.font.baloo2_bold, FontWeight.Bold),
    Font(R.font.baloo2_medium, FontWeight.Medium),
    Font(R.font.baloo2_semi_bold, FontWeight.SemiBold),
    Font(R.font.baloo2_extra_bold, FontWeight.ExtraBold)
)

// 2. Configuração Padrão do Material Design (Opcional, mas recomendado)
// Isso permite que você use MaterialTheme.typography no futuro e já venha com a Baloo
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = BalooFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = BalooFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )
)