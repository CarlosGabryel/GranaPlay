package com.example.granaplay.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val email: String,
    val senha: String,
    val moedas: Int = 0,
    val pontosSaude: Int = 5
)
