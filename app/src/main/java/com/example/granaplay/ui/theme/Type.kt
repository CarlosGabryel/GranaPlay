package com.example.granaplay.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.granaplay.R

// ========================================================================
// 1. DEFINIÇÃO DAS FAMÍLIAS DE FONTE
// ========================================================================

val BalooFontFamily = FontFamily(
    Font(R.font.baloo_regular, FontWeight.Normal)
)

// Baloo 2 é mais completa (vários pesos), ideal para ser a fonte principal
val Baloo2FontFamily = FontFamily(
    Font(R.font.baloo2_regular, FontWeight.Normal),
    Font(R.font.baloo2_medium, FontWeight.Medium),
    Font(R.font.baloo2_semi_bold, FontWeight.SemiBold),
    Font(R.font.baloo2_bold, FontWeight.Bold),
    Font(R.font.baloo2_extra_bold, FontWeight.ExtraBold)
)

// ========================================================================
// 2. CONFIGURAÇÃO DE TIPOGRAFIA (Material Design 3)
// ========================================================================

// Estilo base para reduzir repetição de código
private val BaseTextStyle = TextStyle(
    fontFamily = Baloo2FontFamily, // Define Baloo 2 como padrão para tudo
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.sp
)

val Typography = Typography(

    // --- Títulos Grandes (Headlines) ---
    headlineLarge = BaseTextStyle.copy(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = BaseTextStyle.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),

    // --- Títulos de Seções (Titles) ---
    // Usado em TopAppBar, cabeçalhos de cards, etc.
    titleLarge = BaseTextStyle.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = BaseTextStyle.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),

    // --- Corpo do Texto (Body) ---
    // Usado em parágrafos e textos longos
    bodyLarge = BaseTextStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = BaseTextStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),

    // --- Etiquetas e Botões (Label) ---
    // Usado dentro de Botões, Tabs e legendas pequenas
    labelLarge = BaseTextStyle.copy(
        fontWeight = FontWeight.SemiBold, // Botões ficam melhores com peso maior
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelSmall = BaseTextStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)