package com.example.annona

import android.content.Context
import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import java.util.Calendar
import java.util.Date

// CLASSE DATA CLASS ITEM
// Representa um item da despensa do usuário (RF04, RF05, RF06, RF09, B01, B02, B03, B04, B06)
data class Item(
    @SerializedName("id_item_usuario")
    val id: Long,

    @SerializedName("nome")
    val nome: String,

    @SerializedName("id_categoria")
    val idCategoria: Int,

    @SerializedName("imagem")
    val imagem: String? = "item_padrao.png",

    @SerializedName("consumindo")
    val consumindo: Int = 0, // 1 para Aberto (Consumindo), 0 para Fechado (Por consumir)

    @SerializedName("possui_data_validade")
    val possuiDataValidade: Int = 0, // 1 se tem data, 0 se não tem

    @SerializedName("data_validade")
    val dataValidade: Date? = null,

    @SerializedName("id_local_armazenamento")
    val idLocal: Int = 4,

    // Quantidade, medida e preço mapeados do banco para não perder dados ao editar (B02)
    @SerializedName("quantidade")
    val quantidade: Double = 0.0,

    @SerializedName("id_medida")
    val idMedida: Int = 1,

    @SerializedName("preco")
    val preco: Double = 0.0,

    // Semáforo: 1=Vencido (Marrom), 2=Crítico (Vermelho), 3=Atenção (Amarelo), 4=Seguro (Verde)
    @SerializedName("id_flag")
    var flag: Int = 4,

    @SerializedName("dias_restantes")
    var diasRestantes: Int? = null,

    @SerializedName("fora_doacao")
    var foraDoacao: Int = 0,

    @SerializedName("razao_delecao")
    var razaoDelecao: String? = null,

    // ATRIBUTOS DE EXIBIÇÃO
    var estado: String? = "",
    @SerializedName("prazo")
    var prazo: String? = "",
    var dica: String? = "",

    @DrawableRes var imageRes: Int? = R.drawable.item_padrao,
    @DrawableRes var localIconRes: Int? = R.drawable.local_icone_armario,

    var localNome: String? = "",
    var categoria: String? = "",
    var medidaNome: String? = "",
    var dataVencimentoMs: Long? = null
)

// Converte os IDs e dados da API em textos, dicas e ícones visuais para o card (B04, B06, RN07, B20)
fun Item.mapearCampos(context: Context) {
    estado = if (consumindo == 1) "Consumindo" else "Por consumir"

    categoria = Categoria.obterPeloId(idCategoria).nomeCategoria
    localNome = LocalArmazenamento.obterPeloId(idLocal).nomeLocal
    medidaNome = Medida.obterPeloId(idMedida).nomeMedida

    // Dicas de armazenamento centralizadas considerando categoria e local (RN07, RN16, B04)
    dica = Categoria.obterDica(idCategoria, idLocal)

    localIconRes = when (idLocal) {
        1 -> R.drawable.local_icone_geladeira
        2 -> R.drawable.local_icone_freezer
        3 -> R.drawable.local_icone_fruteira
        4 -> R.drawable.local_icone_armario
        5 -> R.drawable.local_icone_banheiro
        6 -> R.drawable.local_icone_lavanderia
        else -> R.drawable.local_icone_armario
    }

    // Mapeia nomes de arquivo com correção para os 4 itens padrão que tinham diferença no banco (B20)
    val rawName = (imagem ?: "item_padrao.png").substringBeforeLast(".").trim()
    val imageName = when (rawName) {
        "paes_massas_e_doces_acucar_blocos" -> "paes_massas_e_doces_acucar_cubos"
        "pas_massas_e_doces_macarrao" -> "paes_massas_e_doces_macarrao"
        "pães_massas_e_doces_pao_frances" -> "paes_massas_e_doces_pao_frances"
        "higiene_perfumaria_e_farmacia_perfume_hello_kitti" -> "higiene_perfumaria_e_farmacia_hello_kitti"
        "feijoada_cap" -> "feijoada_capa"
        else -> rawName
    }

    imageRes = if (imageName.isNotBlank()) {
        context.resources.getIdentifier(imageName, "drawable", context.packageName)
            .takeIf { it != 0 } ?: R.drawable.item_padrao
    } else {
        R.drawable.item_padrao
    }

    // Caso a API já tenha enviado o prazo e flag calculados no servidor, respeitamos;
    // senão calculamos comparando apenas as datas locais sem erro de meia-noite/fuso (B03, B14, RN01, RN02)
    val resultadoSemaforo = calcularSemaforoData(possuiDataValidade == 1, dataValidade)
    flag = resultadoSemaforo.first
    diasRestantes = resultadoSemaforo.second
    prazo = resultadoSemaforo.third
    dataVencimentoMs = dataValidade?.time
}

