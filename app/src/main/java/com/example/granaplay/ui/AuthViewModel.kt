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

    // ========================================================================
    // ESTADOS (LIVE DATA)
    // ========================================================================

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    private val _cadastroResult = MutableLiveData<Boolean>()
    val cadastroResult: LiveData<Boolean> = _cadastroResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // ========================================================================
    // LÓGICA DE LOGIN
    // ========================================================================

    fun login(email: String, senha: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null // Limpa erros anteriores

                val usuario = repository.buscarUsuarioPorEmail(email)

                // NOTA DE SEGURANÇA: Em um app real, compare Hashes, nunca texto puro!
                if (usuario != null && usuario.senha == senha) {
                    // SUCESSO
                    // Dica: Aqui seria o lugar ideal para salvar o usuario.id no SharedPreferences
                    // Ex: sessionManager.saveUserId(usuario.id)
                    _loginResult.value = true
                } else {
                    // FALHA
                    _errorMessage.value = "Email ou senha incorretos."
                    _loginResult.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao fazer login: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ========================================================================
    // LÓGICA DE CADASTRO
    // ========================================================================

    fun cadastrar(nome: String, email: String, senha: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                // 1. Verifica duplicidade
                val existente = repository.buscarUsuarioPorEmail(email)

                if (existente != null) {
                    _errorMessage.value = "Este email já está cadastrado."
                    _cadastroResult.value = false
                } else {
                    // 2. Cria e Salva
                    val novoUsuario = Usuario(
                        nome = nome,
                        email = email,
                        senha = senha
                    )
                    repository.cadastrarUsuario(novoUsuario)

                    // 3. Opcional: Popular banco inicial se necessário
                    // repository.verificarEPopularBanco()

                    _cadastroResult.value = true
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao cadastrar: ${e.message}"
                _cadastroResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}

// ========================================================================
// FACTORY (Necessária para passar o Repository no construtor)
// ========================================================================

class AuthViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}