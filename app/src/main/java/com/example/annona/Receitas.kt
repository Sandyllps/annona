package com.example.annona

data class Receita(
    val id: Long,
    val nome: String,
    val ingredientes: List<String>,
    val tempoPreparo: String,
    val tag: String,
    val imagem: Int
)
