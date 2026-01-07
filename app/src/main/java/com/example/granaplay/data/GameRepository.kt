package com.example.granaplay.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first

class GameRepository(private val gameDao: GameDao) {

    // --- Módulos e Lições (Dados Observáveis com Flow) ---
    // O Flow emite novos valores sempre que o banco muda e atualiza a tela
    val todosModulos: Flow<List<Modulo>> = gameDao.getTodosModulos()

    fun getLicoesPorModulo(moduloId: Long): Flow<List<Licao>> {
        return gameDao.getLicoesDoModulo(moduloId)
    }

    // Você precisará criar estes métodos no DAO para buscar o Quiz (veja nota abaixo)
    suspend fun getQuestoesPorLicao(licaoId: Long): List<Questao> {
        return gameDao.getQuestoesDaLicao(licaoId)
    }

    suspend fun getAlternativasPorQuestao(questaoId: Long): List<Alternativa> {
        return gameDao.getAlternativasDaQuestao(questaoId)
    }

    // --- Operações de Usuário (Suspend functions) ---

    suspend fun cadastrarUsuario(usuario: Usuario) {
        gameDao.inserirUsuario(usuario)
    }

    suspend fun buscarUsuarioPorEmail(email: String): Usuario? {
        return gameDao.getUsuarioPorEmail(email)
    }

    suspend fun adicionarMoedas(usuarioId: Long, quantidade: Int) {
        gameDao.atualizarMoedas(usuarioId, quantidade)
    }

    // --- POPULAÇÃO DO BANCO DE DADOS (A Mágica acontece aqui) ---
    // Essa função lê o objeto ConteudoInicial e salva tudo no SQL
    suspend fun popularBancoInicial() {
        withContext(Dispatchers.IO) {

            // Passo 1: Loop pelos Módulos (Ex: "Conhecendo o Dinheiro")
            ConteudoInicial.dados.forEach { seedModulo ->
                val moduloId = gameDao.inserirModulo(
                    Modulo(
                        nome = seedModulo.titulo,
                        descricao = "Lições sobre ${seedModulo.titulo}",
                        ordem = seedModulo.id // Usa o ID do JSON para ordenar
                    )
                )

                // Passo 2: Loop pelas Lições dentro do módulo (Ex: "O que são moedas?")
                seedModulo.licoes.forEach { seedLicao ->
                    val licaoId = gameDao.inserirLicao(
                        Licao(
                            nome = seedLicao.titulo,
                            descricao = "Vamos aprender!",
                            pontuacao = 10,
                            idModulo = moduloId, // Amarra a lição ao módulo criado acima
                            ordem = seedLicao.id // Usa o ID do JSON para ordenar
                        )
                    )

                    // Passo 3: Loop pelas Perguntas dentro da lição
                    seedLicao.questoes.forEach { seedQuestao ->
                        val questaoId = gameDao.inserirQuestao(
                            Questao(
                                licaoId = licaoId, // Amarra a pergunta à lição
                                enunciado = seedQuestao.enunciado,
                                feedbackAcerto = "Muito bem! Você acertou!",
                                feedbackErro = "Poxa, não foi dessa vez. Tente de novo!",
                                tipo = seedQuestao.tipo // TEXT_2 ou IMAGE_4
                            )
                        )

                        // Passo 4: Loop pelas Alternativas (Opções de resposta)
                        seedQuestao.options.forEachIndexed { index, textoOpcao ->

                            // Lógica para saber se essa opção tem imagem associada no JSON
                            val imagemNome = if (seedQuestao.images != null && index < seedQuestao.images.size) {
                                seedQuestao.images[index]
                            } else {
                                null
                            }

                            gameDao.inserirAlternativa(
                                Alternativa(
                                    questaoId = questaoId, // Amarra a alternativa à questão
                                    texto = textoOpcao,
                                    isCorreta = (index == seedQuestao.correctIndex), // Verifica se é a correta baseada no índice do JSON
                                    imagemRes = imagemNome
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // Função de verificação automática
    suspend fun verificarEPopularBanco() {
        val qtd = gameDao.contarModulos()
        if (qtd == 0) {
            popularBancoInicial()
        }
    }

    // Função que retorna a lista de módulos já com o estado (Bloqueado/Desbloqueado/Estrelas)
    suspend fun getModulosComEstado(usuarioId: Long): List<ModuloEstado> {
        // 1. Pega todos os módulos ordenados (1, 2, 3...)
        // Nota: Precisamos usar 'first()' aqui para pegar o valor do Flow uma única vez de forma síncrona
        val modulos = gameDao.getTodosModulos().first()

        val listaEstados = mutableListOf<ModuloEstado>()
        var moduloAnteriorCompleto = true // O primeiro (Módulo 1) sempre começa liberado

        for (modulo in modulos) {
            // Conta totais e concluídos
            val total = gameDao.contarLicoesDoModulo(modulo.id)
            val concluidas = gameDao.contarLicoesConcluidasNoModulo(usuarioId, modulo.id)

            // Lógica de Bloqueio:
            // O módulo atual só está liberado se o ANTERIOR estava completo.
            val estaBloqueado = !moduloAnteriorCompleto

            // Cria o objeto para a tela
            listaEstados.add(
                ModuloEstado(
                    modulo = modulo,
                    totalLicoes = total,
                    licoesConcluidas = concluidas,
                    isBloqueado = estaBloqueado
                )
            )

            // Atualiza a flag para o próximo loop:
            // Se eu completei tudo deste módulo (concluidas == total), o próximo estará liberado.
            moduloAnteriorCompleto = (concluidas == total && total > 0)
        }

        return listaEstados
    }

    // Função para o botão "Jogar" do módulo
    suspend fun getProximaLicao(usuarioId: Long, moduloId: Long): Licao? {
        return gameDao.getProximaLicaoPendente(usuarioId, moduloId)
    }
}