package com.example.granaplay.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.granaplay.GranaPlayApplication
import com.example.granaplay.R
import com.example.granaplay.data.SessionManager
import com.example.granaplay.ui.AuthActivity
import com.example.granaplay.ui.GameViewModelFactory
import com.example.granaplay.ui.theme.BalooFontFamily

// ========================================================================
// 1. FRAGMENTO E CONFIGURAÇÃO
// ========================================================================

class ProfileFragment : Fragment() {

    // Inicialização do ViewModel usando a Factory global
    private val viewModel: ProfileViewModel by viewModels {
        GameViewModelFactory((requireActivity().application as GranaPlayApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sessionManager = SessionManager(requireContext())

        // Carrega dados iniciais baseados na sessão
        viewModel.carregarDados(sessionManager.getUserId())

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val usuario by viewModel.usuario.observeAsState()
                val licoesCompletas by viewModel.licoesCompletas.observeAsState(0)

                ProfileScreen(
                    usuarioNome = usuario?.nome ?: "Carregando...",
                    usuarioEmail = usuario?.email ?: "...",
                    vidas = usuario?.pontosSaude ?: 5,
                    xp = usuario?.xp ?: 0,
                    moedas = usuario?.moedas ?: 0,
                    licoesCompletas = licoesCompletas,
                    onLogout = {
                        // Lógica de Logout: Limpa sessão e reinicia pilha de atividades
                        sessionManager.logout()
                        val intent = Intent(requireContext(), AuthActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

// ========================================================================
// 2. DESIGN SYSTEM (CORES)
// ========================================================================

private object ProfileColors {
    val Background = Color(0xFFE1F5FE)
    val Title = Color(0xFF134E63)
    val LabelText = Color(0xFF2C8CAE)
    val ValueText = Color(0xFF134E63)

    // Botões
    val SettingsBg = Color(0xFF2B89A8)
    val SettingsShadow = Color(0xFF1A5F75)
    val LogoutBg = Color(0xFFFF0000)
    val LogoutShadow = Color(0xFFBA0000)

    // Painel de Estatísticas
    val StatItemBg = Color(0xFFD1EFFC)
    val StatRed = Color(0xFFFF0000)
}

// ========================================================================
// 3. TELA PRINCIPAL
// ========================================================================

@Composable
fun ProfileScreen(
    usuarioNome: String,
    usuarioEmail: String,
    vidas: Int,
    xp: Int,
    moedas: Int,
    licoesCompletas: Int,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ProfileColors.Background)
            .statusBarsPadding()
            .padding(bottom = 80.dp)
            .verticalScroll(rememberScrollState()), // Permite rolagem em telas pequenas
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Título
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Meu Perfil",
            fontFamily = BalooFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = ProfileColors.Title
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Avatar e Status Principais (Coração, XP, Moedas)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF81D4FA)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_avatar),
                    contentDescription = "Avatar",
                    modifier = Modifier.size(70.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(34.dp))

            // Lista lateral de Status
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MiniStatPill(iconRes = R.drawable.ic_heart, value = "$vidas/5", textColor = Color.Black)
                MiniStatPill(iconRes = R.drawable.ic_robot_face, value = xp.toString(), textColor = Color.Black)
                MiniStatPill(iconRes = R.drawable.ic_coin, value = moedas.toString(), textColor = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Campos de Informação (Somente Leitura)
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReadOnlyField(label = "NOME DE USUÁRIO:", value = usuarioNome)
            ReadOnlyField(label = "E-MAIL:", value = usuarioEmail)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Botões de Ação (Config e Sair)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Botão Configurações (Quadrado)
            ProfileButton3D(
                onClick = { /* TODO: Implementar Config */ },
                mainColor = ProfileColors.SettingsBg,
                shadowColor = ProfileColors.SettingsShadow,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_config),
                    contentDescription = "Configurações",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Botão Sair (Largo)
            ProfileButton3D(
                onClick = onLogout,
                mainColor = ProfileColors.LogoutBg,
                shadowColor = ProfileColors.LogoutShadow,
                modifier = Modifier
                    .height(60.dp)
                    .weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sair),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SAIR",
                        fontFamily = BalooFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 5. Painel Inferior de Estatísticas Detalhadas
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .background(ProfileColors.StatItemBg.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .border(2.dp, Color.White, RoundedCornerShape(16.dp))
        ) {
            StatRowItem(
                iconRes = R.drawable.ic_fire,
                label = "Sequência de Acesso",
                value = "1 dias",
                isRedValue = true
            )
            Divider()

            StatRowItem(
                iconRes = R.drawable.ic_book,
                label = "Lições Completas",
                value = licoesCompletas.toString(),
                isRedValue = true
            )
            Divider()

            StatRowItem(
                iconRes = R.drawable.ic_grana,
                label = "Total de Granas Ganhas",
                value = moedas.toString(),
                isRedValue = true
            )
            Divider()

            StatRowItem(
                iconRes = R.drawable.ic_predio,
                label = "Construções na Cidade",
                value = "0",
                isRedValue = true
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

// ========================================================================
// 4. COMPONENTES REUTILIZÁVEIS
// ========================================================================

/**
 * Pílula pequena para exibir status (Vidas, XP, Moedas) ao lado do avatar.
 */
@Composable
fun MiniStatPill(iconRes: Int, value: String, textColor: Color) {
    val totalHeight = 50.dp
    val pillHeight = 34.dp
    val iconSize = 44.dp

    Box(
        modifier = Modifier
            .width(140.dp)
            .height(totalHeight)
    ) {
        // Fundo Branco
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(pillHeight)
                .clip(RoundedCornerShape(50))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                textAlign = TextAlign.Center,
                fontFamily = BalooFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = textColor
            )
        }

        // Ícone Flutuante (offset para sair da caixa)
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(iconSize)
                .offset((-10).dp, (5).dp),
            contentScale = ContentScale.Fit
        )
    }
}

/**
 * Campo de texto somente leitura com rótulo e valor.
 */
@Composable
fun ReadOnlyField(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE1F5FE))
            .background(Color.White.copy(alpha = 0.6f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontFamily = BalooFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = ProfileColors.LabelText
        )
        Text(
            text = value,
            fontFamily = BalooFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = ProfileColors.ValueText
        )
    }
}

/**
 * Botão genérico com efeito 3D (Sombra e profundidade).
 */
@Composable
fun ProfileButton3D(
    onClick: () -> Unit,
    mainColor: Color,
    shadowColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val cornerRadius = 12.dp
    val shadowHeight = 5.dp

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val verticalOffset = if (isPressed) shadowHeight / 2 else 0.dp

    Box(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    ) {
        // Camada Sombra
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = verticalOffset)
                .clip(RoundedCornerShape(cornerRadius))
                .background(shadowColor)
        )
        // Camada Frente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = shadowHeight - verticalOffset)
                .clip(RoundedCornerShape(cornerRadius))
                .background(mainColor),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

/**
 * Item de linha para a tabela de estatísticas inferior.
 */
@Composable
fun StatRowItem(
    iconRes: Int,
    label: String,
    value: String,
    isRedValue: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            fontFamily = BalooFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = ProfileColors.ValueText,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            fontFamily = BalooFontFamily,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = if (isRedValue) ProfileColors.StatRed else ProfileColors.ValueText
        )
    }
}

@Composable
fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White)
    )
}