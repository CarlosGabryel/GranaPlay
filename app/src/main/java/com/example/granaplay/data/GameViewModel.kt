package com.example.granaplay.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    // --- ESTADO DE LOADING ---
    private val _isLoading = MutableLiveData(true) // Começa carregando
    val isLoading: LiveData<Boolean> = _isLoading

    val todosModulos: Flow<List<Modulo>> = repository.todosModulos
    private val _estadoModulos = MutableLiveData<List<ModuloEstado>>()
    val estadoModulos: LiveData<List<ModuloEstado>> = _estadoModulos

    var usuarioAtual: LiveData<Usuario>? = null

    init {
        // Agora o carregamento é gerenciado aqui
        prepararApp()
    }

    private fun prepararApp() {
        viewModelScope.launch {
            _isLoading.value = true

            // 1. Popula o banco (Agora seguro contra duplicatas)
            repository.verificarEPopularBanco()

            // 2. Pequeno delay visual opcional (500ms) para não piscar muito rápido
            // delay(500)

            // 3. Libera a tela
            _isLoading.value = false
        }
    }

    fun carregarDadosUsuario(usuarioId: Long) {
        viewModelScope.launch {
            repository.verificarRecargaDeVidas(usuarioId)
        }
        if (usuarioAtual == null) {
            usuarioAtual = repository.getUsuarioEmTempoReal(usuarioId).asLiveData()
        }
        carregarModulos(usuarioId)
    }

    fun carregarModulos(usuarioId: Long) {
        viewModelScope.launch {
            repository.getModulosComEstado(usuarioId).collect { estados ->
                _estadoModulos.value = estados
            }
        }
    }

    fun getProximaLicao(usuarioId: Long, moduloId: Long, onResult: (Licao?) -> Unit) {
        viewModelScope.launch {
            val licao = repository.getProximaLicao(usuarioId, moduloId)
            onResult(licao)
        }
    }

    fun getLicoes(moduloId: Long): Flow<List<Licao>> {
        return repository.getLicoesPorModulo(moduloId)
    }
}

class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}