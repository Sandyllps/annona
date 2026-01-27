package com.example.annona

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.Date

interface ServicoApi {
    @GET("users/listarItens/{id}")
    suspend fun listarItens(@Path("id") userId: Int): RespostaListarItens

    @POST("users/login")
    suspend fun login(@Body bodyLogin: BodyLogin): Response<RespostaLogin> // Response<> serve para checar código

    @POST("users/itensUsuario")
    suspend fun criarItemUsuario(@Body bodyCriarItem: BodyCriarItem): Response<RespostaCriarItem>

    // [NOVO] Esta rota busca a lista de itens que o sistema sugere (itens padrão) do seu Node.js
    // Ela vai chamar o endereço que você configurou no seu servidor: http://seu-ip:porta/itens_padrao
    @GET("itens_padrao")
    suspend fun listarItensPadrao(): RespostaItensPadrao
}


// ----- Classes com a forma dos BODY pra ser enviados à API ------
data class BodyLogin(
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
    val quantidade: Int,
    val id_medida: Int,
    val preco: Double,
    val id_flag: Int,
)

// ----- Classes com a forma das RESPOSTAS da API ------
data class RespostaListarItens(
    val listaItens: List<Item>
)

data class RespostaLogin(

    @SerializedName("response")
    val Response: String,

    @SerializedName("id_usuario")
    val IdUsuario: Int,

    @SerializedName("nome_usuario")
    val NomeUsuario: String,

    @SerializedName("email")
    val Email: String,
)

data class RespostaCriarItem(
    @SerializedName("response")
    val Response: String,

    @SerializedName("id_item_usuario")
    val IdItemUsuario: Int,
)

// [NOVO] Classes com nomes humanos para lidar com as sugestões de alimentos
// Elas servem para "desembrulhar" o JSON que vem do banco de dados e transformar em objetos Kotlin

data class RespostaItensPadrao(
    // O SerializedName garante que o Android ache a chave "itens" no seu JSON do Node (que vimos no Postman)
    // Mas dentro do nosso código, chamamos de 'listaDeSugestoes' para ficar mais fácil de entender
    @SerializedName("itens")
    val listaDeSugestoes: List<ModeloItemSugestao>
)

data class ModeloItemSugestao(
    val nome: String,
    val id_categoria: Int,
    val imagem: String
)