// Função única para calcular semáforo comparando apenas datas civis (ano, mês, dia) (RN01, RN02, B03, B14)
fun calcularSemaforoData(possuiValidade: Boolean, dataValidade: Date?): Triple<Int, Int?, String> {
    if (!possuiValidade || dataValidade == null) {
        return Triple(4, null, "")
    }

    val calHoje = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val calVal = Calendar.getInstance().apply {
        time = dataValidade
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val diffMs = calVal.timeInMillis - calHoje.timeInMillis
    val dias = Math.round(diffMs.toDouble() / (1000 * 60 * 60 * 24)).toInt()

    return when {
        dias < 0 -> Triple(1, dias, "VENCIDO")
        dias == 0 -> Triple(2, 0, "0 DIAS")
        dias <= 3 -> Triple(2, dias, "$dias DIAS")
        dias <= 7 -> Triple(3, dias, "$dias DIAS")
        else -> Triple(4, dias, "$dias DIAS")
    }
}

// --- TABELAS DE REFERÊNCIA SINCRONIZÁVEIS COM A API (/infoGeral - RF23, B01, B04, B06) ---

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

data class Categoria(var id_categoria: Int, var nomeCategoria: String) {
    companion object {
        fun obterPeloNome(nome: String): Categoria =
            listaCategorias.firstOrNull { it.nomeCategoria.equals(nome, ignoreCase = true) }
                ?: listaCategorias.first()

        fun obterPeloId(id: Int): Categoria =
            listaCategorias.firstOrNull { it.id_categoria == id }
                ?: Categoria(id, "Outros")

        // Dicas centralizadas em um único lugar, considerando categoria e local (RN07, RN16, B04)
        fun obterDica(idCategoria: Int, idLocal: Int? = null): String {
            if (idCategoria == 2 && idLocal == 2) {
                return "No freezer, carnes e peixes conservam a validade por muito mais tempo."
            }
            if (idCategoria == 2 && idLocal == 1) {
                return "Na geladeira, consuma carnes e peixes em até 24h ou mova para o freezer."
            }
            if (idCategoria == 7 && idLocal == 2) {
                return "Congelado, o prazo de conservação é ampliado; descongele na geladeira."
            }
            return when (idCategoria) {
                1 -> "Mantenha em local fresco e longe da luz direta."
                2 -> "Mantenha no congelador se não for consumir em 24h."
                3 -> "Após aberto, guarde na geladeira."
                4 -> "Guarde em potes bem fechados, em local seco e longe da luz."
                5 -> "Lave bem antes de guardar."
                6 -> "Mantenha em local fresco e longe da umidade."
                7 -> "Guarde na geladeira, nas prateleiras superiores onde é mais frio."
                8 -> "Guarde na geladeira."
                9 -> "Mantenha longe do calor."
                10 -> "Mantenha em local arejado."
                else -> "Verifique o melhor local para armazenamento 😊"
            }
        }
    }
}

var listaLocais = listOf(
    LocalArmazenamento(1, "Geladeira"),
    LocalArmazenamento(2, "Freezer"),
    LocalArmazenamento(3, "Fruteira"),
    LocalArmazenamento(4, "Armário"),
    LocalArmazenamento(5, "Banheiro"),
    LocalArmazenamento(6, "Lavanderia")
)

data class LocalArmazenamento(var idLocal: Int, var nomeLocal: String) {
    companion object {
        fun obterPeloNome(nome: String): LocalArmazenamento =
            listaLocais.firstOrNull { it.nomeLocal.equals(nome, ignoreCase = true) }
                ?: listaLocais.firstOrNull { it.idLocal == 4 }
                ?: listaLocais.first()

        fun obterPeloId(id: Int): LocalArmazenamento =
            listaLocais.firstOrNull { it.idLocal == id }
                ?: LocalArmazenamento(id, "Armário")
    }
}

// Alinhado com a tabela 'medidas' do banco de dados (B01):
// 1 = Unidade, 2 = Pacote, 3 = Caixa, 4 = Grama, 5 = Kg, 6 = Ml, 7 = Litro
var listaMedidas = listOf(
    Medida(1, "Unidade"),
    Medida(2, "Pacote"),
    Medida(3, "Caixa"),
    Medida(4, "Grama"),
    Medida(5, "Kg"),
    Medida(6, "Ml"),
    Medida(7, "Litro")
)

data class Medida(var idMedida: Int, var nomeMedida: String) {
    companion object {
        fun obterPeloNome(nome: String): Medida {
            val n = nome.trim().lowercase()
            return listaMedidas.firstOrNull { it.nomeMedida.lowercase() == n }
                ?: when (n) {
                    "unidade", "un" -> obterPeloId(1)
                    "pacote", "pct" -> obterPeloId(2)
                    "caixa", "cx" -> obterPeloId(3)
                    "grama", "g" -> obterPeloId(4)
                    "kg", "quilo" -> obterPeloId(5)
                    "ml" -> obterPeloId(6)
                    "litro", "l" -> obterPeloId(7)
                    else -> listaMedidas.first()
                }
        }

        fun obterPeloId(id: Int): Medida =
            listaMedidas.firstOrNull { it.idMedida == id }
                ?: listaMedidas.first()
    }
}

// Atualiza as listas locais a partir da resposta da rota /infoGeral (RF23)
fun atualizarListasDeApoio(info: RespostaInfoGeral) {
    if (info.categorias.isNotEmpty()) {
        listaCategorias = info.categorias.map { Categoria(it.id_categoria, it.nome) }
    }
    if (info.locais.isNotEmpty()) {
        listaLocais = info.locais.map { LocalArmazenamento(it.id_local_armazenamento, it.nome_local_armazenamento) }
    }
    if (info.medidas.isNotEmpty()) {
        listaMedidas = info.medidas.map { Medida(it.id_medida, it.nome_medida) }
    }
}