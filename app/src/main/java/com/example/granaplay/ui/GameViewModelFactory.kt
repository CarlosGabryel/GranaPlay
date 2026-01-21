package com.example.granaplay.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.granaplay.data.GameRepository
import com.example.granaplay.data.GameViewModel
import com.example.granaplay.ui.lesson.LessonViewModel
import com.example.granaplay.ui.profile.ProfileViewModel

/**
 * Factory unificada para instanciar ViewModels que dependem do [GameRepository].
 * Substitui a necessidade de frameworks complexos de injeção (como Hilt/Dagger)
 * para este escopo de projeto, centralizando a criação.
 */
class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 1. Lógica Principal do Jogo (Home)
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(repository) as T
        }

        // 2. Autenticação (Login/Cadastro)
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository) as T
        }

        // 3. Execução de Lições (Quiz)
        if (modelClass.isAssignableFrom(LessonViewModel::class.java)) {
            return LessonViewModel(repository) as T
        }

        // 4. Perfil do Usuário
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(repository) as T
        }

        throw IllegalArgumentException("Classe ViewModel desconhecida: ${modelClass.name}")
    }
}