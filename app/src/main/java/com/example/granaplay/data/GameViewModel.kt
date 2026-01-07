package com.example.granaplay.data

import androidx.lifecycle.LiveData // <--- ESTAVA FALTANDO ESSE IMPORT
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    // Dados crus (apenas a lista do banco, sem saber se está bloqueado ou não)
    val todosModulos: Flow<List<Modulo>> = repository.todosModulos

    // Dados processados (COM a lógica de estrelas e cadeados)
    private val _estadoModulos = MutableLiveData<List<ModuloEstado>>()
    val estadoModulos: LiveData<List<ModuloEstado>> = _estadoModulos

    init {
        // ASSIM QUE O VIEWMODEL NASCE, ELE TENTA POPULAR O BANCO
        inicializarDados()
    }

    private fun inicializarDados() {
        viewModelScope.launch {
            repository.verificarEPopularBanco()
        }
    }

    // Função para buscar lições de um módulo específico
    fun getLicoes(moduloId: Long): Flow<List<Licao>> {
        return repository.getLicoesPorModulo(moduloId)
    }

    // Chama essa função na sua Activity/Fragment passando o ID do usuário logado
    fun carregarModulos(usuarioId: Long) {
        viewModelScope.launch {
            // O repositório faz o cálculo de quem está bloqueado
            val estados = repository.getModulosComEstado(usuarioId)
            // O ViewModel atualiza a tela
            _estadoModulos.value = estados
        }
    }

    // Novo método útil para o clique no botão do módulo
    fun getProximaLicao(usuarioId: Long, moduloId: Long, onResult: (Licao?) -> Unit) {
        viewModelScope.launch {
            val licao = repository.getProximaLicao(usuarioId, moduloId)
            onResult(licao)
        }
    }
}

// Fábrica para criar o ViewModel (Necessário porque ele recebe parâmetros)
class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}