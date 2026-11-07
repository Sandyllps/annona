package com.example.annona

// Classe que representa uma receita completa (RF17, RF18, RF19, RF22, RN14)
data class Receita(
    val nome: String,
    val descricao: String,
    val tempoPreparo: String,
    val porcoes: String,
    val ingredientes: List<String>,
    val modoPreparo: List<EtapaPreparo>,
    val tag: String,
    val imagem: Int,
    // Lista dos itens padrão ligados a esta receita (para sugestão inteligente pela despensa - RF17, RN14)
    val itensPadraoRelacionados: List<String> = emptyList(),
    // Lista de ingredientes que o usuário tem atualmente na despensa
    var ingredientesDisponiveis: List<String> = emptyList()
)

// Representa uma etapa do modo de preparo de uma receita
data class EtapaPreparo(
    val numero: Int,
    val descricao: String,
    val imagem: Int
)
