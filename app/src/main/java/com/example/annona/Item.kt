package com.example.annona

import android.content.Context
import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

// Esta é a "Ficha do Produto". Toda vez que o app criar um alimento,
// ele vai seguir este modelo exato de informações.
data class Item(

    // ------ Campos da API (banco de dados): -----

    // Um número único para identificar esse item no banco de dados
    @SerializedName("id_item_usuario")
    val id: Long,

    // O nome do alimento (ex: "Feijão", "Arroz")
    @SerializedName("nome")
    val nome: String,

    @SerializedName("id_categoria")
    val idCategoria: Int,

    @SerializedName("imagem")
    val imagem: String,

    @SerializedName("consumindo")
    val consumindo: Int,

    @SerializedName("possui_data_validade")
    val possuiDataValidade: Int,

    @SerializedName("data_validade")
    val dataValidade: Date?,

    @SerializedName("id_local_armazenamento")
    val idLocal: Int,

    // O número do Semáforo: 1=Vencido, 2=Crítico, 3=Atenção, 4=Seguro
    @SerializedName("id_flag")
    var flag: Int,


    // ------ Atributos que nâo vem do banco de dados, mas serâo calculados no codigo de Android: -----

    // Se ele está "Consumindo" (aberto) ou "Por consumir" (fechado)
    var estado: String? = "",

    // O texto que diz quanto tempo falta (ex: "4 DIAS")
    val prazo: String? = "",

    // A dica de conservação que aparece no card (ex: "Armazenar na geladeira")
    val dica: String? = "",

    // O desenho/foto do alimento que aparece no lado esquerdo do card
    @DrawableRes var imageRes: Int? = R.drawable.item_padrao,

    // O desenho do ícone do local (ex: um desenho de geladeira ou armário)
    @DrawableRes var localIconRes: Int? = R.drawable.local_icone_armario,

    // O nome do lugar onde ele está guardado escrito por extenso
    var localNome: String? = "",


    // Armazena a categoria escolhida (ex: "Hortifruti") para que o sistema saiba qual prazo padrão buscar no banco de dados
    var categoria: String?,

    // Guarda o momento exato do vencimento em milissegundos; essencial para calcular a diferença em relação ao dia de hoje
    var dataVencimentoMs: Long?,

    )

fun Item.mapearCampos(context: Context) {

    estado = if (consumindo == 1) "Consumindo" else "Por consumir"

    categoria = when (idCategoria) {
        1 -> "Bebidas"
        2 -> "Carnes e Peixes"
        3 -> "Enlatados"
        4 -> "Grãos"
        5 -> "Hortifruti"
        6 -> "Pães, Massas e Doces"
        7 -> "Laticínios"
        8 -> "Ovos"
        9 -> "Higiene, Perfumaria e Farmacia"
        10 -> "Limpeza"
        else -> "Categoria desconhecida"
    }

    localNome = when (idLocal) {
        1 -> "Geladeira"
        2 -> "Freezer"
        3 -> "Fruteira"
        4 -> "Armário"
        5 -> "Banheiro"
        6 -> "Lavanderia"
        else -> "Local desconhecido"
    }

    localIconRes = when(idLocal) {
        1 -> R.drawable.local_icone_geladeira
        2 -> R.drawable.local_icone_freezer
        3 -> R.drawable.local_icone_fruteira
        4 -> R.drawable.local_icone_armario
        5 -> R.drawable.local_icone_banheiro
        6 -> R.drawable.local_icone_lavanderia
        else -> R.drawable.local_icone_armario
    }

    // imagem do backend → drawable
    val imageName = imagem
        .substringBeforeLast(".") // remove .png, .jpg etc
        .trim()
    imageRes = if (!imageName.isNullOrBlank()) {
        context.resources.getIdentifier(
            imageName,
            "drawable",
            context.packageName
        ).takeIf { it != 0 } ?: R.drawable.item_padrao
    } else {
        R.drawable.item_padrao
    }

    val hoje = Calendar.getInstance().timeInMillis
    if(possuiDataValidade == 1) {
        dataVencimentoMs = dataValidade?.time ?: (hoje + TimeUnit.DAYS.toMillis(3650))
    }

    // flag será recalculada depois por processarValidades()
    flag = 0
}