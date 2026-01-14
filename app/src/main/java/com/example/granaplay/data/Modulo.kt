package com.example.granaplay.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "modulos",
    // Garante que não existam dois módulos com o mesmo nome e acelera a busca
    indices = [Index(value = ["nome"], unique = true)]
)
data class Modulo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nome: String,

    val descricao: String,

    val ordem: Int // Define a sequência visual na trilha
)