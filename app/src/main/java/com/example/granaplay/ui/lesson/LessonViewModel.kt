package com.example.granaplay.ui.lesson

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.granaplay.data.Alternativa
import com.example.granaplay.data.GameRepository
import com.example.granaplay.data.Licao
import com.example.granaplay.data.Questao
import kotlinx.coroutines.launch
import kotlin.math.max

sealed class FeedbackState {
    object Hidden : FeedbackState()
    data class Correct(val message: String) : FeedbackState()
    data class Incorrect(val message: String) : FeedbackState()
    object GameOver : FeedbackState()
}

sealed class LessonUiState {
    object Loading : LessonUiState()
    data class Success(
        val licao: Licao,
        val questaoAtual: Questao,
        val alternativas: List<Alternativa>,
        val indiceQuestao: Int,
        val totalQuestoes: Int,
        val vidasAtuais: Int
    ) : LessonUiState()

    data class Completed(val xpGanho: Int, val moedasGanhas: Int) : LessonUiState()
    object Error : LessonUiState()
}

class LessonViewModel(private val repository: GameRepository) : ViewModel() {

    private val _uiState = MutableLiveData<LessonUiState>(LessonUiState.Loading)
    val uiState: LiveData<LessonUiState> = _uiState

    private val _feedbackState = MutableLiveData<FeedbackState>(FeedbackState.Hidden)
    val feedbackState: LiveData<FeedbackState> = _feedbackState

    private var questoesDaLicao: List<Questao> = emptyList()
    private var indiceAtual = 0
    private var vidasAtuais = 5

    private var currentUsuarioId: Long = 0
    private var currentLicaoId: Long = 0

    private val MOEDAS_POR_QUESTAO = 5
    private val XP_POR_QUESTAO = 2.0
    private val PENALIDADE_MOEDA_POR_ERRO = 2
    private val PENALIDADE_XP_POR_ERRO = 0.5

    private var errosCometidos = 0

    fun carregarLicao(usuarioId: Long, moduloId: Long) {
        this.currentUsuarioId = usuarioId

        viewModelScope.launch {
            _uiState.value = LessonUiState.Loading

            // Busca o usuário correto pelo ID
            val usuario = repository.buscarUsuarioPorId(usuarioId)
            vidasAtuais = usuario?.pontosSaude ?: 5

            val licao = repository.getProximaLicao(usuarioId, moduloId)

            if (licao != null) {
                this@LessonViewModel.currentLicaoId = licao.id
                questoesDaLicao = repository.getQuestoesPorLicao(licao.id)

                if (questoesDaLicao.isNotEmpty()) {
                    indiceAtual = 0
                    carregarQuestaoAtual(licao)
                } else {
                    _uiState.value = LessonUiState.Error
                }
            } else {
                _uiState.value = LessonUiState.Completed(0, 0)
            }
        }
    }

    private fun carregarQuestaoAtual(licao: Licao) {
        viewModelScope.launch {
            val questao = questoesDaLicao[indiceAtual]
            val alternativas = repository.getAlternativasPorQuestao(questao.id)

            _uiState.value = LessonUiState.Success(
                licao = licao,
                questaoAtual = questao,
                alternativas = alternativas,
                indiceQuestao = indiceAtual + 1,
                totalQuestoes = questoesDaLicao.size,
                vidasAtuais = vidasAtuais
            )
            _feedbackState.value = FeedbackState.Hidden
        }
    }

    fun verificarResposta(alternativaId: Long) {
        val state = _uiState.value
        if (state is LessonUiState.Success) {
            val alternativa = state.alternativas.find { it.id == alternativaId } ?: return

            if (alternativa.isCorreta) {
                _feedbackState.value = FeedbackState.Correct(state.questaoAtual.feedbackAcerto)
            } else {
                errosCometidos++
                perderVida() // Chama a função que agora salva no banco

                if (vidasAtuais > 0) {
                    _feedbackState.value = FeedbackState.Incorrect(state.questaoAtual.feedbackErro)
                } else {
                    _feedbackState.value = FeedbackState.GameOver
                }
            }
        }
    }

    private fun perderVida() {
        if (vidasAtuais > 0) {
            vidasAtuais--

            // 1. Atualiza a UI imediatamente
            val state = _uiState.value
            if (state is LessonUiState.Success) {
                _uiState.value = state.copy(vidasAtuais = vidasAtuais)
            }

            // 2. Salva no banco imediatamente a cada erro
            viewModelScope.launch {
                if (currentUsuarioId != 0L) {
                    repository.descontarVida(currentUsuarioId, vidasAtuais)
                }
            }
        }
    }

    fun avancar() {
        val state = _uiState.value
        val feedback = _feedbackState.value

        if (state is LessonUiState.Success) {
            when (feedback) {
                is FeedbackState.Correct -> {
                    if (indiceAtual < questoesDaLicao.size - 1) {
                        indiceAtual++
                        carregarQuestaoAtual(state.licao)
                    } else {
                        finalizarLicao()
                    }
                }
                is FeedbackState.Incorrect -> {
                    _feedbackState.value = FeedbackState.Hidden
                }
                is FeedbackState.GameOver -> {
                    _feedbackState.value = FeedbackState.Hidden
                }
                else -> {}
            }
        }
    }

    private fun finalizarLicao() {
        val totalQuestoes = questoesDaLicao.size

        val baseMoedas = totalQuestoes * MOEDAS_POR_QUESTAO
        val baseXp = totalQuestoes * XP_POR_QUESTAO

        val descontoMoedas = errosCometidos * PENALIDADE_MOEDA_POR_ERRO
        val descontoXp = errosCometidos * PENALIDADE_XP_POR_ERRO

        val saldoFinalMoedas = max(0, baseMoedas - descontoMoedas)
        val saldoFinalXp = max(0.0, baseXp - descontoXp).toInt()

        viewModelScope.launch {
            if (currentUsuarioId != 0L && currentLicaoId != 0L) {
                repository.concluirLicao(
                    usuarioId = currentUsuarioId,
                    licaoId = currentLicaoId,
                    xpGanho = saldoFinalXp,
                    moedasGanhas = saldoFinalMoedas,
                    vidasRestantes = vidasAtuais
                )
            }

            _uiState.value = LessonUiState.Completed(
                xpGanho = saldoFinalXp,
                moedasGanhas = saldoFinalMoedas
            )
        }
    }
}

class LessonViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LessonViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}