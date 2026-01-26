package com.example.annona

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ServicoApi {
    @GET("users/listarItens/{id}")
    suspend fun listarItens(@Path("id") userId: Int): RespostaListarItens

    @POST("users/login")
    suspend fun login(@Path("id") userId: Int): RespostaLogin
}

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