package com.example.annona

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.annona.databinding.ActivityTelaLoginBinding
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Data classes
data class LoginRequest(
    val email: String,
    val senha: String
)

data class LoginResponse(
    val id: Int,
    val nome: String,
    val email: String
)

// Interface Retrofit
interface ApiService {
    @POST("/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}

class TelaLogin : AppCompatActivity() {
    private lateinit var binding: ActivityTelaLoginBinding
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTelaLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/") // ⚡ endereço do backend no emulador
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Botão de login
        binding.botaoLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val senha = binding.etSenha.text.toString()
            Log.d("LOGIN", "Enviando login -> email: $email, senha: $senha")

            if(email.isEmpty() || senha.isEmpty()){
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Requisição de login no backend
            lifecycleScope.launch {
                try {
                    val response = apiService.login(LoginRequest(email, senha))
                    if(response.isSuccessful){
                        val user = response.body()
                        // Se o Login for válido → abrir MainActivity
                        val intent = Intent(this@TelaLogin, MainActivity::class.java)
                        intent.putExtra("USER_NAME", user?.nome)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@TelaLogin, "Dados inválidos", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception){
                    Toast.makeText(this@TelaLogin, "Dados inválidos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
