package com.example.annona

import androidx.annotation.DrawableRes

// Esta é a "Ficha do Produto". Toda vez que o app criar um alimento,
// ele vai seguir este modelo exato de informações.
data class Item(
    // Um número único para identificar esse item no banco de dados
    val id: Long,

    // O nome do alimento (ex: "Feijão", "Arroz")
    val titulo: String,

    // Se ele está "Consumindo" (aberto) ou "Por consumir" (fechado)
    val estado: String,

    // O texto que diz quanto tempo falta (ex: "4 DIAS")
    val prazo: String,

    // A dica de conservação que aparece no card (ex: "Armazenar na geladeira")
    val dica: String,

    // O número do Semáforo: 1=Vencido, 2=Crítico, 3=Atenção, 4=Seguro
    val flag: Int,

    // O desenho/foto do alimento que aparece no lado esquerdo do card
    @DrawableRes val imageRes: Int,

    // O desenho do ícone do local (ex: um desenho de geladeira ou armário)
    @DrawableRes val localIconRes: Int,

    // O nome do lugar onde ele está guardado escrito por extenso
    val localNome: String
)