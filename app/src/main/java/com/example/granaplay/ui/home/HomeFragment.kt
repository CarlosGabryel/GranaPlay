package com.example.granaplay.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.granaplay.R
import com.example.granaplay.data.AppDatabase
import com.example.granaplay.data.GameRepository
import com.example.granaplay.data.GameViewModel
import com.example.granaplay.data.GameViewModelFactory
import com.example.granaplay.data.ModuloEstado
import com.example.granaplay.ui.theme.Baloo2FontFamily
import com.example.granaplay.ui.theme.BalooFontFamily

class HomeFragment : Fragment() {

    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dao = AppDatabase.getDatabase(requireContext()).gameDao()
        val repository = GameRepository(dao)
        val factory = GameViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[GameViewModel::class.java]

        val userId = 1L
        viewModel.carregarDadosUsuario(userId)

        return ComposeView(requireContext()).apply {
            setContent {
                val backgroundColor = Color(0xFFDFF3FF)
                // Observa o estado de Loading
                val isLoading by viewModel.isLoading.observeAsState(true)

                // Lógica de Troca de Telas
                if (isLoading) {
                    LoadingScreen(backgroundColor)
                } else {
                    GameScreen(viewModel, backgroundColor)
                }
            }
        }
    }
}

// --- TELA DE LOADING ---
@Composable
fun LoadingScreen(backgroundColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_robot_face),
                contentDescription = "Carregando",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(
                color = Color(0xFF258EB6),
                strokeWidth = 6.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Preparando o Jogo...",
                fontFamily = BalooFontFamily,
                fontSize = 20.sp,
                color = Color(0xFF136F91)
            )
        }
    }
}

// --- TELA DO JOGO ---
@Composable
fun GameScreen(viewModel: GameViewModel, backgroundColor: Color) {
    val usuario by viewModel.usuarioAtual?.observeAsState() ?: androidx.compose.runtime.mutableStateOf(null)
    val modulos by viewModel.estadoModulos.observeAsState(emptyList())

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
            GameTopBar(
                moedas = usuario?.moedas ?: 0,
                vidas = usuario?.pontosSaude ?: 5,
                xp = usuario?.xp ?: 0
            )

            WorldBanner()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Fundo (Estrada)
                Image(
                    painter = painterResource(id = R.drawable.path),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .fillMaxHeight(0.85f)
                        .align(Alignment.Center)
                )

                // Robô
                Image(
                    painter = painterResource(id = R.drawable.ic_robot_body),
                    contentDescription = "Robô",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(160.dp)
                        .padding(start = 22.dp)
                        .align(Alignment.TopStart)
                        .offset(y = (-53).dp)
                )

                // Lista de Módulos
                modulos.forEachIndexed { index, moduloEstado ->
                    val isLeftAligned = (index % 2 != 0)
                    val topOffset = 30.dp + (index * 120).dp
                    val alignModifier = if (isLeftAligned) Alignment.TopStart else Alignment.TopEnd
                    val paddingModifier = if (isLeftAligned)
                        Modifier.padding(top = topOffset, start = 18.dp)
                    else
                        Modifier.padding(top = topOffset, end = 18.dp)

                    Box(
                        modifier = Modifier
                            .align(alignModifier)
                            .then(paddingModifier)
                    ) {
                        LevelItem(
                            moduloEstado = moduloEstado,
                            isLeftAligned = isLeftAligned,
                            onClick = {
                                if (!moduloEstado.isBloqueado) {
                                    // TODO: Navegar para as lições
                                }
                            }
                        )
                    }
                }

                // Seta Final
                val lastModuleY = 30 + (modulos.size * 120)
                val finalY = if (modulos.isNotEmpty()) lastModuleY.dp else 400.dp

                Image(
                    painter = painterResource(id = R.drawable.ic_world_ending),
                    contentDescription = "Próximo Mundo",
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.TopEnd)
                        .offset(y = finalY - 50.dp, x = 18.dp)
                )
            }
        }
    }
}

