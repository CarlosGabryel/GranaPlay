package com.example.granaplay.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.combine

class GameRepository(private val gameDao: GameDao) {

    // ========================================================================
    // GAMIFICAÇÃO E PROGRESSO (NOVO MÉTODO ADICIONADO)
    // ========================================================================

    /**
     * Salva todo o progresso da lição de uma vez:
     * 1. Atualiza saldo de moedas
     * 2. Atualiza saldo de vidas (se perdeu alguma)
     * 3. Adiciona o XP ganho
     * 4. Marca a lição como concluída para o usuário
     */

    // Função para descontar vida IMEDIATAMENTE e marcar o horário
    suspend fun descontarVida(usuarioId: Long, vidasRestantes: Int) {
        withContext(Dispatchers.IO) {
            val agora = System.currentTimeMillis()
            gameDao.registrarPerdaVida(usuarioId, vidasRestantes, agora)
        }
    }
    suspend fun concluirLicao(usuarioId: Long, licaoId: Long, xpGanho: Int, moedasGanhas: Int, vidasRestantes: Int) {
        withContext(Dispatchers.IO) {
            // 1. Busca dados atuais do usuário
            val usuario = gameDao.getUsuarioPorId(usuarioId)

            if (usuario != null) {
                // Atualiza Moedas (Soma ao que já tinha)
                val novoSaldoMoedas = usuario.moedas + moedasGanhas
                gameDao.atualizarMoedas(usuarioId, novoSaldoMoedas)

                // Atualiza Vidas (Define para o valor final da lição)
                gameDao.atualizarVidas(usuarioId, vidasRestantes)

                // Atualiza XP (Soma)
                gameDao.adicionarXp(usuarioId, xpGanho)
            }

            // 2. Registra que a lição foi feita na tabela de junção
            val progresso = UsuarioLicao(usuarioId = usuarioId, licaoId = licaoId)
            gameDao.registrarLicaoConcluida(progresso)
        }
    }

    // ========================================================================
    // MÉTODOS EXISTENTES (MANTIDOS)
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

    val todosModulos: Flow<List<Modulo>> = gameDao.getTodosModulos()

    fun getLicoesPorModulo(moduloId: Long): Flow<List<Licao>> =
        gameDao.getLicoesDoModulo(moduloId)

    suspend fun getQuestoesPorLicao(licaoId: Long): List<Questao> =
        gameDao.getQuestoesDaLicao(licaoId)

    suspend fun getAlternativasPorQuestao(questaoId: Long): List<Alternativa> =
        gameDao.getAlternativasDaQuestao(questaoId)

    suspend fun cadastrarUsuario(usuario: Usuario) = gameDao.inserirUsuario(usuario)

    suspend fun buscarUsuarioPorEmail(email: String): Usuario? = gameDao.getUsuarioPorEmail(email)

    suspend fun buscarUsuarioPorId(id: Long): Usuario? = gameDao.getUsuarioPorId(id)

    fun getUsuarioEmTempoReal(id: Long): Flow<Usuario> = gameDao.getUsuarioFlow(id)

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
            val umDiaEmMs = 60 * 1000
            //val umDiaEmMs = 24 * 60 * 60 * 1000

            if (agora - ultimaPerda >= umDiaEmMs) {
                gameDao.atualizarVidas(usuarioId, 5)
            }
        }
    }

    // Substitua a função getModulosComEstado por esta versão reativa:
    fun getModulosComEstado(usuarioId: Long): Flow<List<ModuloEstado>> {
        // COMBINE: Observa Módulos E Progresso ao mesmo tempo
        return gameDao.getTodosModulos()
            .combine(gameDao.getProgressoFlow(usuarioId)) { modulos, _ ->
                modulos // Retorna a lista de módulos para ser processada no map abaixo
            }
            .map { modulos ->
                val listaEstados = mutableListOf<ModuloEstado>()
                var moduloAnteriorCompleto = true

                for (modulo in modulos) {
                    val totalLicoes = gameDao.contarLicoesDoModulo(modulo.id)
                    // Agora essa contagem será refeita sempre que o progresso mudar!
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

    suspend fun temUsuariosCadastrados(): Boolean {
        return gameDao.contarUsuarios() > 0
    }
}