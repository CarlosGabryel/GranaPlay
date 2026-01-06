package com.example.granaplay.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.granaplay.data.GameRepository
import com.example.granaplay.data.Usuario
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: GameRepository) : ViewModel() {

    // LiveData para observar o resultado do Login/Cadastro na Activity
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    private val _cadastroResult = MutableLiveData<Boolean>()
    val cadastroResult: LiveData<Boolean> = _cadastroResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Função de Login
    fun login(email: String, senha: String) {
        viewModelScope.launch {
            val usuario = repository.buscarUsuarioPorEmail(email)
            if (usuario != null && usuario.senha == senha) {
                // Login Sucesso
                // Dica: Aqui você poderia salvar o ID do usuário em SharedPreferences para manter logado
                _loginResult.value = true
            } else {
                _errorMessage.value = "Email ou senha incorretos."
                _loginResult.value = false
            }
        }
    }

    // Função de Cadastro
    fun cadastrar(nome: String, email: String, senha: String) {
        viewModelScope.launch {
            // Verifica se já existe
            val existente = repository.buscarUsuarioPorEmail(email)
            if (existente != null) {
                _errorMessage.value = "Este email já está cadastrado."
                _cadastroResult.value = false
            } else {
                val novoUsuario = Usuario(nome = nome, email = email, senha = senha)
                repository.cadastrarUsuario(novoUsuario)

                // Opcional: Popular dados iniciais do jogo (modulos/lições) se for o primeiro user
                // repository.popularBancoInicial()

                _cadastroResult.value = true
            }
        }
    }
}