// --- CORES ---
val BlueBanner = Color(0xFF258EB6)
val DarkBlueBanner = Color(0xFF136F91)
val TextModuloColor = Color(0xFF2C8CAE)
val TextLockedColor = Color(0xFF8FAAB6)

// --- COMPONENTES UI ---

@Composable
fun LevelItem(
    moduloEstado: ModuloEstado,
    isLeftAligned: Boolean,
    onClick: () -> Unit
) {
    val alignment = if (isLeftAligned) Arrangement.Start else Arrangement.End
    val gapSize = 0.dp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = alignment,
        modifier = Modifier
            .fillMaxWidth(1f)
            .clickable(enabled = !moduloEstado.isBloqueado) { onClick() }
    ) {
        if (isLeftAligned) {
            HexagonGroup(moduloEstado)
            Spacer(modifier = Modifier.width(gapSize))
            TextGroup(
                moduloEstado = moduloEstado,
                isLeftAligned = true,
                modifier = Modifier.weight(1f)
            )
        } else {
            TextGroup(
                moduloEstado = moduloEstado,
                isLeftAligned = false,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(gapSize))
            HexagonGroup(moduloEstado)
        }
    }
}

@Composable
fun HexagonGroup(moduloEstado: ModuloEstado) {
    val isCompleted = !moduloEstado.isBloqueado && (moduloEstado.licoesConcluidas == moduloEstado.totalLicoes) && moduloEstado.totalLicoes > 0
    val isLocked = moduloEstado.isBloqueado

    val colorFilter: ColorFilter? = when {
        isLocked -> ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
        isCompleted -> ColorFilter.colorMatrix(ColorMatrix().apply { setToScale(1.2f, 1.1f, 0.9f, 1f) })
        else -> null
    }

    // --- MUDANÇA AQUI: Seleção Hardcoded da Imagem ---
    // Certifique-se de ter as imagens: img_level_1, img_level_2, img_level_3, img_level_4 na pasta drawable
    val levelImageRes = when (moduloEstado.modulo.ordem) {
        1 -> R.drawable.ic_modulo1
        2 -> R.drawable.ic_modulo2
        3 -> R.drawable.ic_modulo3
        4 -> R.drawable.ic_modulo4
        else -> R.drawable.ic_modulo1 // Padrão se não encontrar
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(width = 130.dp, height = 130.dp)
            .offset(y = 20.dp)
    ) {
        Image(
            painter = painterResource(id = levelImageRes), // Usando a variável dinâmica
            contentDescription = "Nível",
            colorFilter = colorFilter,
            modifier = Modifier
                .requiredSize(128.dp)
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.Fit
        )
        StarsLayout(total = moduloEstado.totalLicoes, concluidas = moduloEstado.licoesConcluidas)
    }
}

// --- ATUALIZAÇÃO DA LÓGICA DE POSIÇÃO DAS ESTRELAS ---
@Composable
fun BoxScope.StarsLayout(total: Int, concluidas: Int) {

    // Define posições baseadas na quantidade TOTAL de estrelas para manter simetria
    val posicoes = when (total) {
        1 -> listOf(
            Pair(0, -68) // Centralizada no topo
        )
        2 -> listOf(
            Pair(-22, -62), // Esquerda, perto do topo
            Pair(22, -62)   // Direita, perto do topo
        )
        3 -> listOf(
            Pair(-40, -50), // Esquerda mais baixa
            Pair(0, -70),   // Centro topo (pico)
            Pair(40, -50)   // Direita mais baixa
        )
        else -> listOf( // 4 ou mais (Layout Original Mantido)
            Pair(-48, -42),
            Pair(-18, -66),
            Pair(18, -66),
            Pair(48, -42)
        )
    }

    // Garante que não tentamos desenhar mais estrelas do que posições definidas (segurança)
    val qtdParaMostrar = minOf(total, posicoes.size)

    for (i in 0 until qtdParaMostrar) {
        val numeroEstrela = (i + 1).toString()
        val isGold = i < concluidas
        val (x, y) = posicoes[i]

        StarImage(
            number = numeroEstrela,
            isGold = isGold,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = x.dp, y = y.dp)
        )
    }
}

