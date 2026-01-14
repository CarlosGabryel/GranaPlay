package com.example.granaplay.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "usuario_licoes",
    // Chave composta: Um usuário só pode completar a mesma lição uma vez
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
    // Cria índice para a coluna da lição para otimizar a verificação da chave estrangeira
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