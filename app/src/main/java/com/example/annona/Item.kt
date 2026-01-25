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
    @DrawableRes val localIconRes: Int,  // Ícone do local de armazenamento
    val localNome: String                // NOVO: Nome do local por extenso (ex: "Geladeira")
)