package com.example.granaplay.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.granaplay.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val backgroundColor = Color(0xFFDFF3FF)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                        .statusBarsPadding()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp)
                    ) {
                        GameTopBar()

                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "Mapa e Fases aqui...",
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENTES ATUALIZADOS ---

@Composable
fun GameTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lado Esquerdo
        Row(verticalAlignment = Alignment.CenterVertically) {

            // --- ROBÔ (Personalizado) ---
            StatusPill(
                icon = painterResource(id = R.drawable.ic_robot_face),
                value = "50",
                pillColor = Color(0xFF5FA8D3),

                // 1. TAMANHO DO ROBÔ:
                // Aqui você define o tamanho SÓ DELE.
                // Tente aumentar para 54.dp ou 56.dp se quiser ele maior que a moeda.
                iconSize = 52.dp,

                // 2. PADDING DO TEXTO:
                // Se o robô ficar muito grande, aumente aqui para o texto não esconder atrás dele
                textPaddingStart = 44.dp,

                // 3. POSIÇÃO VERTICAL:
                // Ajuste para subir/descer a antena
                iconOffsetY = (-6).dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // --- MOEDA (Padrão) ---
            StatusPill(
                icon = painterResource(id = R.drawable.ic_coin),
                value = "3000",
                pillColor = Color(0xFF5FA8D3),

                // Não passei iconSize, então ele usa o padrão (48.dp)
                // Não passei iconOffsetY, então usa o padrão (0.dp)
                textPaddingStart = 40.dp
            )
        }

        // Lado Direito
        HeartDisplay(lives = 5)
    }
}

@Composable
fun StatusPill(
    icon: Painter,
    value: String,
    pillColor: Color,
    textPaddingStart: Dp,
    iconOffsetY: Dp = 0.dp,
    iconSize: Dp = 48.dp // <--- NOVO PARÂMETRO COM VALOR PADRÃO
) {
    // A altura do Box pai agora obedece o tamanho do ícone que você escolheu
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier.height(iconSize)
    ) {
        // A Pílula continua com altura fixa de 34dp para manter o padrão visual
        Box(
            modifier = Modifier
                .padding(start = 10.dp)
                .height(34.dp)
                .clip(RoundedCornerShape(50))
                .background(pillColor)
                .padding(start = textPaddingStart, end = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }

        // O Ícone usa o tamanho dinâmico (iconSize)
        Image(
            painter = icon,
            contentDescription = null,
            modifier = Modifier
                .size(iconSize) // <--- USA O TAMANHO QUE VOCÊ PASSOU
                .align(Alignment.CenterStart)
                .offset(y = iconOffsetY),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun HeartDisplay(lives: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(lives) {
            Image(
                painter = painterResource(id = R.drawable.ic_heart),
                contentDescription = "Vida",
                modifier = Modifier
                    .size(24.dp)
                    .padding(2.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}