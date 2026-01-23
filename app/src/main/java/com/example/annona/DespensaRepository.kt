package com.example.annona

object DespensaRepository {

    private val itens = mutableListOf<Item>()

    fun setItens(novosItens: List<Item>) {
        itens.clear()
        itens.addAll(novosItens)
    }

    fun getItens(): List<Item> = itens

    fun getIngredientes(): List<String> =
        itens.map { it.titulo }
}
