package com.example.granaplay.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment

class NotificationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // Gerenciamento de memória do Compose dentro do Fragment
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                CityScreen()
            }
        }
    }
}

// ========================================================================
// COMPOSABLE (UI)
// ========================================================================

private val BackgroundColor = Color(0xFFDFF3FF)
private val BottomNavPadding = 80.dp // Espaço para não cobrir o menu inferior

@Composable
fun CityScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .statusBarsPadding()
    ) {
        // Conteúdo centralizado respeitando o menu inferior
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = BottomNavPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Minha Cidade",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.DarkGray
            )
        }
    }
}