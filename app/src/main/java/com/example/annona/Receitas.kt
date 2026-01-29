package com.example.annona

// Classe que representa uma receita completa
// Essa classe é usada para armazenar todas as informações de uma receita,
// como nome, descrição, tempo de preparo, porções, ingredientes, modo de preparo, tag e imagem
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

// Essa classe representa uma etapa do modo de preparo de uma receita
// Ela contém informações como o número da etapa, a descrição da etapa e a imagem correspondente
data class EtapaPreparo(
    val numero: Int,
    val descricao: String,
    val imagem: Int
)
