package com.example.granaplay.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "alternativas",
    foreignKeys = [
        ForeignKey(
            entity = Questao::class,
            parentColumns = ["id"],
            childColumns = ["questaoId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ]
)
data class Alternativa(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questaoId: Long,
    val texto: String,      // Ex: "Um presente" [cite: 243]
    val isCorreta: Boolean,  // Para validar a lógica de acerto/erro [cite: 118, 124]
    val imagemRes: String? = null // Novo campo: Nome da imagem (nullable)
)