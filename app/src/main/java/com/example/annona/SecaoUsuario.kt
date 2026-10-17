package com.example.annona

import android.content.Context

class SecaoUsuario(context: Context) {

    private val prefs = context.getSharedPreferences(
        "annona_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_NOME = "user_nome"
        private const val KEY_TOKEN = "user_token"
        private const val KEY_NOTIFICACOES = "notificacoes_ativas"
        private const val KEY_BASE_URL = "api_custom_base_url"
        private const val KEY_ITENS_FORA_DOACAO = "itens_fora_doacao"
    }

    fun salvarUsuario(
        userId: Int,
        email: String,
        nome: String? = null,
        token: String? = null,
        notificacoesAtivas: Boolean = true
    ) {
        val editor = prefs.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_EMAIL, email)
            .putBoolean(KEY_NOTIFICACOES, notificacoesAtivas)

        if (!nome.isNullOrBlank()) {
            editor.putString(KEY_NOME, nome)
        }
        if (!token.isNullOrBlank()) {
            editor.putString(KEY_TOKEN, token)
        }
        editor.apply()
    }

    fun obterUserId(): Int? {
        val id = prefs.getInt(KEY_USER_ID, -1)
        return if (id <= 0) null else id
    }

    fun obterEmail(): String? {
        return prefs.getString(KEY_EMAIL, "")
    }

    fun obterNome(): String? {
        return prefs.getString(KEY_NOME, "Meu Perfil")
    }

    fun obterToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun notificacoesAtivas(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICACOES, true)
    }

    fun salvarNotificacoesAtivas(ativas: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICACOES, ativas).apply()
    }

    fun salvarBaseUrl(url: String) {
        prefs.edit().putString(KEY_BASE_URL, url.trim()).apply()
        ApiClient.resetarCliente()
    }

    fun obterBaseUrl(defaultUrl: String): String {
        val custom = prefs.getString(KEY_BASE_URL, null)
        return if (!custom.isNullOrBlank()) custom else defaultUrl
    }

    fun obterIdsForaDoacao(): Set<Long> {
        val setStr = prefs.getStringSet(KEY_ITENS_FORA_DOACAO, emptySet()) ?: emptySet()
        return setStr.mapNotNull { it.toLongOrNull() }.toSet()
    }

    fun marcarItemForaDoacao(itemId: Long, fora: Boolean) {
        val atual = prefs.getStringSet(KEY_ITENS_FORA_DOACAO, emptySet())?.toMutableSet() ?: mutableSetOf()
        if (fora) {
            atual.add(itemId.toString())
        } else {
            atual.remove(itemId.toString())
        }
        prefs.edit().putStringSet(KEY_ITENS_FORA_DOACAO, atual).apply()
    }

    fun estaLogado(): Boolean {
        return obterUserId() != null
    }

    fun logout() {
        val customUrl = prefs.getString(KEY_BASE_URL, null)
        prefs.edit().clear().apply()
        if (!customUrl.isNullOrBlank()) {
            prefs.edit().putString(KEY_BASE_URL, customUrl).apply()
        }
    }
}