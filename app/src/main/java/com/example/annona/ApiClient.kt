package com.example.annona

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Cliente único de rede (Singleton) para evitar recriar o Retrofit em cada tela (B05, RNF12)
// Também adiciona automaticamente o cabeçalho de autenticação JWT (RNF09, B08) e permite configurar a URL em Ajustes (B18)
object ApiClient {

    @Volatile
    private var retrofit: Retrofit? = null
    private var currentBaseUrl: String? = null

    fun getService(context: Context): ServicoApi {
        val session = SecaoUsuario(context.applicationContext)
        val rawUrl = session.obterBaseUrl(context.getString(R.string.api_base_url)).trim()
        val baseUrl = if (rawUrl.endsWith("/")) rawUrl else "$rawUrl/"

        if (retrofit == null || currentBaseUrl != baseUrl) {
            synchronized(this) {
                if (retrofit == null || currentBaseUrl != baseUrl) {
                    val okHttpClient = OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .addInterceptor { chain ->
                            val original = chain.request()
                            val token = SecaoUsuario(context.applicationContext).obterToken()
                            val builder = original.newBuilder()
                            if (!token.isNullOrBlank()) {
                                builder.header("Authorization", "Bearer $token")
                            }
                            // Evita tela de aviso do ngrok gratuito em requisições da API
                            builder.header("ngrok-skip-browser-warning", "true")
                            chain.proceed(builder.build())
                        }
                        .build()

                    retrofit = Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    currentBaseUrl = baseUrl
                }
            }
        }

        return retrofit!!.create(ServicoApi::class.java)
    }

    fun resetarCliente() {
        retrofit = null
        currentBaseUrl = null
    }
}

