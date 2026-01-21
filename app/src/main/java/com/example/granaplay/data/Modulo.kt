package com.example.granaplay.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa um capítulo ou fase na trilha de aprendizado.
 * Agrupa várias [Licao].
 */
@Entity(
    tableName = "modulos",
    // Garante unicidade de nomes e acelera buscas por título (útil no Seeding)
    indices = [Index(value = ["nome"], unique = true)]
)
data class Modulo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nome: String,
    val descricao: String,

    // Define a sequência em que o módulo aparece na tela (1º, 2º, etc.)
    val ordem: Int
)