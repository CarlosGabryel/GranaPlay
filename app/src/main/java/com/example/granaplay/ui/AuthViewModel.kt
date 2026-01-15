package com.example.granaplay.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.granaplay.data.GameRepository
import com.example.granaplay.data.Usuario
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: GameRepository) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<Long?>() // Retorna o ID se sucesso
    val loginResult: LiveData<Long?> = _loginResult

    private val _cadastroSucesso = MutableLiveData<Boolean>()
    val cadastroSucesso: LiveData<Boolean> = _cadastroSucesso

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // true = Login, false = Cadastro
    private val _isLoginMode = MutableLiveData(true)
    val isLoginMode: LiveData<Boolean> = _isLoginMode

    // Verifica no banco se deve começar na tela de Login ou Cadastro
    fun verificarEstadoInicial() {
        viewModelScope.launch {
            val temUsuarios = repository.temUsuariosCadastrados()
            // Se tem usuários, vai para Login (true). Se não tem, vai para Cadastro (false)
            _isLoginMode.value = temUsuarios
        }
    }

    fun toggleMode() {
        _isLoginMode.value = !(_isLoginMode.value ?: true)
        _errorMessage.value = null
    }

    // Função auxiliar para mudar forçadamente para a tela de Login
    fun irParaLogin() {
        _isLoginMode.value = true
        _errorMessage.value = null
    }

    fun login(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) {
            _errorMessage.value = "Preencha todos os campos."
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
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

                val existente = repository.buscarUsuarioPorEmail(email)
                if (existente != null) {
                    _errorMessage.value = "Este email já está em uso."
                } else {
                    val novoUsuario = Usuario(nome = nome, email = email, senha = senha)
                    repository.cadastrarUsuario(novoUsuario)

                    // REQUISITO: Depois do cadastro manda pra tela de login
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

class AuthViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}