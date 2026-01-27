package com.example.annona

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ServicoApi {
    @GET("users/listarItens/{id}")
    suspend fun listarItens(@Path("id") userId: Int): RespostaListarItens

    @POST("users/login")
    suspend fun login(@Body bodyLogin: BodyLogin): Response<RespostaLogin> // Response<> serve para checar código
}


// ----- Classes com a forma dos BODY pra ser enviados à API ------
data class BodyLogin(
    val email: String,
    val senha: String
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