package com.example.granaplay.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "licoes",
    foreignKeys = [
        ForeignKey(
            entity = Modulo::class,
            parentColumns = ["id"],
            childColumns = ["idModulo"],
            onDelete = ForeignKey.CASCADE // Se apagar o módulo, apaga as lições
        )
    ]
)
data class Licao(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val descricao: String,
    val pontuacao: Int,
    val ordem: Int, // Para controlar visualmente quem vem primeiro na trilha
    val idModulo: Long // Chave estrangeira
)
