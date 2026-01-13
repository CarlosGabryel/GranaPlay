package com.example.granaplay.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.granaplay.GranaPlayApplication
import com.example.granaplay.MainActivity
import com.example.granaplay.data.GameRepository
import com.example.granaplay.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    // Inicializa o ViewModel usando a Factory correta
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory((application as GranaPlayApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge() // Visual moderno (igual à MainActivity)
        setupListeners()
        setupObservers()
    }

    // ========================================================================
    // CONFIGURAÇÃO DE UI E SISTEMA
    // ========================================================================

    private fun setupEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        // Ícones escuros na barra de status (pois o fundo da tela de login geralmente é claro)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
    }

    // ========================================================================
    // LISTENERS (BOTÕES)
    // ========================================================================

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            handleLogin()
        }

        binding.btnCadastrar.setOnClickListener {
            handleCadastro()
        }
    }

    private fun handleLogin() {
        val email = binding.etEmail.text.toString()
        val senha = binding.etSenha.text.toString()

        if (validarCampos(email, senha)) {
            viewModel.login(email, senha)
        } else {
            exibirMensagem("Preencha email e senha")
        }
    }

    private fun handleCadastro() {
        val nome = binding.etNome.text.toString()
        val email = binding.etEmail.text.toString()
        val senha = binding.etSenha.text.toString()

        if (validarCampos(nome, email, senha)) {
            viewModel.cadastrar(nome, email, senha)
        } else {
            exibirMensagem("Preencha todos os campos para cadastrar")
        }
    }

    /**
     * Valida se uma lista de strings não está vazia.
     * Retorna true se todos os campos tiverem texto.
     */
    private fun validarCampos(vararg campos: String): Boolean {
        return campos.all { it.isNotBlank() }
    }

    // ========================================================================
    // OBSERVERS (RESPOSTAS DO VIEWMODEL)
    // ========================================================================

    private fun setupObservers() {
        // Sucesso no Login -> Vai para o Jogo
        viewModel.loginResult.observe(this) { sucesso ->
            if (sucesso) {
                irParaMainActivity()
            }
        }

        // Sucesso no Cadastro -> Feedback visual
        viewModel.cadastroResult.observe(this) { sucesso ->
            if (sucesso) {
                exibirMensagem("Cadastro realizado! Faça login.")
                limparCamposCadastro()
            }
        }

        // Erros -> Toast
        viewModel.errorMessage.observe(this) { erro ->
            erro?.let { exibirMensagem(it) }
        }
    }

    // ========================================================================
    // NAVEGAÇÃO E UTILITÁRIOS
    // ========================================================================

    private fun irParaMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        // Limpa a pilha para que o usuário não volte ao login ao apertar "Voltar"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun limparCamposCadastro() {
        binding.etNome.text.clear()
        // Opcional: limpar senha também
        binding.etSenha.text.clear()
    }

    private fun exibirMensagem(mensagem: String) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
    }
}