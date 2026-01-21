package com.example.granaplay.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Gerencia o estado da UI para a tela principal (Home) e lógica geral do jogo.
 * Responsável por inicializar o banco e conectar os fluxos de dados do Repositório.
 */
class GameViewModel(private val repository: GameRepository) : ViewModel() {

    // ========================================================================
    // ESTADOS DA UI
    // ========================================================================

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    // Lista de módulos com seus estados (bloqueado/desbloqueado/progresso)
    private val _estadoModulos = MutableLiveData<List<ModuloEstado>>()
    val estadoModulos: LiveData<List<ModuloEstado>> = _estadoModulos

    // ========================================================================
    // INICIALIZAÇÃO
    // ========================================================================

    init {
        // Garante que o banco tenha dados (seed) ao iniciar o ViewModel
        viewModelScope.launch {
            repository.verificarEPopularBanco()
            _isLoading.value = false
        }
    }

    // ========================================================================
    // GESTÃO DO USUÁRIO
    // ========================================================================

    /**
     * Inicia o monitoramento dos dados do usuário e do progresso dos módulos.
     * Deve ser chamado assim que o ID do usuário logado estiver disponível.
     */
    fun iniciarSessaoUsuario(usuarioId: Long): LiveData<Usuario> {
        // 1. Verifica regras de negócio (Recarga de vidas por tempo)
        viewModelScope.launch {
            repository.verificarRecargaDeVidas(usuarioId)
        }

        // 2. Inicia monitoramento dos módulos para este usuário
        viewModelScope.launch {
            repository.getModulosComEstado(usuarioId).collect { estados ->
                _estadoModulos.value = estados
            }
        }

        // 3. Retorna o LiveData do usuário em tempo real
        return repository.getUsuarioEmTempoReal(usuarioId).asLiveData()
    }

    // ========================================================================
    // NAVEGAÇÃO E LÓGICA
    // ========================================================================

    /**
     * Busca a próxima lição pendente para um módulo específico.
     * Usado para o botão "Continuar" ou ao clicar num módulo.
     */
    fun buscarProximaLicao(usuarioId: Long, moduloId: Long, onResult: (Licao?) -> Unit) {
        viewModelScope.launch {
            val licao = repository.getProximaLicao(usuarioId, moduloId)
            onResult(licao)
        }
    }

    /**
     * Retorna o fluxo de lições para a tela de detalhes do módulo.
     */
    fun getLicoesDoModulo(moduloId: Long): Flow<List<Licao>> {
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
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}