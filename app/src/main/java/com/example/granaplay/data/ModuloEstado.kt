package com.example.granaplay.data

// Essa classe não é uma tabela do banco!
// Ela serve apenas para transportar dados prontos para a tela.
data class ModuloEstado(
    val modulo: Modulo,       // Os dados do módulo (nome, descrição)
    val totalLicoes: Int,     // Quantas estrelas cinzas desenhar
    val licoesConcluidas: Int,// Quantas estrelas pintar de dourado
    val isBloqueado: Boolean  // Se deve ficar cinza/desabilitado
)