package com.example.granaplay.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alternativas",
    foreignKeys = [
        ForeignKey(
            entity = Questao::class,
            parentColumns = ["id"],
            childColumns = ["questao_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["questao_id"])] // Otimização para chaves estrangeiras
)
data class Alternativa(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "questao_id")
    val questaoId: Long,

    val texto: String,

    @ColumnInfo(name = "is_correta")
    val isCorreta: Boolean,

    @ColumnInfo(name = "imagem_src")
    val imagemSource: String? = null // Caminho ou nome do resource
)