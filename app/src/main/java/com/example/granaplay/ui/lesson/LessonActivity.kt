package com.example.granaplay.ui.lesson

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.granaplay.R
import com.example.granaplay.data.Alternativa
import com.example.granaplay.data.AppDatabase
import com.example.granaplay.data.GameRepository
import com.example.granaplay.data.Questao
import com.example.granaplay.ui.theme.Baloo2FontFamily
import com.example.granaplay.ui.theme.BalooFontFamily

class LessonActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val moduloId = intent.getLongExtra("MODULO_ID", -1)
        val repository = GameRepository(AppDatabase.getDatabase(this).gameDao())
        val factory = LessonViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[LessonViewModel::class.java]

        viewModel.carregarLicao(usuarioId = 1L, moduloId = moduloId)

        setContent {
            MaterialTheme {
                LessonScreen(viewModel = viewModel, onClose = { finish() })
            }
        }
    }
}

// ==========================================
// CORES DA LIÇÃO
// ==========================================
private object LessonColors {
    val Background = Color(0xFFE1F5FE)
    val TitleText = Color(0xFF01579B)
    val QuestionText = Color(0xFF4E4141)

    // Botões
    val AltUnselectedBorder = Color(0xFF848484)
    val AltUnselectedShadow = Color(0xFF848484)
    val AltUnselectedText = Color(0xFF848484)
    val AltUnselectedBg = Color(0xFFE1F5FE)

    val AltSelectedBorder = Color(0xFF006386)
    val AltSelectedShadow = Color(0xFF006386)
    val AltSelectedText = Color(0xFF006386)
    val AltSelectedBg = Color(0xFFE1F5FE)

    val VerifyEnabledBg = Color(0xFFFFCC29)
    val VerifyEnabledBorder = Color(0xFFDBA906)
    val VerifyEnabledShadow = Color(0xFFDBA906)
    val VerifyEnabledText = Color(0xFF006386)

    val VerifyDisabledBg = Color(0xFFA29C9C)
    val VerifyDisabledBorder = Color(0xFF848484)
    val VerifyDisabledShadow = Color(0xFF848484)
    val VerifyDisabledText = Color(0xFF585656)

    val ProgressBarTrack = Color(0xFF006386)
    val ProgressBarFill = Color(0xFF1CEE00)

    // Feedback
    val SuccessBg = Color(0xFFD7FFB8)
    val SuccessText = Color(0xFF58A700)
    val SuccessShadow = Color(0xFF428000)

    val ErrorBg = Color(0xFFFFDFE0)
    val ErrorText = Color(0xFFEA2B2B)

    val ErrorBtnBg = Color(0xFFFF0000)
    val ErrorBtnShadow = Color(0xFFBA0000)
    val ErrorBtnText = Color.White

    // Tela de Conclusão
    val BannerBlue = Color(0xFF258EB6)
    val XpCardBorder = Color(0xFF9E9E9E)
    val XpCardShadow = Color(0xFF757575)
    val XpCardText = Color(0xFF757575)

    val CoinCardBorder = Color(0xFFFF9800)
    val CoinCardShadow = Color(0xFFF57C00)
    val CoinCardText = Color(0xFFF57C00)
}

