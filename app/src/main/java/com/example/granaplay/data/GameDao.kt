package com.example.granaplay.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    // --- Usuário ---
    @Insert
    suspend fun inserirUsuario(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getUsuarioPorEmail(email: String): Usuario?

    @Query("UPDATE usuarios SET moedas = :novasMoedas WHERE id = :id")
    suspend fun atualizarMoedas(id: Long, novasMoedas: Int)

    // --- Módulos e Lições ---
    @Insert
    suspend fun inserirModulo(modulo: Modulo): Long

    @Insert
    suspend fun inserirLicao(licao: Licao)

    @Query("SELECT * FROM modulos")
    fun getTodosModulos(): Flow<List<Modulo>> // Flow atualiza a UI automaticamente

    @Query("SELECT * FROM licoes WHERE idModulo = :moduloId")
    fun getLicoesDoModulo(moduloId: Long): Flow<List<Licao>>

    // --- Progresso ---
    @Insert
    suspend fun registrarLicaoConcluida(progresso: UsuarioLicao)

    @Query("SELECT EXISTS(SELECT * FROM usuario_licoes WHERE idUsuario = :userId AND idLicao = :licaoId)")
    suspend fun isLicaoConcluida(userId: Long, licaoId: Long): Boolean
}
