package com.example.granaplay.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "questoes",
    foreignKeys = [
        ForeignKey(
            entity = Licao::class,
            parentColumns = ["id"],
            childColumns = ["licaoId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Questao(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val licaoId: Long,          // Vincula a questão à Lição
    val enunciado: String,      // O texto da pergunta [cite: 116]
    val feedbackAcerto: String, // Mensagem de "Parabéns" [cite: 120]
    val feedbackErro: String,    // Mensagem explicativa do erro [cite: 126]
    val tipo: String = "TEXT_2" // Novo campo: "TEXT_2" ou "IMAGE_4"
)