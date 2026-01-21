package com.example.granaplay.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa uma pergunta dentro de uma [Licao].
 *
 * @property tipo Define o layout da questão. Deve corresponder aos valores
 * do enum [TipoQuestao] (ex: "TEXT_2", "IMAGE_4").
 */
@Entity(
    tableName = "questoes",
    foreignKeys = [
        ForeignKey(
            entity = Licao::class,
            parentColumns = ["id"],
            childColumns = ["licao_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    // Índice essencial para buscar todas as questões de uma lição rapidamente
    indices = [Index(value = ["licao_id"])]
)
data class Questao(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "licao_id")
    val licaoId: Long,

    val enunciado: String,

    // Armazena o nome do enum (ex: "TEXT_2") para definir o layout na UI
    val tipo: String,

    @ColumnInfo(name = "feedback_acerto")
    val feedbackAcerto: String,

    @ColumnInfo(name = "feedback_erro")
    val feedbackErro: String
)