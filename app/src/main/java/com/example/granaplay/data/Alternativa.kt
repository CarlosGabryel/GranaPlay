package com.example.granaplay.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa uma opção de resposta associada a uma [Questao].
 *
 * Configurada com [ForeignKey.CASCADE]: Se a Questão pai for deletada,
 * todas as alternativas associadas serão removidas automaticamente.
 */
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
    // Índice essencial para performance de queries com JOIN e verificação de FK
    indices = [Index(value = ["questao_id"])]
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
    val imagemSource: String? = null
)