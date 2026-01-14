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

// ========================================================================
// CONFIGURAÇÕES DE ESTILO E CORES
// ========================================================================

private val BarHeight = 60.dp
private val TotalHeight = 80.dp // Altura considerando a aba que "salta" para cima

// Cores da Aba Selecionada (Gradiente Ciano)
private val TabGradientStart = Color(0xFF00E5FF)
private val TabGradientEnd = Color(0xFF00838F)

// Cores da Barra de Fundo (Gradiente Azul Petróleo)
private val BarGradientStart = Color(0xFF2C8CAE)
private val BarGradientEnd = Color(0xFF006386)

private val DividerColor = Color(0xFF004D40)
private val IconActiveColor = Color.White
private val IconInactiveColor = Color.White // Pode alterar se quiser opacidade diferente

// ========================================================================
// MODELO DE DADOS
// ========================================================================

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val navDestinationId: Int
)

// ========================================================================
// COMPONENTE PRINCIPAL
// ========================================================================

@Composable
fun CustomBottomNavigation(
    items: List<BottomNavItem>,
    currentDestinationId: Int?,
    onNavigate: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(TotalHeight),
        contentAlignment = Alignment.BottomCenter
    ) {
        // CAMADA 1: Barra de Fundo (Itens Inativos)
        BackgroundBar(items, currentDestinationId, onNavigate)

        // CAMADA 2: Indicador Flutuante (Item Selecionado)
        FloatingSelectedTab(items, currentDestinationId)
    }
}

// ========================================================================
// SUB-COMPONENTES
// ========================================================================

@Composable
private fun BackgroundBar(
    items: List<BottomNavItem>,
    currentDestinationId: Int?,
    onNavigate: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(BarHeight)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BarGradientStart, BarGradientEnd)
                )
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = currentDestinationId == item.navDestinationId

            // Se estiver selecionado, renderiza um espaço vazio (pois o item flutuante ocupará este lugar)
            // Se não, renderiza o ícone clicável padrão
            Box(modifier = Modifier.weight(1f)) {
                if (!isSelected) {
                    UnselectedItemView(
                        item = item,
                        showDivider = index < items.size - 1,
                        onClick = { onNavigate(item.navDestinationId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingSelectedTab(
    items: List<BottomNavItem>,
    currentDestinationId: Int?
) {
    val selectedIndex = items.indexOfFirst { it.navDestinationId == currentDestinationId }

    if (selectedIndex >= 0) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            // Espaçador à esquerda (ocupa o peso equivalente aos itens anteriores)
            if (selectedIndex > 0) {
                Spacer(modifier = Modifier.weight(selectedIndex.toFloat()))
            }

            // O Item Selecionado em si
            SelectedTabIndicator(item = items[selectedIndex])

            // Espaçador à direita (ocupa o peso equivalente aos itens posteriores)
            val itemsAfter = items.size - 1 - selectedIndex
            if (itemsAfter > 0) {
                Spacer(modifier = Modifier.weight(itemsAfter.toFloat()))
            }
        }
    }
}

@Composable
private fun UnselectedItemView(
    item: BottomNavItem,
    showDivider: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = IconInactiveColor,
            modifier = Modifier.size(32.dp)
        )

        // Linha divisória vertical à direita
        if (showDivider) {
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

@Composable
private fun RowScope.SelectedTabIndicator(item: BottomNavItem) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(TotalHeight)
            .zIndex(1f) // Garante que fique acima da barra de fundo
            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(TabGradientStart, TabGradientEnd)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title, // Importante para acessibilidade
            tint = IconActiveColor,
            modifier = Modifier
                .size(55.dp) // Ícone bem grande para destaque
                .padding(bottom = 8.dp) // Ajuste visual para centralizar na parte visível
        )
    }
}