@Composable
fun LessonScreen(viewModel: LessonViewModel, onClose: () -> Unit) {
    val uiState by viewModel.uiState.observeAsState(LessonUiState.Loading)
    val feedbackState by viewModel.feedbackState.observeAsState(FeedbackState.Hidden)

    Box(modifier = Modifier.fillMaxSize().background(LessonColors.Background)) {
        when (val state = uiState) {
            is LessonUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LessonColors.ProgressBarTrack)
                }
            }
            // NOVO: Tela de Conclusão
            is LessonUiState.Completed -> {
                LessonCompletedScreen(
                    xp = state.xpGanho,
                    moedas = state.moedasGanhas,
                    onClose = onClose
                )
            }
            is LessonUiState.Success -> {
                LessonContent(
                    questao = state.questaoAtual,
                    alternativas = state.alternativas,
                    progressoAtual = state.indiceQuestao,
                    totalQuestoes = state.totalQuestoes,
                    vidasAtuais = state.vidasAtuais,
                    onClose = onClose,
                    onVerificar = { idSelecionado ->
                        viewModel.verificarResposta(idSelecionado)
                    }
                )

                FeedbackSheet(
                    feedbackState = feedbackState,
                    onContinue = {
                        if (feedbackState is FeedbackState.GameOver) {
                            onClose()
                        } else {
                            viewModel.avancar()
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
            is LessonUiState.Error -> {}
        }
    }
}

// ==========================================
// TELA DE CONCLUSÃO
// ==========================================
@Composable
fun LessonCompletedScreen(xp: Int, moedas: Int, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // Balão Azul
        Box(contentAlignment = Alignment.BottomCenter) {
            Box(
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LessonColors.BannerBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "PARABÉNS!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontFamily = BalooFontFamily,
                    letterSpacing = 1.sp
                )
            }
            // Triângulo do balão
            Canvas(modifier = Modifier.size(width = 30.dp, height = 15.dp).offset(x = (-40).dp)) {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height)
                    close()
                }
                drawPath(path, color = LessonColors.BannerBlue)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Robô
        Image(
            painter = painterResource(id = R.drawable.ic_robot_body),
            contentDescription = "Robô Feliz",
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Você aprendeu e conquistou\nbastante com essa lição",
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontFamily = Baloo2FontFamily,
            color = Color.Black,
            lineHeight = 26.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Cards de XP e Moedas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatCard(
                iconRes = R.drawable.ic_robot_face, // Ícone de XP/Rosto
                value = "$xp XP",
                borderColor = LessonColors.XpCardBorder,
                shadowColor = LessonColors.XpCardShadow,
                textColor = LessonColors.XpCardText,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(24.dp))

            StatCard(
                iconRes = R.drawable.ic_one_coin, // Ícone de moeda
                value = "$ $moedas",
                borderColor = LessonColors.CoinCardBorder,
                shadowColor = LessonColors.CoinCardShadow,
                textColor = LessonColors.CoinCardText,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        GameButton3D(
            text = "VOLTAR PARA O MAPA",
            onClick = onClose, // Fecha a activity
            mainColor = LessonColors.VerifyEnabledBg,
            shadowColor = LessonColors.VerifyEnabledShadow,
            borderColor = LessonColors.VerifyEnabledBorder,
            textColor = LessonColors.VerifyEnabledText,
            height = 65.dp,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun StatCard(
    iconRes: Int,
    value: String,
    borderColor: Color,
    shadowColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val cornerRadius = 12.dp
    val shadowHeight = 4.dp
    val height = 110.dp

    Box(modifier = modifier.height(height)) {
        // Sombra
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(cornerRadius))
                .background(shadowColor)
        )
        // Frente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = shadowHeight)
                .border(2.dp, borderColor, RoundedCornerShape(cornerRadius))
                .clip(RoundedCornerShape(cornerRadius))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontFamily = BalooFontFamily
                )
            }
        }
    }
}

// ... (Mantenha LessonContent, FeedbackSheet e componentes auxiliares como estavam) ...

