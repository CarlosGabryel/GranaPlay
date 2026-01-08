package com.example.granaplay.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

// --- Cores do Gradiente da ABA SELECIONADA (Ciano/Azul Claro) ---
val TabGradientStart = Color(0xFF00E5FF)
val TabGradientEnd = Color(0xFF00838F)

// --- Cores do Gradiente da BARRA DE FUNDO (Extraídas da sua imagem) ---
val BarGradientStart = Color(0xFF2C8CAE)
val BarGradientEnd = Color(0xFF006386)

val DividerColor = Color(0xFF004D40) // Cor da linha divisória

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val navDestinationId: Int
)

@Composable
fun CustomBottomNavigation(
    items: List<BottomNavItem>,
    currentDestinationId: Int?,
    onNavigate: (Int) -> Unit
) {
    val totalHeight = 80.dp // Altura total da aba selecionada
    val barHeight = 60.dp   // Altura da barra de fundo

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(totalHeight),
        contentAlignment = Alignment.BottomCenter
    ) {
        // --- CAMADA 1: Barra de Fundo com Gradiente e Itens Inativos ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                // AQUI: Aplicando o degradê na barra de fundo
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BarGradientStart, BarGradientEnd)
                    )
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = currentDestinationId == item.navDestinationId

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(enabled = !isSelected) { onNavigate(item.navDestinationId) },
                    contentAlignment = Alignment.Center
                ) {
                    if (!isSelected) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = Color.White, // Ícones inativos agora são BRANCOS
                            modifier = Modifier.size(32.dp) // Ícones maiores
                        )
                    }

                    // Linha divisória
                    if (index < items.size - 1) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .width(1.dp)
                                .fillMaxHeight(0.6f)
                                .background(DividerColor)
                        )
                    }
                }
            }
        }

        // --- CAMADA 2: O Item Selecionado "Flutuante" ---
        val selectedIndex = items.indexOfFirst { it.navDestinationId == currentDestinationId }
        if (selectedIndex >= 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                for (i in 0 until selectedIndex) {
                    Spacer(modifier = Modifier.weight(1f))
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(totalHeight)
                        .zIndex(1f)
                        .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                        // Gradiente da aba selecionada
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(TabGradientStart, TabGradientEnd)
                            )
                        )
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = items[selectedIndex].icon,
                        contentDescription = items[selectedIndex].title,
                        tint = Color.White,
                        // AQUI: Ícone selecionado AINDA MAIOR
                        modifier = Modifier
                            .size(55.dp)
                            .padding(bottom = 8.dp)
                    )
                }

                for (i in selectedIndex + 1 until items.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}