package com.example.granaplay.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.granaplay.MainActivity
import com.example.granaplay.R
import com.example.granaplay.data.SessionManager
import kotlinx.coroutines.delay

// ========================================================================
// 1. CONFIGURAÇÕES
// ========================================================================

private object SplashConfig {
    val BackgroundColor = Color(0xFFB4E0FB)
    const val DELAY_MS = 3000L
    val LogoSize = 250.dp
}

// ========================================================================
// 2. ACTIVITY (LÓGICA DE NAVEGAÇÃO)
// ========================================================================

/**
 * Tela de entrada do aplicativo.
 * Responsabilidades:
 * 1. Exibir a marca (Branding).
 * 2. Verificar se o usuário já está logado.
 * 3. Redirecionar para Login ou Home com animação suave.
 */
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura tela cheia (Edge-to-Edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // Renderiza a Interface
            SplashScreenUI()

            // Executa a lógica de navegação após o tempo de espera
            LaunchedEffect(Unit) {
                delay(SplashConfig.DELAY_MS)
                verificarSessaoENavegar()
            }
        }
    }

    private fun verificarSessaoENavegar() {
        val session = SessionManager(applicationContext)

        val intent = if (session.isLogado()) {
            // Fluxo: Usuário já autenticado -> Vai para o Jogo
            Intent(this, MainActivity::class.java)
        } else {
            // Fluxo: Primeiro acesso ou deslogado -> Vai para Autenticação
            Intent(this, AuthActivity::class.java)
        }

        startActivity(intent)

        // Aplica animação de dissolver (Cross-fade) para uma transição suave
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        // Remove a Splash da pilha para que o botão "Voltar" não retorne a ela
        finish()
    }
}

// ========================================================================
// 3. INTERFACE DE USUÁRIO (UI)
// ========================================================================

@Composable
fun SplashScreenUI() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashConfig.BackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo_auth),
            contentDescription = "Logo GranaPlay",
            modifier = Modifier.size(SplashConfig.LogoSize),
            contentScale = ContentScale.Fit
        )
    }
}