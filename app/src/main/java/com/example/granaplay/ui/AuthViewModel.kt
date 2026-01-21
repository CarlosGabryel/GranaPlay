package com.example.granaplay.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.granaplay.data.GameRepository
import com.example.granaplay.data.Usuario
import kotlinx.coroutines.launch

/**
 * Gerencia a lógica de autenticação (Login e Cadastro).
 * Interage com o repositório para validar credenciais e criar novos usuários.
 */
class AuthViewModel(private val repository: GameRepository) : ViewModel() {

    // ========================================================================
    // ESTADOS DA UI (LiveData)
    // ========================================================================

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Retorna o ID do usuário em caso de sucesso no Login (Navegação para Home)
    private val _loginResult = MutableLiveData<Long?>()
    val loginResult: LiveData<Long?> = _loginResult

    // Sinaliza que o cadastro foi concluído (Navegação para aba de Login)
    private val _cadastroSucesso = MutableLiveData<Boolean>()
    val cadastroSucesso: LiveData<Boolean> = _cadastroSucesso

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Controla qual formulário exibir: true = Login, false = Cadastro
    private val _isLoginMode = MutableLiveData(true)
    val isLoginMode: LiveData<Boolean> = _isLoginMode

    // ========================================================================
    // CONTROLE DE NAVEGAÇÃO E MODO
    // ========================================================================

    /**
     * Verifica no banco de dados se existem usuários cadastrados.
     * Se não houver ninguém (primeiro uso), inicia diretamente na tela de Cadastro.
     */
    fun verificarEstadoInicial() {
        viewModelScope.launch {
            val temUsuarios = repository.temUsuariosCadastrados()
            _isLoginMode.value = temUsuarios
        }
    }

    /**
     * Alterna entre os modos de Login e Cadastro.
     */
    fun toggleMode() {
        _isLoginMode.value = !(_isLoginMode.value ?: true)
        _errorMessage.value = null
    }

    /**
     * Força a transição para a tela de Login (usado após um cadastro bem-sucedido).
     */
    fun irParaLogin() {
        _isLoginMode.value = true
        _errorMessage.value = null
    }

    // ========================================================================
    // AÇÕES DE AUTENTICAÇÃO
    // ========================================================================

    fun login(email: String, senha: String) {
        // 1. Validação Básica
        if (email.isBlank() || senha.isBlank()) {
            _errorMessage.value = "Preencha todos os campos."
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                // 2. Busca e Verificação
                val usuario = repository.buscarUsuarioPorEmail(email)

                if (usuario != null && usuario.senha == senha) {
                    _loginResult.value = usuario.id
                } else {
                    _errorMessage.value = "Email ou senha incorretos."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cadastrar(nome: String, email: String, senha: String, confirmarSenha: String) {
        // 1. Validação dos Campos
        if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
            _errorMessage.value = "Preencha todos os campos."
            return
        }
        if (senha != confirmarSenha) {
            _errorMessage.value = "As senhas não coincidem."
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                // 2. Verifica duplicidade
                val existente = repository.buscarUsuarioPorEmail(email)
                if (existente != null) {
                    _errorMessage.value = "Este email já está em uso."
                } else {
                    // 3. Cria novo usuário
                    val novoUsuario = Usuario(nome = nome, email = email, senha = senha)
                    repository.cadastrarUsuario(novoUsuario)

                    _cadastroSucesso.value = true
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao cadastrar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

// ========================================================================
// FACTORY
// ========================================================================

class AuthViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}