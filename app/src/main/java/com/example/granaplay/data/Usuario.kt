package com.example.granaplay.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade que representa o perfil do jogador.
 * Armazena credenciais e o estado atual da economia do jogo (Moedas, XP, Vidas).
 */
@Entity(
    tableName = "usuarios",
    // Índice único no e-mail: Garante integridade e acelera a busca no Login
    indices = [Index(value = ["email"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // --- Dados de Acesso ---
    val nome: String,
    val email: String,
    val senha: String, // TODO: Em produção, armazenar apenas o Hash (ex: SHA-256)

    // --- Gamificação ---
    val moedas: Int = 0,
    val xp: Int = 0,

    @ColumnInfo(name = "pontos_saude")
    val pontosSaude: Int = 5,

    /**
     * Timestamp (System.currentTimeMillis) de quando a vida foi reduzida.
     * Usado para calcular a regeneração automática (Cooldown).
     * Null se o usuário nunca perdeu vida ou já recuperou tudo.
     */
    @ColumnInfo(name = "tempo_ultima_vida_perdida")
    val tempoUltimaVidaPerdida: Long? = null
)