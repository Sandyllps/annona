package com.example.annona

import androidx.annotation.DrawableRes

data class Item(
    val id: Long,
    val titulo: String,
    val estado: String,
    val prazo: String,
    val dica: String,
    @DrawableRes val imageRes: Int
)