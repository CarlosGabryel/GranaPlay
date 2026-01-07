package com.example.granaplay.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
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

    // Atualizar Vidas (Necessário para a mecânica de erro no Quiz)
    @Query("UPDATE usuarios SET pontosSaude = :novasVidas WHERE id = :id")
    suspend fun atualizarVidas(id: Long, novasVidas: Int)

    // --- Módulos e Lições ---
    @Query("SELECT COUNT(*) FROM modulos")
    suspend fun contarModulos(): Int

    @Insert
    suspend fun inserirModulo(modulo: Modulo): Long

    // IMPORTANTE: Alterado para retornar Long (o ID da lição inserida)
    @Insert
    suspend fun inserirLicao(licao: Licao): Long

    @Query("SELECT * FROM modulos ORDER BY ordem ASC")
    fun getTodosModulos(): Flow<List<Modulo>>

    @Query("SELECT * FROM licoes WHERE idModulo = :moduloId ORDER BY ordem ASC")
    fun getLicoesDoModulo(moduloId: Long): Flow<List<Licao>>

    // --- Quiz (Questões e Alternativas) ---
    // Métodos novos que estavam faltando e causando o erro

    @Insert
    suspend fun inserirQuestao(questao: Questao): Long

    @Insert
    suspend fun inserirAlternativa(alternativa: Alternativa): Long

    @Query("SELECT * FROM questoes WHERE licaoId = :licaoId")
    suspend fun getQuestoesDaLicao(licaoId: Long): List<Questao>

    @Query("SELECT * FROM alternativas WHERE questaoId = :questaoId")
    suspend fun getAlternativasDaQuestao(questaoId: Long): List<Alternativa>

    // --- Progresso ---
    @Insert
    suspend fun registrarLicaoConcluida(progresso: UsuarioLicao)

    @Query("SELECT EXISTS(SELECT * FROM usuario_licoes WHERE idUsuario = :userId AND idLicao = :licaoId)")
    suspend fun isLicaoConcluida(userId: Long, licaoId: Long): Boolean

    // --- Consultas de Progresso e Lógica de Jogo ---

    // 1. Conta quantas lições existem num módulo (Para saber o total de estrelas possíveis)
    @Query("SELECT COUNT(*) FROM licoes WHERE idModulo = :moduloId")
    suspend fun contarLicoesDoModulo(moduloId: Long): Int

    // 2. Conta quantas lições desse módulo o usuário já terminou (Para pintar as estrelas douradas)
    @Query("SELECT COUNT(*) FROM usuario_licoes " +
            "INNER JOIN licoes ON usuario_licoes.idLicao = licoes.id " +
            "WHERE usuario_licoes.idUsuario = :usuarioId AND licoes.idModulo = :moduloId")
    suspend fun contarLicoesConcluidasNoModulo(usuarioId: Long, moduloId: Long): Int

    // 3. Busca a primeira lição que o usuário AINDA NÃO FEZ (Para o botão "Continuar")
    @Query("SELECT * FROM licoes " +
            "WHERE idModulo = :moduloId AND id NOT IN (" +
            "    SELECT idLicao FROM usuario_licoes WHERE idUsuario = :usuarioId" +
            ") ORDER BY ordem ASC LIMIT 1")
    suspend fun getProximaLicaoPendente(usuarioId: Long, moduloId: Long): Licao?
}