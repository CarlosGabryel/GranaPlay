package com.example.granaplay.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GameRepository(private val gameDao: GameDao) {

    val todosModulos: Flow<List<Modulo>> = gameDao.getTodosModulos()

    fun getUsuarioEmTempoReal(id: Long): Flow<Usuario> {
        return gameDao.getUsuarioFlow(id)
    }

    // --- POPULAÇÃO À PROVA DE FALHAS (IDEMPOTENTE) ---
    suspend fun verificarEPopularBanco() {
        withContext(Dispatchers.IO) {
            ConteudoInicial.dados.forEach { seedModulo ->

                // SOLUÇÃO DA DUPLICAÇÃO:
                // Antes de inserir, verifica se JÁ EXISTE um módulo com esse nome exato.
                val jaExiste = gameDao.existeModuloComNome(seedModulo.titulo)

                if (!jaExiste) {
                    // Só entra aqui se não existir. Seguro para rodar várias vezes.
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
                                idModulo = moduloId,
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
                                        imagemRes = imagemNome
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Demais Métodos (Mantidos iguais) ---

    fun getLicoesPorModulo(moduloId: Long): Flow<List<Licao>> = gameDao.getLicoesDoModulo(moduloId)

    suspend fun getQuestoesPorLicao(licaoId: Long): List<Questao> = gameDao.getQuestoesDaLicao(licaoId)

    suspend fun getAlternativasPorQuestao(questaoId: Long): List<Alternativa> = gameDao.getAlternativasDaQuestao(questaoId)

    suspend fun verificarRecargaDeVidas(usuarioId: Long) {
        val usuario = gameDao.getUsuarioPorIdSemFlow(usuarioId) ?: return
        if (usuario.pontosSaude >= 5) return
        val ultimaPerda = usuario.tempoUltimaVidaPerdida
        if (ultimaPerda != null) {
            val agora = System.currentTimeMillis()
            val vinteQuatroHorasEmMs = 24 * 60 * 60 * 1000
            if (agora - ultimaPerda >= vinteQuatroHorasEmMs) {
                gameDao.atualizarVidas(usuarioId, 5)
            }
        }
    }

    suspend fun ganharXp(usuarioId: Long, xpGanho: Int) = gameDao.adicionarXp(usuarioId, xpGanho)

    suspend fun cadastrarUsuario(usuario: Usuario) = gameDao.inserirUsuario(usuario)

    suspend fun buscarUsuarioPorEmail(email: String): Usuario? = gameDao.getUsuarioPorEmail(email)

    suspend fun adicionarMoedas(usuarioId: Long, quantidade: Int) = gameDao.atualizarMoedas(usuarioId, quantidade)

    fun getModulosComEstado(usuarioId: Long): Flow<List<ModuloEstado>> {
        return gameDao.getTodosModulos().map { modulos ->
            val listaEstados = mutableListOf<ModuloEstado>()
            var moduloAnteriorCompleto = true
            for (modulo in modulos) {
                val total = gameDao.contarLicoesDoModulo(modulo.id)
                val concluidas = gameDao.contarLicoesConcluidasNoModulo(usuarioId, modulo.id)
                val estaBloqueado = !moduloAnteriorCompleto
                listaEstados.add(
                    ModuloEstado(
                        modulo = modulo,
                        totalLicoes = total,
                        licoesConcluidas = concluidas,
                        isBloqueado = estaBloqueado
                    )
                )
                moduloAnteriorCompleto = (concluidas == total && total > 0)
            }
            listaEstados
        }
    }

    suspend fun getProximaLicao(usuarioId: Long, moduloId: Long): Licao? = gameDao.getProximaLicaoPendente(usuarioId, moduloId)
}