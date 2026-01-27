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
    }

    fun salvarUsuario(userId: Int, email: String) {
        prefs.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun obterUserId(): Int? {
        val id = prefs.getInt(KEY_USER_ID, -1)
        return if (id == -1) null else id
    }

    fun obterEmail(): String? {
        val email = prefs.getString(KEY_EMAIL, "")
        return email
    }

    fun estaLogado(): Boolean {
        return obterUserId() != null
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}