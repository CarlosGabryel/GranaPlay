package com.example.granaplay.data

/**
 * Modelo de UI (View Object) que agrega a entidade Módulo com o progresso do usuário.
 * Esta classe não é salva no Banco de Dados.
 */
data class ModuloEstado(
    // Dados estáticos do módulo (Título, Descrição, Ordem)
    val modulo: Modulo,

    // Métricas de progresso
    val totalLicoes: Int,
    val licoesConcluidas: Int,

    // Estado de acesso (baseado na conclusão do módulo anterior)
    val isBloqueado: Boolean
)