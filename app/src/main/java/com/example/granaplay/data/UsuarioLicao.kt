package com.example.granaplay.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Tabela de junção (Join Table) que registra o progresso.
 * Conecta [Usuario] e [Licao], indicando quais lições foram concluídas.
 */
@Entity(
    tableName = "usuario_licoes",
    // Chave composta: Impede duplicidade (um usuário só completa a lição uma vez)
    primaryKeys = ["usuario_id", "licao_id"],
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuario_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Licao::class,
            parentColumns = ["id"],
            childColumns = ["licao_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    // Índice na coluna "child" (licao_id) é vital para performance de JOINs e verificação de FK
    indices = [Index(value = ["licao_id"])]
)
data class UsuarioLicao(
    @ColumnInfo(name = "usuario_id")
    val usuarioId: Long,

    @ColumnInfo(name = "licao_id")
    val licaoId: Long,

    @ColumnInfo(name = "completada_em")
    val completadaEm: Long = System.currentTimeMillis()
)