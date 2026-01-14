package com.example.granaplay.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.ViewCompositionStrategy
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
import kotlin.math.min

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
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val isLoading by viewModel.isLoading.observeAsState(true)

                if (isLoading) {
                    LoadingScreen()
                } else {
                    GameScreen(viewModel)
                }
            }
        }
    }
}

// ========================================================================
// ESTILOS E CORES
// ========================================================================

private object HomeStyles {
    val BackgroundColor = Color(0xFFDFF3FF)
    val BlueBanner = Color(0xFF258EB6)
    val DarkBlueBanner = Color(0xFF136F91)
    val TextModuloColor = Color(0xFF2C8CAE)
    val TextLockedColor = Color(0xFF8FAAB6)
    val PillColor = Color(0xFF5FA8D3)
    val LoadingText = Color(0xFF136F91)
}

// ========================================================================
// TELAS PRINCIPAIS
// ========================================================================

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeStyles.BackgroundColor),
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
                color = HomeStyles.BlueBanner,
                strokeWidth = 6.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Preparando o Jogo...",
                fontFamily = BalooFontFamily,
                fontSize = 20.sp,
                color = HomeStyles.LoadingText
            )
        }
    }
}

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val usuario by viewModel.usuarioAtual?.observeAsState() ?: androidx.compose.runtime.mutableStateOf(null)
    val modulos by viewModel.estadoModulos.observeAsState(emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeStyles.BackgroundColor)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Espaço para BottomNav
        ) {
            // 1. HUD (Topo)
            GameHUD(
                moedas = usuario?.moedas ?: 0,
                vidas = usuario?.pontosSaude ?: 5,
                xp = usuario?.xp ?: 0
            )

            // 2. Mapa (Centro/Fundo)
            // Usa weight(1f) para ocupar todo o espaço restante disponível
            GameMapArea(
                modulos = modulos,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

// ========================================================================
// MAPA E POSICIONAMENTO
// ========================================================================

@Composable
fun GameMapArea(
    modulos: List<ModuloEstado>,
    modifier: Modifier = Modifier
) {
    // IMPORTANTE: Mantivemos Box (sem Scroll) para respeitar o layout fixo original
    Box(modifier = modifier) {

        // A. Fundo (Estrada)
        Image(
            painter = painterResource(id = R.drawable.path),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .fillMaxHeight(0.85f)
                .align(Alignment.Center)
        )

        // B. Avatar (Robô)
        Image(
            painter = painterResource(id = R.drawable.ic_robot_body),
            contentDescription = "Avatar",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(160.dp)
                .padding(start = 22.dp)
                .align(Alignment.TopStart)
                .offset(y = (-53).dp)
        )

        // C. Módulos (Posicionamento Absoluto via Padding)
        modulos.forEachIndexed { index, moduloEstado ->
            val isLeftAligned = (index % 2 != 0)

            // Lógica original de cálculo de posição Y
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
                            // TODO: Navegar
                        }
                    }
                )
            }
        }

        // D. Seta Final (Próximo Mundo)
        // Posicionamento também calculado manualmente baseado na quantidade de módulos
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

@Composable
fun LevelItem(
    moduloEstado: ModuloEstado,
    isLeftAligned: Boolean,
    onClick: () -> Unit
) {
    val rowArrangement = if (isLeftAligned) Arrangement.Start else Arrangement.End

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = rowArrangement,
        modifier = Modifier
            .fillMaxWidth(1f) // Ocupa largura total para permitir alinhamento correto
            .clickable(enabled = !moduloEstado.isBloqueado) { onClick() }
    ) {
        if (isLeftAligned) {
            HexagonGroup(moduloEstado)
            Spacer(modifier = Modifier.width(0.dp))
            TextGroup(moduloEstado, isLeftAligned = true, modifier = Modifier.weight(1f))
        } else {
            TextGroup(moduloEstado, isLeftAligned = false, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(0.dp))
            HexagonGroup(moduloEstado)
        }
    }
}

// ========================================================================
// COMPONENTES VISUAIS (Hexágono, Texto, Estrelas)
// ========================================================================

