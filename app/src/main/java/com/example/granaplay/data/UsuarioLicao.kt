package com.example.granaplay.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "usuario_licoes",
    primaryKeys = ["idUsuario", "idLicao"],
    foreignKeys = [
        ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["idUsuario"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Licao::class, parentColumns = ["id"], childColumns = ["idLicao"], onDelete = ForeignKey.CASCADE)
    ]
)
data class UsuarioLicao(
    val idUsuario: Long,
    val idLicao: Long,
    val completadaEm: Long = System.currentTimeMillis() // Data de conclusão
)
