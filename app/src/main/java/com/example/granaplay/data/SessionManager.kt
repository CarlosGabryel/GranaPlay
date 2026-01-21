package com.example.granaplay.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Gerenciador de sessão simples baseado em SharedPreferences.
 * Responsável por persistir o estado de login do usuário entre execuções do app.
 */
class SessionManager(context: Context) {

    // Usa applicationContext para evitar memory leaks se passar uma Activity
    private val prefs: SharedPreferences = context.applicationContext
        .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "granaplay_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
    }

    /**
     * Salva os dados do usuário e marca a sessão como ativa.
     */
    fun criarSessao(userId: Long) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putLong(KEY_USER_ID, userId)
            apply() // Salva de forma assíncrona (não trava a UI)
        }
    }

    fun isLogado(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Retorna o ID do usuário logado.
     * @return O ID (Long) ou -1 caso não encontrado.
     */
    fun getUserId(): Long {
        return prefs.getLong(KEY_USER_ID, -1)
    }

    /**
     * Limpa todos os dados da sessão (Logout).
     */
    fun logout() {
        prefs.edit().clear().apply()
    }
}