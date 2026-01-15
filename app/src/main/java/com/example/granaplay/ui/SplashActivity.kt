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

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura tela cheia
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFB4E0FB)), // Cor do fundo solicitada
                contentAlignment = Alignment.Center
            ) {
                // Logo Centralizada
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_auth), // Sua logo
                    contentDescription = "Logo GranaPlay",
                    modifier = Modifier.size(250.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // Lógica de espera (3 segundos) e navegação com ANIMAÇÃO
            LaunchedEffect(Unit) {
                delay(3000) // 3 segundos

                val session = SessionManager(applicationContext)

                val intent = if (session.isLogado()) {
                    // Usuário já logado -> Vai para o Jogo
                    Intent(this@SplashActivity, MainActivity::class.java)
                } else {
                    // Não logado -> Vai para Autenticação
                    Intent(this@SplashActivity, AuthActivity::class.java)
                }

                startActivity(intent)

                // --- ANIMAÇÃO DE DISSOLVER (CROSS-FADE) ---
                // O primeiro parâmetro é a animação de entrada da nova tela (fade_in)
                // O segundo parâmetro é a animação de saída da Splash (fade_out)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

                finish()
            }
        }
    }
}