@Composable
fun LessonContent(
    questao: Questao,
    alternativas: List<Alternativa>,
    progressoAtual: Int,
    totalQuestoes: Int,
    vidasAtuais: Int,
    onClose: () -> Unit,
    onVerificar: (Long) -> Unit
) {
    var selectedOptionId by remember(questao.id) { mutableStateOf<Long?>(null) }
    val context = LocalContext.current

    val preparedAlternativas = remember(alternativas) {
        alternativas.map { alt ->
            val resId = getDrawableId(context, alt.imagemSource)
            Pair(alt, resId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 16.dp)
            .statusBarsPadding()
    ) {
        HeaderSection(
            progresso = progressoAtual.toFloat() / totalQuestoes.toFloat(),
            vidas = vidasAtuais,
            onClose = onClose
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Leia e responda:",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = LessonColors.TitleText,
                fontFamily = BalooFontFamily
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.ic_sound),
                contentDescription = "Ouvir",
                modifier = Modifier.size(48.dp).clickable { }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = questao.enunciado,
            fontSize = 22.sp,
            color = LessonColors.QuestionText,
            lineHeight = 32.sp,
            fontFamily = Baloo2FontFamily,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Justify
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (questao.tipo == "IMAGE_4") {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(preparedAlternativas) { (alternativa, imageResId) ->
                    val isSelected = (selectedOptionId == alternativa.id)
                    val (main, shadow, border) = getButtonColors(isSelected)

                    GameImageButton3D(
                        imageResId = imageResId,
                        mainColor = main,
                        shadowColor = shadow,
                        borderColor = border,
                        onClick = { selectedOptionId = alternativa.id },
                        height = 140.dp
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                alternativas.forEach { alternativa ->
                    val isSelected = (selectedOptionId == alternativa.id)
                    val (main, shadow, border, text) = getButtonColorsText(isSelected)

                    GameButton3D(
                        text = alternativa.texto,
                        mainColor = main,
                        shadowColor = shadow,
                        borderColor = border,
                        textColor = text,
                        onClick = { selectedOptionId = alternativa.id },
                        height = 80.dp,
                        fontSize = 20.sp
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        val isVerifyEnabled = selectedOptionId != null
        val (vMain, vShadow, vBorder, vText) = if (isVerifyEnabled) {
            listOf(LessonColors.VerifyEnabledBg, LessonColors.VerifyEnabledShadow, LessonColors.VerifyEnabledBorder, LessonColors.VerifyEnabledText)
        } else {
            listOf(LessonColors.VerifyDisabledBg, LessonColors.VerifyDisabledShadow, LessonColors.VerifyDisabledBorder, LessonColors.VerifyDisabledText)
        }

        GameButton3D(
            text = "VERIFICAR",
            onClick = { selectedOptionId?.let { onVerificar(it) } },
            enabled = isVerifyEnabled,
            mainColor = vMain,
            shadowColor = vShadow,
            borderColor = vBorder,
            textColor = vText,
            height = 80.dp,
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun FeedbackSheet(
    feedbackState: FeedbackState,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isVisible = feedbackState !is FeedbackState.Hidden

    val backgroundColor = when (feedbackState) {
        is FeedbackState.Correct -> LessonColors.SuccessBg
        is FeedbackState.Incorrect -> LessonColors.ErrorBg
        is FeedbackState.GameOver -> LessonColors.ErrorBg
        else -> Color.Transparent
    }

    val titleColor = when (feedbackState) {
        is FeedbackState.Correct -> LessonColors.SuccessText
        is FeedbackState.Incorrect -> LessonColors.ErrorText
        is FeedbackState.GameOver -> LessonColors.ErrorBtnShadow
        else -> Color.Black
    }

    val titleText = when (feedbackState) {
        is FeedbackState.Correct -> "Muito bem!"
        is FeedbackState.Incorrect -> "Que pena!"
        is FeedbackState.GameOver -> "Acabaram as vidas!"
        else -> ""
    }

    val message = when (feedbackState) {
        is FeedbackState.Correct -> feedbackState.message
        is FeedbackState.Incorrect -> feedbackState.message
        is FeedbackState.GameOver -> "Você precisa descansar."
        else -> ""
    }

    val btnText = if (feedbackState is FeedbackState.Incorrect) "ENTENDI" else "CONTINUAR"

    val (btnMain, btnShadow, btnTextCol) = when(feedbackState) {
        is FeedbackState.Correct -> listOf(LessonColors.SuccessText, LessonColors.SuccessShadow, Color.White)
        is FeedbackState.Incorrect, is FeedbackState.GameOver -> listOf(LessonColors.ErrorBtnBg, LessonColors.ErrorBtnShadow, LessonColors.ErrorBtnText)
        else -> listOf(Color.Gray, Color.DarkGray, Color.White)
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(32.dp)
        ) {
            Text(
                text = titleText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                fontFamily = BalooFontFamily
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontSize = 18.sp,
                color = titleColor,
                fontFamily = Baloo2FontFamily
            )
            Spacer(modifier = Modifier.height(24.dp))

            GameButton3D(
                text = btnText,
                onClick = onContinue,
                mainColor = btnMain,
                shadowColor = btnShadow,
                borderColor = btnShadow,
                textColor = btnTextCol,
                height = 80.dp,
                fontSize = 20.sp
            )
        }
    }
}

fun getDrawableId(context: Context, name: String?): Int {
    if (name.isNullOrEmpty()) return R.drawable.ic_close
    val id = context.resources.getIdentifier(name, "drawable", context.packageName)
    return if (id != 0) id else R.drawable.ic_close
}

@Composable
fun getButtonColors(isSelected: Boolean): List<Color> {
    return if (isSelected) {
        listOf(LessonColors.AltSelectedBg, LessonColors.AltSelectedShadow, LessonColors.AltSelectedBorder)
    } else {
        listOf(LessonColors.AltUnselectedBg, LessonColors.AltUnselectedShadow, LessonColors.AltUnselectedBorder)
    }
}

@Composable
fun getButtonColorsText(isSelected: Boolean): List<Color> {
    return if (isSelected) {
        listOf(LessonColors.AltSelectedBg, LessonColors.AltSelectedShadow, LessonColors.AltSelectedBorder, LessonColors.AltSelectedText)
    } else {
        listOf(LessonColors.AltUnselectedBg, LessonColors.AltUnselectedShadow, LessonColors.AltUnselectedBorder, LessonColors.AltUnselectedText)
    }
}

@Composable
fun GameButton3D(
    text: String,
    onClick: () -> Unit,
    mainColor: Color,
    shadowColor: Color,
    borderColor: Color,
    textColor: Color,
    enabled: Boolean = true,
    height: Dp = 70.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 18.sp
) {
    val cornerRadius = 10.dp
    val shadowHeight = 5.dp
    val borderWidth = 2.dp

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val verticalOffset = if (isPressed && enabled) shadowHeight / 2 else 0.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clickable(interactionSource = interactionSource, indication = null, enabled = enabled, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = verticalOffset)
                .clip(RoundedCornerShape(cornerRadius))
                .background(shadowColor)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = shadowHeight - verticalOffset)
                .border(borderWidth, borderColor, RoundedCornerShape(cornerRadius))
                .clip(RoundedCornerShape(cornerRadius))
                .background(mainColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontFamily = BalooFontFamily,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun GameImageButton3D(
    imageResId: Int,
    onClick: () -> Unit,
    mainColor: Color,
    shadowColor: Color,
    borderColor: Color,
    height: Dp
) {
    val cornerRadius = 16.dp
    val shadowHeight = 5.dp
    val borderWidth = 2.dp

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val verticalOffset = if (isPressed) shadowHeight / 2 else 0.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = verticalOffset)
                .clip(RoundedCornerShape(cornerRadius))
                .background(shadowColor)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = shadowHeight - verticalOffset)
                .border(borderWidth, borderColor, RoundedCornerShape(cornerRadius))
                .clip(RoundedCornerShape(cornerRadius))
                .background(mainColor),
            contentAlignment = Alignment.Center
        ) {
            if (imageResId != 0) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun HeaderSection(progresso: Float, vidas: Int, onClose: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = "Fechar",
            modifier = Modifier
                .size(32.dp)
                .clickable { onClose() }
        )

        Spacer(modifier = Modifier.width(12.dp))

        LinearProgressIndicator(
            progress = progresso,
            modifier = Modifier
                .weight(1f)
                .height(16.dp)
                .clip(RoundedCornerShape(50)),
            color = LessonColors.ProgressBarFill,
            trackColor = LessonColors.ProgressBarTrack
        )

        Spacer(modifier = Modifier.width(16.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_heart),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = vidas.toString(),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red,
            fontFamily = BalooFontFamily
        )
    }
}