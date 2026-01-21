package com.example.granaplay.data

/**
 * Objeto de UI (View Object) que combina a entidade [Modulo] com o progresso do usuário.
 * Usado para alimentar a lista na tela principal.
 */
data class ModuloEstado(
    val modulo: Modulo,
    val totalLicoes: Int,
    val licoesConcluidas: Int,
    val isBloqueado: Boolean
) {
    /**
     * Retorna a porcentagem de conclusão (0 a 100).
     * Facilita a configuração de ProgressBar na interface.
     */
    val progressoPercentual: Int
        get() = if (totalLicoes > 0) (licoesConcluidas * 100) / totalLicoes else 0
}