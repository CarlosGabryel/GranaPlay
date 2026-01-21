package com.example.granaplay.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa uma unidade de ensino dentro de um [Modulo].
 * Configurada com DELETE CASCADE: Se o módulo for apagado, as lições também serão.
 */
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
    // Índice necessário para evitar scans de tabela completa ao buscar lições de um módulo
    indices = [Index(value = ["modulo_id"])]
)
data class Licao(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "modulo_id")
    val moduloId: Long,

    val nome: String,
    val descricao: String,
    val pontuacao: Int,

    // Define a sequência lógica da lição na trilha (1, 2, 3...)
    val ordem: Int
)