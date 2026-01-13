package com.example.granaplay.ui.dashboard

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

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // Garante que o Compose limpe a memória quando a View do Fragment for destruída
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                DashboardScreen()
            }
        }
    }
}

// ========================================================================
// COMPOSABLE (UI)
// ========================================================================

private val BackgroundColor = Color(0xFFDFF3FF)
private val BottomNavPadding = 80.dp // Espaço reservado para o menu inferior

@Composable
fun DashboardScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            // statusBarsPadding: Garante que o conteúdo comece abaixo da barra de status
            .statusBarsPadding()
    ) {
        // Conteúdo Principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = BottomNavPadding), // Evita que o conteúdo fique atrás do menu
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tarefas e Missões",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.DarkGray
            )
        }
    }
}