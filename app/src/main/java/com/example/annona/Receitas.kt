package com.example.annona

data class Receita(
    val nome: String,
    val descricao: String,
    val tempoPreparo: String,
    val porcoes: String,
    val ingredientes: List<String>,
    val modoPreparo: List<EtapaPreparo>,
    val tag: String,
    val imagem: Int
)

data class EtapaPreparo(
    val numero: Int,
    val descricao: String,
    val imagem: Int
)
