package com.example.annona

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.Date

//INTERFACE SERVICO API
//Esta interface é o "catálogo" de todas as chamadas que o Android pode fazer ao Servidor (Node.js).
//O Retrofit usa esta interface para saber qual URL chamar e que tipo de dado esperar de volta.
interface ServicoApi {

    // [GET] Busca a lista de itens da despensa de um usuário específico.
    // O {id} é uma variável que será substituída pelo ID real do usuário logado.
    @GET("users/listarItens/{id}")
    suspend fun listarItens(@Path("id") userId: Int): RespostaListarItens

    // [POST] Envia e-mail e senha para o servidor validar o acesso.
    // Usamos 'Response<>' aqui para conseguirmos ler o código de status (ex: 200 para sucesso ou 401 para erro).
    @POST("users/login")
    suspend fun login(@Body bodyLogin: BodyLogin): Response<RespostaLogin>

    // [POST] Envia os dados de um novo alimento para ser salvo no banco de dados do servidor.
    @POST("users/itensUsuario")
    suspend fun criarItemUsuario(@Body bodyCriarItem: BodyCriarItem): Response<RespostaCriarItem>

    // [GET] Busca a lista de "Itens Padrão" (sugestões) cadastrados no sistema.
    // Esta é a função que alimenta o Autocomplete na tela de cadastro.
    @GET("itens_padrao")
    suspend fun listarItensPadrao(): RespostaItensPadrao
}

//CLASSES DE CORPO (BODY)/
 //Estas classes representam o que o Android ENVIA para o servidor.
 //É como se fosse a ficha que você preenche e entrega no balcão.

// Dados necessários para realizar o login.
data class BodyLogin(
    val email: String,
    val senha: String
)

// Dados necessários para cadastrar um novo item na despensa.
data class BodyCriarItem(
    val id_usuario: Int,
    val nome: String,
    val id_categoria: Int,
    val imagem: String,
    val consumindo: Boolean,
    val possui_data_validade: Boolean,
    val data_validade: String?, // Pode ser nulo se o item não tiver validade
    val id_local_armazenamento: Int,
    val quantidade: Int,
    val id_medida: Int,
    val preco: Double,
    val id_flag: Int,
)

//CLASSES DE RESPOSTA (RESPONSE)/
 //Estas classes representam o que o Android RECEBE do servidor.
//Elas transformam o texto JSON que o Node.js envia em objetos que o Kotlin consegue ler.

// Resposta da lista de despensa. O servidor manda um JSON com "listaItens": [...]
data class RespostaListarItens(
    val listaItens: List<Item>
)

// Resposta após o login.
data class RespostaLogin(
    // O @SerializedName é usado quando o nome no banco/Node (ex: "response")
    // é diferente ou precisa ser mapeado exatamente para a variável no Kotlin.
    @SerializedName("response")
    val Response: String,

    @SerializedName("id_usuario")
    val IdUsuario: Int,

    @SerializedName("nome_usuario")
    val NomeUsuario: String,

    @SerializedName("email")
    val Email: String,
)

// Resposta após criar um item. O servidor devolve o ID do item que acabou de ser gerado no banco.
data class RespostaCriarItem(
    @SerializedName("response")
    val Response: String,

    @SerializedName("id_item_usuario")
    val IdItemUsuario: Int,
)


// MODELOS PARA SUGESTÕES (AUTOCAMPLETE)
//Classes criadas para facilitar a leitura dos itens padrão que vêm do banco.
data class RespostaItensPadrao(
    // No servidor Node, eu  envioi: res.json({ itens: [...] })
    // O @SerializedName("itens") garante que o Kotlin ache essa chave no JSON.
    @SerializedName("itens")
    val listaDeSugestoes: List<ModeloItemSugestao>
)

// Representa cada alimento sugerido individualmente.
data class ModeloItemSugestao(
    val nome: String,
    val id_categoria: Int,
    val imagem: String
)