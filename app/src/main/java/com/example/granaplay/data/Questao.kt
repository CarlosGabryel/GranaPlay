package com.example.granaplay.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    // Otimização essencial para evitar Full Table Scan ao buscar questões de uma lição
    indices = [Index(value = ["licao_id"])]
)
data class Questao(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "licao_id")
    val licaoId: Long,

    val enunciado: String,

    @ColumnInfo(name = "feedback_acerto")
    val feedbackAcerto: String,

    @ColumnInfo(name = "feedback_erro")
    val feedbackErro: String,

    // Define o layout da questão (Ex: "TEXT_2" ou "IMAGE_4")
    val tipo: String = "TEXT_2"
)