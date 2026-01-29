package com.example.annona

import android.R.attr.data
import android.R.attr.x
import android.content.Context
import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

//CLASSE DATA CLASS ITEM
//Toda vez que o app busca ou cria um alimento,
//ele segue este molde. Ele é dividido em duas partes:
//1. O que vem da API (Banco de Dados).
//2. O que o Android calcula sozinho (Lógica de exibição).
data class Item(
    // 1. CAMPOS DA API (MAPEADOS DO BANCO DE DADOS)

    @SerializedName("id_item_usuario")
    val id: Long, // ID único do item no banco

    @SerializedName("nome")
    val nome: String, // Nome (ex: "Feijão")

    @SerializedName("id_categoria")
    val idCategoria: Int, // Número da categoria (ex: 4 para Grãos)

    @SerializedName("imagem")
    val imagem: String, // Nome do arquivo de imagem vindo do Node (ex: "arroz.png")

    @SerializedName("consumindo")
    val consumindo: Int, // 1 para Aberto, 0 para Fechado

    @SerializedName("possui_data_validade")
    val possuiDataValidade: Int, // 1 se tem data, 0 se não tem

    @SerializedName("data_validade")
    val dataValidade: Date?, // A data de validade real (pode ser nula)

    @SerializedName("id_local_armazenamento")
    val idLocal: Int, // Onde está guardado (ex: 1 para Geladeira)

    // O status do Semáforo: 1=Vencido (Vermelho), 2=Crítico (Laranja), 3=Atenção (Amarelo), 4=Seguro (Verde)
    @SerializedName("id_flag")
    var flag: Int,




    // 2. ATRIBUTOS CALCULADOS NO PROJETO DE ANDROID
    // Estes campos não existem no banco, eu criei pra facilitar o preenchimento dos cards.

    var estado: String? = "", // Texto "Consumindo" ou "Por consumir"
    var prazo: String? = "",  // Texto "X DIAS"
    var dica: String? = "",   // A frase de conservação

    @DrawableRes var imageRes: Int? = R.drawable.item_padrao, // O ícone do alimento
    @DrawableRes var localIconRes: Int? = R.drawable.local_icone_armario, // O ícone do local

    var localNome: String? = "", // Nome do local por extenso (ex: "Geladeira")
    var categoria: String?,     // Nome da categoria por extenso (ex: "Grãos")
    var dataVencimentoMs: Long? // A data de vencimento em milissegundos para cálculos matemáticos
)



//FUNÇÃO MAPEAR CAMPOS
//Esta função é a que transforma os números da API em coisas visuais
//Ela é chamada toda vez que o app recebe dados do Node.js.
fun Item.mapearCampos(context: Context) {

    // Transforma o 0 ou 1 do banco em um texto
    estado = if (consumindo == 1) "Consumindo" else "Por consumir"

    // Busca o nome da categoria e do local usando os IDs (converte 1 em "Geladeira")
    categoria = Categoria.obterPeloId(idCategoria).nomeCategoria
    localNome = LocalArmazenamento.obterPeloId(idLocal).nomeLocal

    // LÓGICA DE DICAS: Define a frase baseada no tipo do alimento
    if (idCategoria == 1) { dica = "Mantenha em local fresco e longe da luz direta." }
    else if (idCategoria == 2) { dica = "Mantenha no congelador se não for consumir em 24h." }
    else if (idCategoria == 3) { dica = "Após aberto, guarde na geladeira." }
    else if (idCategoria == 4) { dica = "Guarde em potes bem fechados, em local seco e longe da luz." }
    else if (idCategoria == 5) { dica = "Lave bem antes de guardar." }
    else if (idCategoria == 6) { dica = "Mantenha em local fresco e longe da umidade." }
    else if (idCategoria == 7) { dica = "Guarde na geladeira, nas prateleiras superiores onde é mais frio." }
    else if (idCategoria == 8) { dica = "Guarde na geladeira." }
    else if (idCategoria == 9) { dica = "Mantenha longe do calor." }
    else if (idCategoria == 10) { dica = "Mantenha em local arejado." }
    else { dica = "Verifique o melhor local para armazenamento 😊" }



    // LÓGICA DE ÍCONES DE LOCAL: Escolhe o desenho correto para o card
    localIconRes = when(idLocal) {
        1 -> R.drawable.local_icone_geladeira
        2 -> R.drawable.local_icone_freezer
        3 -> R.drawable.local_icone_fruteira
        4 -> R.drawable.local_icone_armario
        5 -> R.drawable.local_icone_banheiro
        6 -> R.drawable.local_icone_lavanderia
        else -> R.drawable.local_icone_armario
    }

    // LÓGICA DE IMAGEM DO PRODUTO: Por exemplo, converte o nome "arroz.png" em R.drawable.arroz e assim pra todas as imagens
    val imageName = imagem.substringBeforeLast(".").trim()
    imageRes = if (!imageName.isNullOrBlank()) {
        context.resources.getIdentifier(imageName, "drawable", context.packageName).takeIf { it != 0 } ?: R.drawable.item_padrao
    } else {
        R.drawable.item_padrao
    }
}

//COMPANION OBJECTS (CATEGORIAS, LOCAIS, MEDIDAS)
//Estes blocos servem como "Tabelas de Referência" dentro do código.
//Eles permitem converter rapidamente um nome em ID ou um ID em nome.

// Lista fixa de Categorias
var listaCategorias = listOf(
    Categoria(1, "Bebidas"), Categoria(2, "Carnes e Peixes"), Categoria(3, "Enlatados"),
    Categoria(4, "Grãos"), Categoria(5, "Hortifruti"), Categoria(6, "Pães, Massas e Doces"),
    Categoria(7, "Laticínios"), Categoria(8, "Ovos"), Categoria(9, "Higiene, Perfumaria e Farmacia"),
    Categoria(10, "Limpeza")
)
data class Categoria(var id_categoria: Int, var nomeCategoria: String){
    companion object {
        fun obterPeloNome(nome: String) = listaCategorias.first { it.nomeCategoria == nome }
        fun obterPeloId(id: Int) = listaCategorias.first { it.id_categoria == id }
    }
}

// Lista fixa de Locais
var listaLocais = listOf(
    LocalArmazenamento(1, "Geladeira"), LocalArmazenamento(2, "Freezer"), LocalArmazenamento(3, "Fruteira"),
    LocalArmazenamento(4, "Armário"), LocalArmazenamento(5, "Banheiro"), LocalArmazenamento(6, "Lavanderia"),
    LocalArmazenamento(7, "Outro")
)
data class LocalArmazenamento(var idLocal: Int, var nomeLocal: String){
    companion object {
        fun obterPeloNome(nome: String) = listaLocais.first { it.nomeLocal == nome }
        fun obterPeloId(id: Int) = listaLocais.first { it.idLocal == id }
    }
}

// Lista fixa de Medidas
var listaMedidas = listOf(
    Medida(1, "unidade"), Medida(2, "kg"), Medida(3, "g"),
    Medida(4, "ml"), Medida(5, "l"), Medida(6, "caixa"), Medida(7, "pacote")
)
data class Medida(var idMedida: Int, var nomeMedida: String){
    companion object {
        fun obterPeloNome(nome: String) = listaMedidas.first { it.nomeMedida == nome }
    }
}