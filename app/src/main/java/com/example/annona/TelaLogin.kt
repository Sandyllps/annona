package com.example.annona

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.example.annona.databinding.ActivityTelaLoginBinding
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TelaLogin : AppCompatActivity() {
    private lateinit var binding: ActivityTelaLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val session = SecaoUsuario(this)

        if (session.estaLogado()) {
            val intent = Intent(this, MainActivity::class.java)
            // Enviando o ID do usuario pra MainActivity
            intent.putExtra("USER_ID", session.obterUserId())
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityTelaLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botão de login sem verificação
        binding.botaoLogin.setOnClickListener {
            fazerLogin(binding.etEmail.text.toString(),
                binding.etSenha.text.toString())
        }
    }


    private fun fazerLogin(email: String, senha: String) {

        // Checando que os EditText nâo estejam vazios
        if (email.isBlank() || senha.isBlank()) {
            Toast.makeText(this, "Preencha email e senha", Toast.LENGTH_SHORT).show()
            return
        }

        // Criando objeto de retrofit para fazer a requisiçâo à API
        val urlApi = this.getString(R.string.api_base_url)
        val retrofit = Retrofit.Builder()
            .baseUrl(urlApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ServicoApi::class.java)

        val activityContext = this

        lifecycleScope.launch {
            try {

                val resp = api.login(BodyLogin(email, senha))

                // checa status code
                if (resp.isSuccessful && resp.body() != null) {
                    // ---------- LOGIN COM SUCESO (HTTP 200) ------------

                    val body = resp.body()!!

                    // Salvando o id do usuario em SharedPreferences
                    SecaoUsuario(activityContext).salvarUsuario(body.IdUsuario, email)

                    // navegar para MainActivity
                    val intent = Intent(activityContext, MainActivity::class.java)
                    // Enviando o ID do usuario pra MainActivity
                    intent.putExtra("USER_ID", body.IdUsuario)
                    startActivity(intent)
                    finish()


                } else {
                    // ---------- LOGIN COM ERRO (CODIGO HTTP DE ERRO) ------------

                    val errorBody = resp.errorBody()?.string()

                    Toast.makeText(
                        activityContext,
                        "Erro no login: HTTP ${resp.code()} - ${errorBody}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                // ---------- LOGIN COM ERRO (FALHA NA CONEXAO COM A API) ------------
                Toast.makeText(
                    activityContext,
                    "Falha na requisição: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