@Composable
fun TextGroup(moduloEstado: ModuloEstado, isLeftAligned: Boolean, modifier: Modifier = Modifier) {
    val textAlign = if (isLeftAligned) TextAlign.Start else TextAlign.End
    val alignment = if (isLeftAligned) Alignment.Start else Alignment.End

    // Uso correto da cor TextLockedColor
    val tituloColor = if (moduloEstado.isBloqueado) TextLockedColor else BlueBanner
    val descColor = if (moduloEstado.isBloqueado) TextLockedColor else TextModuloColor

    Column(
        horizontalAlignment = alignment,
        modifier = modifier.padding(top = 55.dp)
    ) {
        Text(
            text = moduloEstado.modulo.nome,
            color = tituloColor,
            fontFamily = BalooFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = textAlign,
            lineHeight = 22.sp
        )
        Text(
            text = moduloEstado.modulo.descricao,
            modifier = Modifier.offset(y = (-10).dp),
            color = descColor,
            fontFamily = Baloo2FontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            textAlign = textAlign,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun StarImage(number: String, isGold: Boolean, modifier: Modifier = Modifier) {
    val imageRes = if (isGold) R.drawable.ic_star_gold else R.drawable.ic_star_grey
    Box(
        modifier = modifier.size(38.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        Text(
            text = number,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            fontFamily = BalooFontFamily,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun GameTopBar(moedas: Int, vidas: Int, xp: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusPill(
                icon = painterResource(id = R.drawable.ic_robot_face),
                value = xp.toString(),
                pillColor = Color(0xFF5FA8D3),
                iconSize = 52.dp,
                textPaddingStart = 44.dp,
                iconOffsetY = (-6).dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            StatusPill(
                icon = painterResource(id = R.drawable.ic_coin),
                value = moedas.toString(),
                pillColor = Color(0xFF5FA8D3),
                textPaddingStart = 40.dp
            )
        }
        HeartDisplay(lives = vidas)
    }
}

@Composable
fun HeartDisplay(lives: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) {
            val isRed = i > (5 - lives)
            val heartIcon = if (isRed) R.drawable.ic_heart else R.drawable.ic_heart_grey
            Image(
                painter = painterResource(id = heartIcon),
                contentDescription = "Vida",
                modifier = Modifier.size(24.dp).padding(2.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun StatusPill(icon: Painter, value: String, pillColor: Color, textPaddingStart: Dp, iconOffsetY: Dp = 0.dp, iconSize: Dp = 48.dp) {
    Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.height(iconSize)) {
        Box(
            modifier = Modifier.padding(start = 10.dp).height(34.dp).clip(RoundedCornerShape(50)).background(pillColor).padding(start = textPaddingStart, end = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp, fontFamily = BalooFontFamily)
        }
        Image(painter = icon, contentDescription = null, modifier = Modifier.size(iconSize).align(Alignment.CenterStart).offset(y = iconOffsetY), contentScale = ContentScale.Fit)
    }
}

@Composable
fun WorldBanner() {
    Column(modifier = Modifier.fillMaxWidth().padding(start = 28.dp, end = 28.dp, bottom = 8.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).clip(RoundedCornerShape(16.dp)).background(DarkBlueBanner).padding(bottom = 6.dp)) {
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(BlueBanner).padding(vertical = 16.dp, horizontal = 20.dp)) {
                Column {
                    Text(text = "MUNDO 1", color = Color.White.copy(alpha = 0.7f), fontFamily = BalooFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp)
                    Text(text = "Explorando o Valor do Dinheiro", color = Color.White, fontFamily = BalooFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 24.sp)
                }
            }
        }
        BubbleTail(color = BlueBanner, modifier = Modifier.padding(start = 95.dp).offset(y = (-9).dp))
    }
}

@Composable
fun BubbleTail(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(width = 45.dp, height = 25.dp)) {
        val path = Path().apply { moveTo(size.width, 0f); lineTo(0f, 0f); lineTo(0f, size.height); close() }
        drawPath(path, color = color)
    }
}