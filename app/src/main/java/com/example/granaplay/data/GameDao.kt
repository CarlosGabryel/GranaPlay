package com.example.granaplay.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    // --- Módulos e Lições ---

    // NOVO: Verifica se existe módulo pelo nome (para evitar duplicata)
    @Query("SELECT EXISTS(SELECT * FROM modulos WHERE nome = :nome)")
    suspend fun existeModuloComNome(nome: String): Boolean

    @Query("SELECT COUNT(*) FROM modulos")
    suspend fun contarModulos(): Int

    @Insert
    suspend fun inserirModulo(modulo: Modulo): Long

    @Insert
    suspend fun inserirLicao(licao: Licao): Long

    @Query("SELECT * FROM modulos ORDER BY ordem ASC")
    fun getTodosModulos(): Flow<List<Modulo>>

    @Query("SELECT * FROM licoes WHERE idModulo = :moduloId ORDER BY ordem ASC")
    fun getLicoesDoModulo(moduloId: Long): Flow<List<Licao>>

    // --- Quiz (Questões e Alternativas) ---

    @Insert
    suspend fun inserirQuestao(questao: Questao): Long

    @Insert
    suspend fun inserirAlternativa(alternativa: Alternativa): Long

    @Query("SELECT * FROM questoes WHERE licaoId = :licaoId")
    suspend fun getQuestoesDaLicao(licaoId: Long): List<Questao>

    @Query("SELECT * FROM alternativas WHERE questaoId = :questaoId")
    suspend fun getAlternativasDaQuestao(questaoId: Long): List<Alternativa>

    // --- Usuário, Vidas e Progresso ---

    @Insert
    suspend fun inserirUsuario(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getUsuarioPorEmail(email: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun getUsuarioFlow(id: Long): Flow<Usuario>

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun getUsuarioPorIdSemFlow(id: Long): Usuario?

    @Query("UPDATE usuarios SET moedas = :novasMoedas WHERE id = :id")
    suspend fun atualizarMoedas(id: Long, novasMoedas: Int)

    @Query("UPDATE usuarios SET pontosSaude = :novasVidas WHERE id = :id")
    suspend fun atualizarVidas(id: Long, novasVidas: Int)

    @Query("UPDATE usuarios SET xp = xp + :quantidade WHERE id = :id")
    suspend fun adicionarXp(id: Long, quantidade: Int)

    @Insert
    suspend fun registrarLicaoConcluida(progresso: UsuarioLicao)

    @Query("SELECT EXISTS(SELECT * FROM usuario_licoes WHERE idUsuario = :userId AND idLicao = :licaoId)")
    suspend fun isLicaoConcluida(userId: Long, licaoId: Long): Boolean

    @Query("SELECT COUNT(*) FROM licoes WHERE idModulo = :moduloId")
    suspend fun contarLicoesDoModulo(moduloId: Long): Int

    @Query("SELECT COUNT(*) FROM usuario_licoes " +
            "INNER JOIN licoes ON usuario_licoes.idLicao = licoes.id " +
            "WHERE usuario_licoes.idUsuario = :usuarioId AND licoes.idModulo = :moduloId")
    suspend fun contarLicoesConcluidasNoModulo(usuarioId: Long, moduloId: Long): Int

    @Query("SELECT * FROM licoes " +
            "WHERE idModulo = :moduloId AND id NOT IN (" +
            "    SELECT idLicao FROM usuario_licoes WHERE idUsuario = :usuarioId" +
            ") ORDER BY ordem ASC LIMIT 1")
    suspend fun getProximaLicaoPendente(usuarioId: Long, moduloId: Long): Licao?
}