@Composable
fun HexagonGroup(moduloEstado: ModuloEstado) {
    val isCompleted = !moduloEstado.isBloqueado && (moduloEstado.licoesConcluidas == moduloEstado.totalLicoes) && moduloEstado.totalLicoes > 0
    val isLocked = moduloEstado.isBloqueado

    val colorFilter: ColorFilter? = when {
        isLocked -> ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
        isCompleted -> ColorFilter.colorMatrix(ColorMatrix().apply { setToScale(1.2f, 1.1f, 0.9f, 1f) })
        else -> null
    }

    val levelImageRes = when (moduloEstado.modulo.ordem) {
        1 -> R.drawable.ic_modulo1
        2 -> R.drawable.ic_modulo2
        3 -> R.drawable.ic_modulo3
        4 -> R.drawable.ic_modulo4
        else -> R.drawable.ic_modulo1
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(width = 130.dp, height = 130.dp)
            .offset(y = 20.dp)
    ) {
        Image(
            painter = painterResource(id = levelImageRes),
            contentDescription = null,
            colorFilter = colorFilter,
            modifier = Modifier
                .requiredSize(128.dp)
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.Fit
        )
        StarsLayout(total = moduloEstado.totalLicoes, concluidas = moduloEstado.licoesConcluidas)
    }
}

@Composable
fun BoxScope.StarsLayout(total: Int, concluidas: Int) {
    val posicoes = when (total) {
        1 -> listOf(Pair(0, -68))
        2 -> listOf(Pair(-22, -62), Pair(22, -62))
        3 -> listOf(Pair(-40, -50), Pair(0, -70), Pair(40, -50))
        else -> listOf(Pair(-48, -42), Pair(-18, -66), Pair(18, -66), Pair(48, -42))
    }

    val qtdParaMostrar = minOf(total, posicoes.size)

    for (i in 0 until qtdParaMostrar) {
        val isGold = i < concluidas
        val (x, y) = posicoes[i]

        StarImage(
            number = (i + 1).toString(),
            isGold = isGold,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = x.dp, y = y.dp)
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
fun TextGroup(moduloEstado: ModuloEstado, isLeftAligned: Boolean, modifier: Modifier = Modifier) {
    val textAlign = if (isLeftAligned) TextAlign.Start else TextAlign.End
    val alignment = if (isLeftAligned) Alignment.Start else Alignment.End

    val tituloColor = if (moduloEstado.isBloqueado) HomeStyles.TextLockedColor else HomeStyles.BlueBanner
    val descColor = if (moduloEstado.isBloqueado) HomeStyles.TextLockedColor else HomeStyles.TextModuloColor

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

// ========================================================================
// HUD E BANNER
// ========================================================================

@Composable
fun GameHUD(moedas: Int, vidas: Int, xp: Int) {
    Column {
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
                    iconSize = 52.dp,
                    textPaddingStart = 44.dp,
                    iconOffsetY = (-6).dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                StatusPill(
                    icon = painterResource(id = R.drawable.ic_coin),
                    value = moedas.toString(),
                    textPaddingStart = 40.dp
                )
            }
            HeartDisplay(lives = vidas)
        }
        WorldBanner()
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
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .padding(2.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun StatusPill(icon: Painter, value: String, textPaddingStart: Dp, iconOffsetY: Dp = 0.dp, iconSize: Dp = 48.dp) {
    Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.height(iconSize)) {
        Box(
            modifier = Modifier
                .padding(start = 10.dp)
                .height(34.dp)
                .clip(RoundedCornerShape(50))
                .background(HomeStyles.PillColor)
                .padding(start = textPaddingStart, end = 14.dp),
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
        Box(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).clip(RoundedCornerShape(16.dp)).background(HomeStyles.DarkBlueBanner).padding(bottom = 6.dp)) {
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(HomeStyles.BlueBanner).padding(vertical = 16.dp, horizontal = 20.dp)) {
                Column {
                    Text(text = "MUNDO 1", color = Color.White.copy(alpha = 0.7f), fontFamily = BalooFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp)
                    Text(text = "Explorando o Valor do Dinheiro", color = Color.White, fontFamily = BalooFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 24.sp)
                }
            }
        }
        BubbleTail(color = HomeStyles.BlueBanner, modifier = Modifier.padding(start = 95.dp).offset(y = (-9).dp))
    }
}

@Composable
fun BubbleTail(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(width = 45.dp, height = 25.dp)) {
        val path = Path().apply { moveTo(size.width, 0f); lineTo(0f, 0f); lineTo(0f, size.height); close() }
        drawPath(path, color = color)
    }
}