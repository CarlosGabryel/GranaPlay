package com.example.granaplay

import android.app.Application
import com.example.granaplay.data.AppDatabase
import com.example.granaplay.data.GameRepository

/**
 * Ponto de entrada global da aplicação.
 *
 * Atua como um Container de Dependências (Service Locator), garantindo que
 * objetos pesados e compartilhados (Banco de Dados e Repositório) sejam
 * instanciados apenas uma vez (Singletons) durante o ciclo de vida do App.
 */
class GranaPlayApplication : Application() {

    // ========================================================================
    // INJEÇÃO DE DEPENDÊNCIA MANUAL
    // ========================================================================

    /**
     * Instância única do Banco de Dados.
     * O uso de 'by lazy' garante que o banco só seja criado no primeiro acesso,
     * melhorando o tempo de inicialização (startup time).
     */
    val database by lazy {
        AppDatabase.getDatabase(this)
    }

    /**
     * Instância única do Repositório.
     * Recebe o DAO do banco de dados e centraliza a lógica de dados para as ViewModels.
     */
    val repository by lazy {
        GameRepository(database.gameDao())
    }
}