package com.example.granaplay.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.granaplay.data.GameRepository
import com.example.granaplay.data.GameViewModel

/**
 * Factory compartilhada para criar ViewModels que dependem do GameRepository.
 * Isso evita ter que criar uma Factory separada para cada tela.
 */
class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // CASO 1: GameViewModel (Usado na MainActivity / HomeFragment)
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }

        // CASO 2: AuthViewModel (Usado na AuthActivity)
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }

        // Adicione outros ViewModels aqui com 'if' se necessário...

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}