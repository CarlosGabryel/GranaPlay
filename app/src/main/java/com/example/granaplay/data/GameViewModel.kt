package com.example.granaplay.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    // ========================================================================
    // ESTADOS (State)
    // ========================================================================

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _estadoModulos = MutableLiveData<List<ModuloEstado>>()
    val estadoModulos: LiveData<List<ModuloEstado>> = _estadoModulos

    // Variável que guardará o LiveData do usuário após o login/identificação
    var usuarioAtual: LiveData<Usuario>? = null

    // ========================================================================
    // INICIALIZAÇÃO
    // ========================================================================

    init {
        inicializarAplicacao()
    }

    private fun inicializarAplicacao() {
        viewModelScope.launch {
            _isLoading.value = true

            // Popula o banco com os módulos/lições iniciais se necessário
            repository.verificarEPopularBanco()

            _isLoading.value = false
        }
    }

    // ========================================================================
    // LÓGICA DO USUÁRIO
    // ========================================================================

    fun carregarDadosUsuario(usuarioId: Long) {
        viewModelScope.launch {
            // Verifica regras de negócio (recarga de vidas) antes de expor os dados
            repository.verificarRecargaDeVidas(usuarioId)
        }

        // Configura o observável do usuário se ainda não estiver configurado
        if (usuarioAtual == null) {
            usuarioAtual = repository.getUsuarioEmTempoReal(usuarioId).asLiveData()
        }

        // Inicia o monitoramento do progresso dos módulos para este usuário
        monitorarProgressoModulos(usuarioId)
    }

    private fun monitorarProgressoModulos(usuarioId: Long) {
        viewModelScope.launch {
            // Coleta o Flow do repositório e atualiza o LiveData da UI
            repository.getModulosComEstado(usuarioId).collect { estados ->
                _estadoModulos.value = estados
            }
        }
    }

    // ========================================================================
    // NAVEGAÇÃO E CONTEÚDO
    // ========================================================================

    fun getProximaLicao(usuarioId: Long, moduloId: Long, onResult: (Licao?) -> Unit) {
        viewModelScope.launch {
            val licao = repository.getProximaLicao(usuarioId, moduloId)
            onResult(licao)
        }
    }

    // Retorna Flow diretamente para ser consumido pela UI ou convertido
    fun getLicoes(moduloId: Long): Flow<List<Licao>> {
        return repository.getLicoesPorModulo(moduloId)
    }
}

// ========================================================================
// FACTORY
// ========================================================================

class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}