package com.example.granaplay.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GameRepository(private val gameDao: GameDao) {

    // ========================================================================
    // POPULAÇÃO DO BANCO (SEEDING)
    // ========================================================================

    suspend fun verificarEPopularBanco() {
        withContext(Dispatchers.IO) {
            ConteudoInicial.dados.forEach { seedModulo ->
                val jaExiste = gameDao.existeModuloComNome(seedModulo.titulo)

                if (!jaExiste) {
                    val moduloId = gameDao.inserirModulo(
                        Modulo(
                            nome = seedModulo.titulo,
                            descricao = seedModulo.descricao,
                            ordem = seedModulo.id
                        )
                    )

                    seedModulo.licoes.forEach { seedLicao ->
                        val licaoId = gameDao.inserirLicao(
                            Licao(
                                nome = seedLicao.titulo,
                                descricao = "Vamos aprender!",
                                pontuacao = 10,
                                // CORREÇÃO AQUI: O nome do parâmetro na classe Licao agora é 'moduloId'
                                moduloId = moduloId,
                                ordem = seedLicao.id
                            )
                        )

                        seedLicao.questoes.forEach { seedQuestao ->
                            val questaoId = gameDao.inserirQuestao(
                                Questao(
                                    licaoId = licaoId,
                                    enunciado = seedQuestao.enunciado,
                                    feedbackAcerto = "Muito bem! Você acertou!",
                                    feedbackErro = "Poxa, não foi dessa vez. Tente de novo!",
                                    tipo = seedQuestao.tipo
                                )
                            )

                            seedQuestao.options.forEachIndexed { index, textoOpcao ->
                                val imagemNome = if (seedQuestao.images != null && index < seedQuestao.images.size) {
                                    seedQuestao.images[index]
                                } else {
                                    null
                                }

                                gameDao.inserirAlternativa(
                                    Alternativa(
                                        questaoId = questaoId,
                                        texto = textoOpcao,
                                        isCorreta = (index == seedQuestao.correctIndex),
                                        // CORREÇÃO TAMBÉM AQUI: Se você atualizou a classe Alternativa,
                                        // o campo mudou de 'imagemRes' para 'imagemSource'
                                        imagemSource = imagemNome
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ========================================================================
    // GESTÃO DE CONTEÚDO (LEITURA)
    // ========================================================================

    val todosModulos: Flow<List<Modulo>> = gameDao.getTodosModulos()

    fun getLicoesPorModulo(moduloId: Long): Flow<List<Licao>> =
        gameDao.getLicoesDoModulo(moduloId)

    suspend fun getQuestoesPorLicao(licaoId: Long): List<Questao> =
        gameDao.getQuestoesDaLicao(licaoId)

    suspend fun getAlternativasPorQuestao(questaoId: Long): List<Alternativa> =
        gameDao.getAlternativasDaQuestao(questaoId)

    // ========================================================================
    // USUÁRIO E AUTENTICAÇÃO
    // ========================================================================

    suspend fun cadastrarUsuario(usuario: Usuario) = gameDao.inserirUsuario(usuario)

    suspend fun buscarUsuarioPorEmail(email: String): Usuario? = gameDao.getUsuarioPorEmail(email)

    fun getUsuarioEmTempoReal(id: Long): Flow<Usuario> = gameDao.getUsuarioFlow(id)

    // ========================================================================
    // GAMIFICAÇÃO E PROGRESSO
    // ========================================================================

    suspend fun ganharXp(usuarioId: Long, xpGanho: Int) =
        gameDao.adicionarXp(usuarioId, xpGanho)

    suspend fun adicionarMoedas(usuarioId: Long, quantidade: Int) {
        val usuario = gameDao.getUsuarioPorId(usuarioId) ?: return
        val novoSaldo = usuario.moedas + quantidade
        gameDao.atualizarMoedas(usuarioId, novoSaldo)
    }

    suspend fun verificarRecargaDeVidas(usuarioId: Long) {
        val usuario = gameDao.getUsuarioPorId(usuarioId) ?: return

        if (usuario.pontosSaude >= 5) return

        val ultimaPerda = usuario.tempoUltimaVidaPerdida
        if (ultimaPerda != null) {
            val agora = System.currentTimeMillis()
            val umDiaEmMs = 24 * 60 * 60 * 1000

            if (agora - ultimaPerda >= umDiaEmMs) {
                gameDao.atualizarVidas(usuarioId, 5)
            }
        }
    }

    fun getModulosComEstado(usuarioId: Long): Flow<List<ModuloEstado>> {
        return gameDao.getTodosModulos().map { modulos ->
            val listaEstados = mutableListOf<ModuloEstado>()
            var moduloAnteriorCompleto = true

            for (modulo in modulos) {
                val totalLicoes = gameDao.contarLicoesDoModulo(modulo.id)
                val licoesConcluidas = gameDao.contarLicoesConcluidasNoModulo(usuarioId, modulo.id)
                val estaBloqueado = !moduloAnteriorCompleto

                listaEstados.add(
                    ModuloEstado(
                        modulo = modulo,
                        totalLicoes = totalLicoes,
                        licoesConcluidas = licoesConcluidas,
                        isBloqueado = estaBloqueado
                    )
                )
                moduloAnteriorCompleto = (licoesConcluidas == totalLicoes && totalLicoes > 0)
            }
            listaEstados
        }
    }

    suspend fun getProximaLicao(usuarioId: Long, moduloId: Long): Licao? =
        gameDao.getProximaLicaoPendente(usuarioId, moduloId)
}