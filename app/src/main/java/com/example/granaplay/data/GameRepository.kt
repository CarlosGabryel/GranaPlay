package com.example.granaplay.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.withContext

class GameRepository(private val gameDao: GameDao) {

    companion object {
        //private const val TEMPO_RECARGA_MS = 24 * 60 * 60 * 1000L
        private const val TEMPO_RECARGA_MS = 60 * 60 * 1000L
    }

    // ========================================================================
    // 1. INICIALIZAÇÃO E SEEDING
    // ========================================================================

    suspend fun verificarEPopularBanco() {
        withContext(Dispatchers.IO) {
            if (gameDao.contarModulos() > 0) return@withContext

            ConteudoInicial.dados.forEach { seedModulo ->
                // CORREÇÃO: Converte seedModulo.id (Int) para Long e força o ID
                val moduloId = gameDao.inserirModulo(
                    Modulo(
                        id = seedModulo.id.toLong(),
                        nome = seedModulo.titulo,
                        descricao = seedModulo.descricao,
                        ordem = seedModulo.id
                    )
                )

                seedModulo.licoes.forEach { seedLicao ->
                    // CORREÇÃO: Garante que a lição use o moduloId correto retornado acima
                    val licaoId = gameDao.inserirLicao(
                        Licao(
                            id = 0L,
                            moduloId = moduloId,
                            nome = seedLicao.titulo,
                            descricao = "Vamos aprender!",
                            pontuacao = 10,
                            ordem = seedLicao.id
                        )
                    )

                    seedLicao.questoes.forEach { seedQuestao ->
                        val questaoId = gameDao.inserirQuestao(
                            Questao(
                                id = 0L,
                                licaoId = licaoId,
                                enunciado = seedQuestao.enunciado,
                                tipo = seedQuestao.tipo.name,
                                feedbackAcerto = "Muito bem!",
                                feedbackErro = "Tente de novo."
                            )
                        )

                        seedQuestao.options.forEachIndexed { index, texto ->
                            val img = seedQuestao.images?.getOrNull(index)
                            gameDao.inserirAlternativa(
                                Alternativa(
                                    id = 0L,
                                    questaoId = questaoId,
                                    texto = texto,
                                    isCorreta = index == seedQuestao.correctIndex,
                                    imagemSource = img
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // ========================================================================
    // 2. GAMIFICAÇÃO & ESTADOS
    // ========================================================================

    suspend fun descontarVida(usuarioId: Long, vidasRestantes: Int) {
        withContext(Dispatchers.IO) {
            val agora = System.currentTimeMillis()
            gameDao.registrarPerdaVida(usuarioId, vidasRestantes, agora)
        }
    }

    suspend fun concluirLicao(usuarioId: Long, licaoId: Long, xpGanho: Int, moedasGanhas: Int, vidasRestantes: Int) {
        withContext(Dispatchers.IO) {
            val usuario = gameDao.getUsuarioPorId(usuarioId) ?: return@withContext
            gameDao.atualizarMoedas(usuarioId, usuario.moedas + moedasGanhas)
            gameDao.atualizarVidas(usuarioId, vidasRestantes)
            gameDao.adicionarXp(usuarioId, xpGanho)
            gameDao.registrarLicaoConcluida(UsuarioLicao(usuarioId = usuarioId, licaoId = licaoId))
        }
    }

    suspend fun verificarRecargaDeVidas(usuarioId: Long) {
        withContext(Dispatchers.IO) {
            val usuario = gameDao.getUsuarioPorId(usuarioId) ?: return@withContext
            if (usuario.pontosSaude >= 5) return@withContext
            val ultimaPerda = usuario.tempoUltimaVidaPerdida ?: return@withContext
            if (System.currentTimeMillis() - ultimaPerda >= TEMPO_RECARGA_MS) {
                gameDao.atualizarVidas(usuarioId, 5)
            }
        }
    }

    fun getModulosComEstado(usuarioId: Long): Flow<List<ModuloEstado>> {
        return combine(gameDao.getTodosModulos(), gameDao.getProgressoFlow(usuarioId)) { modulos, _ ->
            modulos
        }.transformLatest { modulos ->
            val listaEstados = mutableListOf<ModuloEstado>()
            // O primeiro módulo (índice 0, ordem 1) sempre começa desbloqueado
            var moduloAnteriorCompleto = true

            for (modulo in modulos) {
                val totalLicoes = gameDao.contarLicoesDoModulo(modulo.id)
                val licoesConcluidasCount = gameDao.contarLicoesConcluidasNoModulo(usuarioId, modulo.id)

                val estaBloqueado = !moduloAnteriorCompleto

                listaEstados.add(ModuloEstado(modulo, totalLicoes, licoesConcluidasCount, estaBloqueado))

                // Se completou este, o próximo será desbloqueado na próxima iteração
                moduloAnteriorCompleto = (licoesConcluidasCount == totalLicoes && totalLicoes > 0)
            }
            emit(listaEstados)
        }.flowOn(Dispatchers.IO)
    }

    // --- Métodos de Apoio ---
    fun getLicoesPorModulo(moduloId: Long) = gameDao.getLicoesDoModulo(moduloId)
    suspend fun getQuestoesPorLicao(licaoId: Long) = gameDao.getQuestoesDaLicao(licaoId)
    suspend fun getAlternativasPorQuestao(questaoId: Long) = gameDao.getAlternativasDaQuestao(questaoId)
    suspend fun getProximaLicao(usuarioId: Long, moduloId: Long) = gameDao.getProximaLicaoPendente(usuarioId, moduloId)

    // --- Usuário ---
    suspend fun cadastrarUsuario(usuario: Usuario) = gameDao.inserirUsuario(usuario)
    suspend fun buscarUsuarioPorEmail(email: String) = gameDao.getUsuarioPorEmail(email)
    suspend fun buscarUsuarioPorId(id: Long) = gameDao.getUsuarioPorId(id)
    fun getUsuarioEmTempoReal(id: Long) = gameDao.getUsuarioFlow(id)

    // CORREÇÃO: Métodos que faltavam no DAO agora são chamados aqui
    suspend fun temUsuariosCadastrados() = gameDao.contarUsuarios() > 0
    fun contarLicoesConcluidas(userId: Long) = gameDao.contarLicoesConcluidasPeloUsuario(userId)
}