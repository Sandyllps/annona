package com.example.annona

import androidx.annotation.DrawableRes

data class Item(
    val id: Long,
    val titulo: String,
    val estado: String,
    val prazo: String,
    val dica: String,
    val flag: Int,
    @DrawableRes val imageRes: Int,      // Imagem principal do alimento (ex: Tomate)
    @DrawableRes val localIconRes: Int,  // Ícone do local (ex: Desenho da Geladeira)
    val localNome: String                // Texto que aparece ao lado do ícone (ex: "Geladeira")
)