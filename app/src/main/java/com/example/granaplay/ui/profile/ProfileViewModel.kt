package com.example.granaplay.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.granaplay.data.GameRepository
import com.example.granaplay.data.Usuario
import kotlinx.coroutines.launch

/**
 * ViewModel responsável por gerenciar os dados da tela de Perfil.
 * Observa o usuário e suas estatísticas em tempo real.
 */
class ProfileViewModel(private val repository: GameRepository) : ViewModel() {

    // ========================================================================
    // ESTADOS (LiveData)
    // ========================================================================

    private val _usuario = MutableLiveData<Usuario>()
    val usuario: LiveData<Usuario> = _usuario

    private val _licoesCompletas = MutableLiveData(0)
    val licoesCompletas: LiveData<Int> = _licoesCompletas

    // ========================================================================
    // LÓGICA DE NEGÓCIO
    // ========================================================================

    /**
     * Inicia a observação dos fluxos de dados do repositório.
     * Atualiza a UI automaticamente sempre que o banco de dados mudar.
     */
    fun carregarDados(userId: Long) {
        // 1. Observa dados cadastrais e gamificação (Moedas, XP, Vidas)
        viewModelScope.launch {
            repository.getUsuarioEmTempoReal(userId).collect { user ->
                _usuario.value = user
            }
        }

        // 2. Observa estatísticas de progresso (Lições feitas)
        viewModelScope.launch {
            repository.contarLicoesConcluidas(userId).collect { count ->
                _licoesCompletas.value = count
            }
        }
    }
}