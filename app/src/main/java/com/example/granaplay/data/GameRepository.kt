package com.example.granaplay.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {

    // --- Módulos e Lições (Dados Observáveis com Flow) ---
    // O Flow emite novos valores sempre que o banco muda
    val todosModulos: Flow<List<Modulo>> = gameDao.getTodosModulos()

    fun getLicoesPorModulo(moduloId: Long): Flow<List<Licao>> {
        return gameDao.getLicoesDoModulo(moduloId)
    }

    // --- Operações de Usuário (Suspend functions) ---
    // Devem ser chamadas dentro de uma Coroutine (viewModelScope)

    suspend fun cadastrarUsuario(usuario: Usuario) {
        gameDao.inserirUsuario(usuario)
    }

    suspend fun buscarUsuarioPorEmail(email: String): Usuario? {
        return gameDao.getUsuarioPorEmail(email)
    }

    suspend fun adicionarMoedas(usuarioId: Long, quantidade: Int) {
        // Lógica simples: primeiro pega o saldo atual (seria ideal fazer isso tudo no SQL ou transaction)
        // Para simplificar aqui, vamos assumir que apenas atualizamos o valor final.
        // O ideal seria: gameDao.somarMoedas(usuarioId, quantidade)
        // Mas vamos manter a assinatura do DAO anterior:
        gameDao.atualizarMoedas(usuarioId, quantidade)
    }

    // --- Inicialização de Dados (Seed) ---
    // Útil para criar dados falsos quando o app abre pela primeira vez
    suspend fun popularBancoInicial() {
        val modulo1Id = gameDao.inserirModulo(Modulo(nome = "Educação Financeira Básica", descricao = "Aprendendo sobre educação financeira"))
        gameDao.inserirLicao(Licao(nome = "O que é Dinheiro?", descricao = "História do dinheiro", pontuacao = 10, idModulo = modulo1Id))
        gameDao.inserirLicao(Licao(nome = "Poupando", descricao = "A importância de guardar", pontuacao = 15, idModulo = modulo1Id))
    }
}
