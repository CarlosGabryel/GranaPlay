package com.example.granaplay.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    // Garante e-mail único e acelera muito o login
    indices = [Index(value = ["email"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nome: String,

    val email: String,

    val senha: String, // Em produção, idealmente armazenar o hash (ex: SHA-256)

    val moedas: Int = 0,

    @ColumnInfo(name = "pontos_saude")
    val pontosSaude: Int = 5,

    val xp: Int = 0,

    @ColumnInfo(name = "tempo_ultima_vida_perdida")
    val tempoUltimaVidaPerdida: Long? = null
)