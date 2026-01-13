package com.example.granaplay.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "licoes",
    foreignKeys = [
        ForeignKey(
            entity = Modulo::class,
            parentColumns = ["id"],
            childColumns = ["modulo_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    // Cria um índice para a chave estrangeira (Melhora performance de busca e deletação)
    indices = [Index(value = ["modulo_id"])]
)
data class Licao(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nome: String,

    val descricao: String,

    val pontuacao: Int,

    val ordem: Int, // Define a sequência visual na trilha

    @ColumnInfo(name = "modulo_id")
    val moduloId: Long
)