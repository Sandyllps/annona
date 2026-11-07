package com.example.annona

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ServicoApi {

    // --- Autenticação e Cadastro (RF01, RF02) ---
    @POST("users/login")
    suspend fun login(@Body bodyLogin: BodyLogin): Response<RespostaLogin>

    @POST("users/cadastro")
    suspend fun cadastrarUsuario(@Body bodyCadastro: BodyCadastro): Response<RespostaCadastro>

    // --- Despensa CRUD (RF04, RF05, RF06, RF07, RF13) ---
    @GET("users/listarItens/{id}")
    suspend fun listarItens(@Path("id") userId: Int): RespostaListarItens

    @POST("users/ItensUsuario")
    suspend fun criarItemUsuario(@Body bodyCriarItem: BodyCriarItem): Response<RespostaCriarItem>

    @PUT("users/editarItem/{id}")
    suspend fun editarItemUsuario(
        @Path("id") itemId: Long,
        @Body bodyEditarItem: BodyCriarItem
    ): Response<ResponseBody>

    @PUT("users/itens/{id}/consumo")
    suspend fun alterarConsumoItem(
        @Path("id") itemId: Long,
        @Body bodyAlterarConsumo: BodyAlterarConsumo
    ): Response<ResponseBody>

    @HTTP(method = "DELETE", path = "users/deletarItem/{id_item_usuario}", hasBody = true)
    suspend fun deletarItemUsuario(
        @Path("id_item_usuario") idItem: Int,
        @Body bodyDeletarItem: BodyDeletarItem
    ): Response<Any>

    // --- Itens Padrão e Info Geral (RF08, RF23, B04, B11) ---
    @GET("itens_padrao")
    suspend fun listarItensPadrao(): RespostaItensPadrao

    @GET("infoGeral")
    suspend fun buscarInfoGeral(): RespostaInfoGeral

    // --- Receitas (RF17, RF22, RN14) ---
    @GET("receitas")
    suspend fun listarReceitas(): RespostaReceitasApi

    @GET("receitas/sugeridas/{id_usuario}")
    suspend fun listarReceitasSugeridas(@Path("id_usuario") userId: Int): RespostaReceitasSugeridasApi

    // --- Doação (RF15, RN12, RN13, B16) ---
    @GET("users/{id_usuario}/doacao")
    suspend fun listarItensDoacao(@Path("id_usuario") userId: Int): RespostaListarItens

    @PUT("users/itens/{id_item_usuario}/doacao")
    suspend fun alterarDisponibilidadeDoacao(
        @Path("id_item_usuario") itemId: Long,
        @Body bodyDoacao: BodyAlterarDoacao
    ): Response<ResponseBody>

    // --- Histórico e Perfil / Notificações (RF16, RF20, RF21) ---
    @GET("users/{id_usuario}/historico")
    suspend fun listarHistorico(@Path("id_usuario") userId: Int): RespostaHistorico

    @PUT("users/{id_usuario}/notificacoes")
    suspend fun atualizarNotificacoes(
        @Path("id_usuario") userId: Int,
        @Body bodyNotif: BodyNotificacoes
    ): Response<ResponseBody>
}

// --- CLASSES DE CORPO (BODY) ---

data class BodyLogin(
    val email: String,
    val senha: String
)

data class BodyCadastro(
    val nome_usuario: String,
    val email: String,
    val senha: String
)

data class BodyCriarItem(
    val id_usuario: Int,
    val nome: String,
    val id_categoria: Int,
    val imagem: String,
    val consumindo: Boolean,
    val possui_data_validade: Boolean,
    val data_validade: String?,
    val id_local_armazenamento: Int,
    val quantidade: Double,
    val id_medida: Int,
    val preco: Double,
    val id_flag: Int
)

data class BodyAlterarConsumo(
    val id_usuario: Int,
    val consumindo: Boolean
)

data class BodyAlterarDoacao(
    val id_usuario: Int,
    val fora_doacao: Boolean
)

data class BodyDeletarItem(
    val razao_delecao: String,
    val id_usuario: Int? = null
)

