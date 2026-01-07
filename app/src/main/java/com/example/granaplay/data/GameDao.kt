package com.example.granaplay.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import com.example.granaplay.data.Questao

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

    // Novo: Buscar questões de uma lição
    @Query("SELECT * FROM questoes WHERE licaoId = :licaoId")
    suspend fun getQuestoesDaLicao(licaoId: Long): List<Questao>

    // Novo: Buscar alternativas de uma questão
    @Query("SELECT * FROM alternativas WHERE questaoId = :questaoId")
    suspend fun getAlternativasDaQuestao(questaoId: Long): List<Alternativa>

    // Opcional: Atualizar Vidas (Necessário para o fluxo de erro do quiz) [cite: 130]
    @Query("UPDATE usuarios SET pontosSaude = :novasVidas WHERE id = :id")
    suspend fun atualizarVidas(id: Long, novasVidas: Int)
}
