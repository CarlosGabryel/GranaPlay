package com.example.granaplay

import android.app.Application
import com.example.granaplay.data.AppDatabase
import com.example.granaplay.data.GameRepository

/**
 * Classe principal da Aplicação.
 * Atua como um Container de Dependências (Service Locator), garantindo que
 * o Banco de Dados e o Repositório sejam Singletons (existam apenas uma vez na memória).
 */
class GranaPlayApplication : Application() {

    // ========================================================================
    // INJEÇÃO DE DEPENDÊNCIA MANUAL
    // ========================================================================

    // 1. Instância única do Banco de Dados
    // O uso de 'by lazy' garante que o banco só seja aberto quando for realmente necessário,
    // melhorando o tempo de inicialização do app (startup time).
    val database by lazy {
        AppDatabase.getDatabase(this)
    }

    // 2. Instância única do Repositório
    // O repositório depende do DAO, que é extraído da instância do database acima.
    val repository by lazy {
        GameRepository(database.gameDao())
    }
}