data class BodyNotificacoes(
    val notificacoes_ativas: Boolean
)

// --- CLASSES DE RESPOSTA (RESPONSE) ---

data class RespostaListarItens(
    val listaItens: List<Item>
)

data class RespostaLogin(
    @SerializedName("response")
    val Response: String,

    @SerializedName("id_usuario")
    val IdUsuario: Int,

    @SerializedName("nome_usuario")
    val NomeUsuario: String?,

    @SerializedName("email")
    val Email: String,

    @SerializedName("token")
    val Token: String? = null,

    @SerializedName("notificacoes_ativas")
    val NotificacoesAtivas: Boolean? = true
)

data class RespostaCadastro(
    @SerializedName("response")
    val response: String,

    @SerializedName("id_usuario")
    val idUsuario: Int?,

    @SerializedName("nome_usuario")
    val nomeUsuario: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("token")
    val token: String? = null
)

data class RespostaCriarItem(
    @SerializedName("response")
    val Response: String,

    @SerializedName("id_item_usuario")
    val IdItemUsuario: Int
)

data class RespostaItensPadrao(
    @SerializedName("itens")
    val listaDeSugestoes: List<ModeloItemSugestao>
)

data class ModeloItemSugestao(
    val id_item_padrao: Int? = null,
    val nome: String,
    val id_categoria: Int,
    val imagem: String
)

data class RespostaInfoGeral(
    val medidas: List<MedidaApi> = emptyList(),
    val locais: List<LocalApi> = emptyList(),
    val categorias: List<CategoriaApi> = emptyList(),
    val flags: List<FlagApi> = emptyList(),
    val itens_padrao: List<ModeloItemSugestao> = emptyList()
)

data class MedidaApi(
    val id_medida: Int,
    val nome_medida: String
)

data class LocalApi(
    val id_local_armazenamento: Int,
    val nome_local_armazenamento: String,
    val imagem: String? = null
)

data class CategoriaApi(
    val id_categoria: Int,
    val nome: String
)

data class FlagApi(
    val id_flag: Int,
    val nome_flag: String
)

data class RespostaReceitasApi(
    val receitas: List<ReceitaApiDto> = emptyList()
)

data class RespostaReceitasSugeridasApi(
    val receitas: List<ReceitaApiDto> = emptyList(),
    val todas_receitas: List<ReceitaApiDto> = emptyList()
)

data class ReceitaApiDto(
    @SerializedName(value = "id", alternate = ["ID"])
    val id: Int? = null,
    @SerializedName(value = "nome", alternate = ["NOME"])
    val nome: String? = null,
    @SerializedName(value = "descricao", alternate = ["DESCRIÇÃO"])
    val descricao: String? = null,
    @SerializedName(value = "tempoPreparo", alternate = ["TEMPO"])
    val tempoPreparo: String? = null,
    @SerializedName(value = "porcoes", alternate = ["PORÇÃO"])
    val porcoes: String? = null,
    @SerializedName(value = "tag", alternate = ["TAG"])
    val tag: String? = null,
    @SerializedName(value = "imagem_capa", alternate = ["img capa"])
    val imagemCapa: String? = null,
    @SerializedName("ingredientes")
    val ingredientes: List<String>? = null,
    @SerializedName("INGREDIENTES")
    val ingredientesTextoBruto: String? = null,
    @SerializedName("modoPreparo")
    val modoPreparo: List<EtapaPreparoApiDto>? = null,
    @SerializedName("ingredientes_disponiveis")
    val ingredientesDisponiveis: List<String>? = null
)

data class EtapaPreparoApiDto(
    val numero: Int,
    val descricao: String,
    val imagem_nome: String? = null
)

data class RespostaHistorico(
    val historico: List<Item> = emptyList(),
    val resumo: ResumoHistorico? = null
)

data class ResumoHistorico(
    val consumidos: Int = 0,
    val desperdicados: Int = 0,
    val doados: Int = 0,
    val outros: Int = 0
)

data class RespostaDelecao(
    val response: String
)