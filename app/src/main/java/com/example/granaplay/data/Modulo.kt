package com.example.granaplay.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "modulos")
data class Modulo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val descricao: String
)
