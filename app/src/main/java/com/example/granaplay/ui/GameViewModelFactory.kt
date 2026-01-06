package com.example.granaplay.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.granaplay.data.GameRepository

class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Se a classe pedida for AuthViewModel, retorna uma instância dela
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }

        // Se futuramente tiver HomeViewModel, adicione outro IF aqui

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
