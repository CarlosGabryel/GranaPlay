package com.example.granaplay.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // REMOVIDO: Não precisamos mais manipular window flags aqui.
        // A MainActivity já cuida do edge-to-edge.

        return ComposeView(requireContext()).apply {
            setContent {
                val backgroundColor = Color(0xFFDFF3FF)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                        // CORREÇÃO: Usa apenas statusBarsPadding.
                        // Isso garante o padding no topo (bateria/hora),
                        // mas deixa o fundo azul descer até o final da tela.
                        .statusBarsPadding()
                ) {
                    // Adicione um padding bottom no CONTEÚDO (não no fundo)
                    // para que os itens da lista não fiquem escondidos atrás do menu
                    Box(modifier = Modifier.padding(bottom = 80.dp)) {
                        // Seu conteúdo da Home (Textos, Listas, etc) vem aqui
                    }
                }
            }
        }
    }
}