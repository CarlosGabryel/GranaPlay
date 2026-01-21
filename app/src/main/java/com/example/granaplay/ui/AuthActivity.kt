package com.example.granaplay.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.granaplay.GranaPlayApplication
import com.example.granaplay.MainActivity
import com.example.granaplay.R
import com.example.granaplay.data.SessionManager
import com.example.granaplay.ui.theme.Baloo2FontFamily
import com.example.granaplay.ui.theme.BalooFontFamily

// ========================================================================
// 1. ACTIVITY E CONFIGURAÇÃO
// ========================================================================

class AuthActivity : ComponentActivity() {

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory((application as GranaPlayApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define se a tela abre em modo Login ou Cadastro (baseado em lógica prévia)
        viewModel.verificarEstadoInicial()

        // Configura tela cheia (transparente atrás da status bar)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        setContent {
            AuthScreen(
                viewModel = viewModel,
                onSuccess = { userId ->
                    // Persiste a sessão e navega para a Home
                    SessionManager(this).criarSessao(userId)

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            )
        }
    }
}

// ========================================================================
// 2. DESIGN SYSTEM (CORES)
// ========================================================================

private object AuthColors {
    val Background = Color(0xFFBCE6FA)

    // Inputs
    val InputBackground = Color(0xFFE1F5FE)
    val InputLabel = Color(0xFF2C8CAE)
    val InputText = Color(0xFF134E63)

    // Textos
    val Title = Color(0xFF134E63)
    val Subtitle = Color(0xFF2C8CAE)

    // Botões
    val BtnMain = Color(0xFF2B89A8)
    val BtnShadow = Color(0xFF1A5F75)
    val BtnBorder = Color(0xFF1A5F75)
}

// ========================================================================
// 3. TELA PRINCIPAL (CONTROLLER)
// ========================================================================

@Composable
fun AuthScreen(viewModel: AuthViewModel, onSuccess: (Long) -> Unit) {
    // Observação de Estados do ViewModel
    val isLoginMode by viewModel.isLoginMode.observeAsState(true)
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState(null)

    val loginResult by viewModel.loginResult.observeAsState(null)
    val cadastroSucesso by viewModel.cadastroSucesso.observeAsState(false)

    val context = LocalContext.current

    // Efeitos Colaterais (Navegação e Toasts)
    LaunchedEffect(loginResult) {
        loginResult?.let { userId -> onSuccess(userId) }
    }

    LaunchedEffect(cadastroSucesso) {
        if (cadastroSucesso) {
            Toast.makeText(context, "Cadastro realizado! Faça login.", Toast.LENGTH_SHORT).show()
            viewModel.irParaLogin()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthColors.Background)
            .statusBarsPadding()
            .imePadding() // Ajusta layout quando teclado abre
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Logo (Robô)
            Image(
                painter = painterResource(id = R.drawable.ic_logo_auth),
                contentDescription = "Logo",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Cabeçalho (Título e Subtítulo dinâmicos)
            Text(
                text = if (isLoginMode) "Log in" else "Cadastro",
                fontFamily = BalooFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = AuthColors.Title,
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                text = if (isLoginMode) "Faça login para continuar" else "Por favor, preencha os campos abaixo",
                fontFamily = Baloo2FontFamily,
                fontSize = 18.sp,
                color = AuthColors.Subtitle,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Alternância de Formulários
            if (isLoginMode) {
                LoginForm(viewModel, isLoading)
            } else {
                RegisterForm(viewModel, isLoading)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Rodapé (Link para trocar entre Login/Cadastro)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isLoginMode) "Anda não tem conta? " else "Já tem uma conta? ",
                    fontFamily = Baloo2FontFamily,
                    color = AuthColors.Subtitle,
                    fontSize = 16.sp
                )
                Text(
                    text = if (isLoginMode) "Cadastre-se" else "Faça Log in",
                    fontFamily = BalooFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = AuthColors.Title,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { viewModel.toggleMode() }
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
        }

        // Loading Overlay
        if (isLoading) {
            CircularProgressIndicator(
                color = AuthColors.BtnMain,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

// ========================================================================
// 4. FORMULÁRIOS
// ========================================================================

@Composable
fun LoginForm(viewModel: AuthViewModel, isLoading: Boolean) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AuthInput(
            label = "E-MAIL:",
            value = email,
            onValueChange = { email = it },
            keyboardType = KeyboardType.Email
        )
        AuthInput(
            label = "SENHA:",
            value = senha,
            onValueChange = { senha = it },
            isPassword = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        AuthButton3D(
            text = "ENTRAR",
            onClick = { viewModel.login(email, senha) },
            enabled = !isLoading
        )
    }
}

@Composable
fun RegisterForm(viewModel: AuthViewModel, isLoading: Boolean) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AuthInput(
            label = "NOME DE USUÁRIO:",
            value = nome,
            onValueChange = { nome = it }
        )
        AuthInput(
            label = "E-MAIL:",
            value = email,
            onValueChange = { email = it },
            keyboardType = KeyboardType.Email
        )
        AuthInput(
            label = "SENHA:",
            value = senha,
            onValueChange = { senha = it },
            isPassword = true
        )
        AuthInput(
            label = "CONFIRMAR SENHA:",
            value = confirmarSenha,
            onValueChange = { confirmarSenha = it },
            isPassword = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        AuthButton3D(
            text = "CADASTRAR",
            onClick = { viewModel.cadastrar(nome, email, senha, confirmarSenha) },
            enabled = !isLoading
        )
    }
}

// ========================================================================
// 5. COMPONENTES REUTILIZÁVEIS
// ========================================================================

/**
 * Campo de texto estilizado com suporte a visibilidade de senha.
 */
@Composable
fun AuthInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AuthColors.InputBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            color = AuthColors.InputLabel,
            fontFamily = BalooFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    color = AuthColors.InputText,
                    fontFamily = BalooFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                ),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                visualTransformation = if (isPassword && !passwordVisible)
                    PasswordVisualTransformation() else VisualTransformation.None,
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            // Ícone de Olho para senha
            if (isPassword) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_eye_open),
                    contentDescription = "Toggle Password",
                    tint = AuthColors.InputLabel,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { passwordVisible = !passwordVisible }
                )
            }
        }
    }
}

/**
 * Botão com efeito 3D (Sombra e movimentação ao clicar).
 */
@Composable
fun AuthButton3D(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    height: Dp = 60.dp
) {
    val cornerRadius = 12.dp
    val shadowHeight = 5.dp
    val mainColor = AuthColors.BtnMain
    val shadowColor = AuthColors.BtnShadow

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Calcula o deslocamento vertical para simular o "apertar"
    val verticalOffset = if (isPressed && enabled) shadowHeight / 2 else 0.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        // Camada de Sombra (Fundo)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = verticalOffset)
                .clip(RoundedCornerShape(cornerRadius))
                .background(shadowColor)
        )
        // Camada Principal (Frente)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = shadowHeight - verticalOffset)
                .border(2.dp, AuthColors.BtnBorder, RoundedCornerShape(cornerRadius))
                .clip(RoundedCornerShape(cornerRadius))
                .background(mainColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontFamily = BalooFontFamily,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp
            )
        }
    }
}