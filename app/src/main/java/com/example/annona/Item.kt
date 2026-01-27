package com.example.annona

import android.R.attr.data
import android.R.attr.x
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

    categoria = Categoria.obterPeloId(idCategoria).nomeCategoria
    localNome = LocalArmazenamento.obterPeloId(idLocal).nomeLocal

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

var listaCategorias = listOf(
    Categoria(1, "Bebidas"),
    Categoria(2, "Carnes e Peixes"),
    Categoria(3, "Enlatados"),
    Categoria(4, "Grãos"),
    Categoria(5, "Hortifruti"),
    Categoria(6, "Pães, Massas e Doces"),
    Categoria(7, "Laticínios"),
    Categoria(8, "Ovos"),
    Categoria(9, "Higiene, Perfumaria e Farmacia"),
    Categoria(10, "Limpeza")


)
data class Categoria(
    var id_categoria: Int,
    var nomeCategoria: String
){
    companion object{
        fun obterPeloNome(nome: String): Categoria{
            val categoria = listaCategorias.first{categoria -> categoria.nomeCategoria == nome}
            return categoria;
        }

        fun obterPeloId(id: Int): Categoria{
            val categoria = listaCategorias.first{categoria -> categoria.id_categoria == id}
            return categoria;
        }
    }
}


var listaLocais = listOf(
    LocalArmazenamento(1, "Geladeira"),
    LocalArmazenamento(2, "Freezer"),
    LocalArmazenamento(3, "Fruteira"),
    LocalArmazenamento(4, "Armário"),
    LocalArmazenamento(5, "Banheiro"),
    LocalArmazenamento(6, "Lavanderia"),
    LocalArmazenamento(7, "Outro"),
)
data class LocalArmazenamento(
    var idLocal: Int,
    var nomeLocal: String
){
    companion object{
        fun obterPeloNome(nome: String): LocalArmazenamento{
            val local = listaLocais.first{x -> x.nomeLocal == nome}
            return local;
        }

        fun obterPeloId(id: Int): LocalArmazenamento{
            val local = listaLocais.first{x -> x.idLocal == id}
            return local;
        }
    }
}



var listaMedidas = listOf(
    Medida(1, "unidade"),
    Medida(2, "kg"),
    Medida(3, "g"),
    Medida(4, "ml"),
    Medida(5, "l"),
    Medida(6, "caixa"),
    Medida(7, "pacote"),
)
data class Medida(
    var idMedida: Int,
    var nomeMedida: String
){
    companion object{
        fun obterPeloNome(nome: String): Medida{
            val medida = listaMedidas.first{x -> x.nomeMedida == nome}
            return medida;
        }

        fun obterPeloId(id: Int): Medida{
            val medida = listaMedidas.first{x -> x.idMedida == id}
            return medida;
        }
    }
}

