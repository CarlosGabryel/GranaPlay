package com.example.granaplay.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
// CONFIGURAÇÕES VISUAIS
// ========================================================================

private object NavBarStyles {
    val Height = 60.dp
    val TotalHeight = 80.dp // Altura total incluindo a aba flutuante

    // Gradiente da Aba Ativa (Ciano)
    val ActiveTabGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF00E5FF), Color(0xFF00838F))
    )

    // Gradiente do Fundo (Azul Petróleo)
    val BackgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF2C8CAE), Color(0xFF006386))
    )

    val DividerColor = Color(0xFF004D40)
    val IconActiveColor = Color.White
    val IconInactiveColor = Color.White
}

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

/**
 * Barra de navegação inferior customizada com efeito de aba flutuante ("Floating Tab").
 *
 * A lógica visual funciona em duas camadas sobrepostas:
 * 1. [BackgroundLayer]: Renderiza o fundo e os itens não selecionados.
 * 2. [ActiveTabLayer]: Renderiza apenas a aba selecionada na posição correta usando pesos (weights).
 */
@Composable
fun CustomBottomNavigation(
    items: List<BottomNavItem>,
    currentDestinationId: Int?,
    onNavigate: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(NavBarStyles.TotalHeight),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Camada 1: Fundo e itens inativos
        BackgroundLayer(items, currentDestinationId, onNavigate)

        // Camada 2: Aba ativa (flutuante)
        ActiveTabLayer(items, currentDestinationId)
    }
}

// ========================================================================
// IMPLEMENTAÇÃO INTERNA
// ========================================================================

@Composable
private fun BackgroundLayer(
    items: List<BottomNavItem>,
    currentDestinationId: Int?,
    onNavigate: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(NavBarStyles.Height)
            .background(NavBarStyles.BackgroundGradient),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = currentDestinationId == item.navDestinationId

            // Ocupa o espaço (weight 1f) mas só desenha o ícone se NÃO estiver selecionado.
            // Se estiver selecionado, fica vazio para a ActiveTabLayer preencher por cima.
            Box(modifier = Modifier.weight(1f)) {
                if (!isSelected) {
                    UnselectedItem(
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
private fun ActiveTabLayer(
    items: List<BottomNavItem>,
    currentDestinationId: Int?
) {
    val selectedIndex = items.indexOfFirst { it.navDestinationId == currentDestinationId }
    if (selectedIndex == -1) return

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        // Empurra a aba para a direita baseada na quantidade de itens anteriores
        if (selectedIndex > 0) {
            Spacer(modifier = Modifier.weight(selectedIndex.toFloat()))
        }

        // A aba ativa em si
        ActiveTabIndicator(item = items[selectedIndex])

        // Empurra o restante para preencher a linha (itens posteriores)
        val itemsAfter = items.size - 1 - selectedIndex
        if (itemsAfter > 0) {
            Spacer(modifier = Modifier.weight(itemsAfter.toFloat()))
        }
    }
}

@Composable
private fun UnselectedItem(
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
            tint = NavBarStyles.IconInactiveColor,
            modifier = Modifier.size(32.dp)
        )

        if (showDivider) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(1.dp)
                    .fillMaxHeight(0.6f)
                    .background(NavBarStyles.DividerColor)
            )
        }
    }
}

@Composable
private fun RowScope.ActiveTabIndicator(item: BottomNavItem) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(NavBarStyles.TotalHeight)
            .zIndex(1f)
            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            .background(NavBarStyles.ActiveTabGradient),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = NavBarStyles.IconActiveColor,
            modifier = Modifier
                .size(55.dp)
                .padding(bottom = 8.dp)
        )
    }
}