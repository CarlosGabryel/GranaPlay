package com.example.granaplay.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.example.granaplay.R
import com.example.granaplay.ui.theme.Baloo2FontFamily
import com.example.granaplay.ui.theme.BalooFontFamily

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
                        // 1. Topo (Fixo)
                        GameTopBar()

                        // 2. Banner (Fixo)
                        WorldBanner()

                        // 3. Área do Jogo (Flexível)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            // --- CAMADA 1: O Caminho (Estrada) ---
                            Image(
                                painter = painterResource(id = R.drawable.path),
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .fillMaxWidth(0.75f)
                                    .fillMaxHeight(0.85f)
                                    .align(Alignment.Center)
                                    .padding(start = 0.dp, bottom = 0.dp)
                            )

                            // --- CAMADA 2: O Robô (Topo Esquerda) ---
                            Image(
                                painter = painterResource(id = R.drawable.ic_robot_body),
                                contentDescription = "Robô",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .height(160.dp)
                                    .padding(start = 22.dp)
                                    .align(Alignment.TopStart)
                                    .offset(y = (-50).dp)
                            )

                            // --- CAMADA 3: OS NÍVEIS (Zigue-Zague) ---

                            // NÍVEL 1 (Direita)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 30.dp, end = 18.dp)
                            ) {
                                LevelItem(isLeftAligned = false)
                            }

                            // NÍVEL 2 (Esquerda)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(top = 150.dp, start = 18.dp)
                            ) {
                                LevelItem(isLeftAligned = true)
                            }

                            // NÍVEL 3 (Direita)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 270.dp, end = 18.dp)
                            ) {
                                LevelItem(isLeftAligned = false)
                            }

                            // NÍVEL 4 (Esquerda)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(top = 390.dp, start = 18.dp)
                            ) {
                                LevelItem(isLeftAligned = true)
                            }

                            // --- CAMADA 4: SETA FINAL (NOVO) ---
                            // Posicionada no canto inferior direito
                            Image(
                                painter = painterResource(id = R.drawable.ic_world_ending),
                                contentDescription = "Próximo Mundo",
                                modifier = Modifier
                                    .size(200.dp) // Tamanho grande conforme protótipo
                                    .align(Alignment.BottomEnd) // Canto inferior direito
                                    .offset(y = (38).dp, x = (18).dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- CORES ---
val BlueBanner = Color(0xFF258EB6)
val DarkBlueBanner = Color(0xFF136F91)
val TextModuloColor = Color(0xFF2C8CAE)


// --- COMPONENTE DE NÍVEL GENÉRICO ---

@Composable
fun LevelItem(isLeftAligned: Boolean) {
    val alignment = if (isLeftAligned) Arrangement.Start else Arrangement.End

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = alignment,
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        if (isLeftAligned) {
            HexagonGroup()
            TextGroup(isLeftAligned = true, paddingStart = 5.dp, paddingEnd = 0.dp)
        } else {
            TextGroup(isLeftAligned = false, paddingStart = 0.dp, paddingEnd = 5.dp)
            HexagonGroup()
        }
    }
}

@Composable
fun HexagonGroup() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(width = 120.dp, height = 120.dp)
            .offset(y = 20.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_level_1),
            contentDescription = "Nível",
            modifier = Modifier
                .size(128.dp)
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.Fit
        )

        StarImage("1", true, Modifier.align(Alignment.Center).offset(x = (-48).dp, y = (-42).dp))
        StarImage("2", true, Modifier.align(Alignment.Center).offset(x = (-18).dp, y = (-66).dp))
        StarImage("3", false, Modifier.align(Alignment.Center).offset(x = 18.dp, y = (-66).dp))
        StarImage("4", false, Modifier.align(Alignment.Center).offset(x = 48.dp, y = (-42).dp))
    }
}

@Composable
fun TextGroup(isLeftAligned: Boolean, paddingStart: Dp, paddingEnd: Dp) {
    val textAlign = if (isLeftAligned) TextAlign.Start else TextAlign.End
    val alignment = if (isLeftAligned) Alignment.Start else Alignment.End

    Column(
        horizontalAlignment = alignment,
        modifier = Modifier.padding(start = paddingStart, end = paddingEnd, top = 70.dp)
    ) {
        Text(
            text = "Conhecendo o Dinheiro",
            color = BlueBanner,
            fontFamily = BalooFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = textAlign,
            lineHeight = 22.sp
        )
        Text(
            text = "Primeiros passos com o dinheiro",
            modifier = Modifier.offset(y = (-7).dp),
            color = TextModuloColor,
            fontFamily = Baloo2FontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 17.5.sp,
            textAlign = textAlign,
            lineHeight = 14.sp
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

// --- COMPONENTES ANTERIORES (Mantidos) ---

@Composable
fun WorldBanner() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 28.dp, end = 28.dp, bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkBlueBanner)
                .padding(bottom = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BlueBanner)
                    .padding(vertical = 16.dp, horizontal = 20.dp)
            ) {
                Column {
                    Text(
                        text = "MUNDO 1",
                        color = Color.White.copy(alpha = 0.7f),
                        fontFamily = BalooFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Explorando o Valor do Dinheiro",
                        color = Color.White,
                        fontFamily = BalooFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        lineHeight = 24.sp
                    )
                }
            }
        }
        BubbleTail(
            color = BlueBanner,
            modifier = Modifier
                .padding(start = 95.dp)
                .offset(y = (-9).dp)
        )
    }
}

@Composable
fun BubbleTail(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(width = 45.dp, height = 25.dp)) {
        val path = Path().apply {
            moveTo(size.width, 0f)
            lineTo(0f, 0f)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path, color = color)
    }
}

@Composable
fun GameTopBar() {
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
                value = "50",
                pillColor = Color(0xFF5FA8D3),
                iconSize = 52.dp,
                textPaddingStart = 44.dp,
                iconOffsetY = (-6).dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            StatusPill(
                icon = painterResource(id = R.drawable.ic_coin),
                value = "3000",
                pillColor = Color(0xFF5FA8D3),
                textPaddingStart = 40.dp
            )
        }
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
    iconSize: Dp = 48.dp
) {
    Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.height(iconSize)) {
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
                fontSize = 17.sp,
                fontFamily = BalooFontFamily
            )
        }
        Image(
            painter = icon,
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
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
                modifier = Modifier.size(24.dp).padding(2.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}