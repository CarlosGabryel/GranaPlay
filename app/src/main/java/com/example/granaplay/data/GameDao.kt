package com.example.granaplay.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    // ========================================================================
    // GESTÃO DE CONTEÚDO (Módulos e Lições)
    // ========================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirModulo(modulo: Modulo): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirLicao(licao: Licao): Long

    @Query("SELECT EXISTS(SELECT * FROM modulos WHERE nome = :nome)")
    suspend fun existeModuloComNome(nome: String): Boolean

    @Query("SELECT COUNT(*) FROM modulos")
    suspend fun contarModulos(): Int

    @Query("SELECT * FROM modulos ORDER BY ordem ASC")
    fun getTodosModulos(): Flow<List<Modulo>>

    // CORREÇÃO: 'idModulo' mudou para 'modulo_id'
    @Query("SELECT * FROM licoes WHERE modulo_id = :moduloId ORDER BY ordem ASC")
    fun getLicoesDoModulo(moduloId: Long): Flow<List<Licao>>

    // ========================================================================
    // QUIZ (Questões e Alternativas)
    // ========================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirQuestao(questao: Questao): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirAlternativa(alternativa: Alternativa): Long

    // CORREÇÃO: 'licaoId' mudou para 'licao_id'
    @Query("SELECT * FROM questoes WHERE licao_id = :licaoId")
    suspend fun getQuestoesDaLicao(licaoId: Long): List<Questao>

    // CORREÇÃO: 'questaoId' mudou para 'questao_id'
    @Query("SELECT * FROM alternativas WHERE questao_id = :questaoId")
    suspend fun getAlternativasDaQuestao(questaoId: Long): List<Alternativa>

    // ========================================================================
    // USUÁRIO (Perfil, Vidas e Economia)
    // ========================================================================

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun inserirUsuario(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getUsuarioPorEmail(email: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun getUsuarioPorId(id: Long): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun getUsuarioFlow(id: Long): Flow<Usuario>

    @Query("UPDATE usuarios SET moedas = :novasMoedas WHERE id = :id")
    suspend fun atualizarMoedas(id: Long, novasMoedas: Int)

    // CORREÇÃO: 'pontosSaude' mudou para 'pontos_saude'
    @Query("UPDATE usuarios SET pontos_saude = :novasVidas WHERE id = :id")
    suspend fun atualizarVidas(id: Long, novasVidas: Int)

    @Query("UPDATE usuarios SET xp = xp + :quantidade WHERE id = :id")
    suspend fun adicionarXp(id: Long, quantidade: Int)

    // ========================================================================
    // PROGRESSO E GAMIFICAÇÃO
    // ========================================================================

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun registrarLicaoConcluida(progresso: UsuarioLicao)

    // CORREÇÃO: Colunas da tabela de junção mudaram para 'usuario_id' e 'licao_id'
    @Query("SELECT EXISTS(SELECT * FROM usuario_licoes WHERE usuario_id = :userId AND licao_id = :licaoId)")
    suspend fun isLicaoConcluida(userId: Long, licaoId: Long): Boolean

    // CORREÇÃO: 'idModulo' -> 'modulo_id'
    @Query("SELECT COUNT(*) FROM licoes WHERE modulo_id = :moduloId")
    suspend fun contarLicoesDoModulo(moduloId: Long): Int

    // CORREÇÃO: Atualizei todos os joins para usar snake_case (licao_id, usuario_id, modulo_id)
    @Query("""
        SELECT COUNT(*) FROM usuario_licoes 
        INNER JOIN licoes ON usuario_licoes.licao_id = licoes.id 
        WHERE usuario_licoes.usuario_id = :usuarioId AND licoes.modulo_id = :moduloId
    """)
    suspend fun contarLicoesConcluidasNoModulo(usuarioId: Long, moduloId: Long): Int

    // CORREÇÃO: Atualizei o WHERE e o sub-select para snake_case
    @Query("""
        SELECT * FROM licoes 
        WHERE modulo_id = :moduloId 
        AND id NOT IN (SELECT licao_id FROM usuario_licoes WHERE usuario_id = :usuarioId) 
        ORDER BY ordem ASC 
        LIMIT 1
    """)
    suspend fun getProximaLicaoPendente(usuarioId: Long, moduloId: Long): Licao?
}