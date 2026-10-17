package com.example.annona

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.annona.databinding.ActivityTelaLoginBinding
import kotlinx.coroutines.launch
import org.json.JSONObject

class TelaLogin : AppCompatActivity() {
    private lateinit var binding: ActivityTelaLoginBinding

    private val cadastroLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val emailCadastrado = result.data?.getStringExtra("EMAIL_CADASTRADO")
            if (!emailCadastrado.isNullOrBlank()) {
                binding.etEmail.setText(emailCadastrado)
                binding.etSenha.requestFocus()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val session = SecaoUsuario(this)

        // Se o usuário já estiver logado, abre diretamente a tela da Despensa (RF03)
        if (session.estaLogado()) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("USER_ID", session.obterUserId())
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityTelaLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.botaoLogin.setOnClickListener {
            fazerLogin(
                binding.etEmail.text.toString().trim(),
                binding.etSenha.text.toString()
            )
        }

        binding.btnCriarConta.setOnClickListener {
            val intent = Intent(this, TelaCadastro::class.java)
            cadastroLauncher.launch(intent)
        }

        binding.txtEsqueceuSenha.setOnClickListener {
            Toast.makeText(
                this,
                "Para redefinir sua senha, entre em contato com o suporte ou crie uma nova conta.",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.loginGoogle.setOnClickListener {
            Toast.makeText(
                this,
                "Use seu e-mail ou clique em 'Criar conta' para entrar no Annona.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun fazerLogin(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) {
            Toast.makeText(this, "Preencha e-mail e senha", Toast.LENGTH_SHORT).show()
            return
        }

        val api = ApiClient.getService(this)
        val activityContext = this
        binding.botaoLogin.isEnabled = false

        lifecycleScope.launch {
            try {
                val resp = api.login(BodyLogin(email, senha))

                if (resp.isSuccessful && resp.body() != null) {
                    val body = resp.body()!!

                    SecaoUsuario(activityContext).salvarUsuario(
                        userId = body.IdUsuario,
                        email = body.Email,
                        nome = body.NomeUsuario ?: email.substringBefore("@"),
                        token = body.Token,
                        notificacoesAtivas = body.NotificacoesAtivas ?: true
                    )

                    val intent = Intent(activityContext, MainActivity::class.java)
                    intent.putExtra("USER_ID", body.IdUsuario)
                    startActivity(intent)
                    finish()
                } else {
                    val errorStr = resp.errorBody()?.string()
                    val msg = try {
                        JSONObject(errorStr ?: "").optString("response", "E-mail ou senha incorretos")
                    } catch (_: Exception) {
                        if (resp.code() == 401) "E-mail ou senha incorretos" else "Não foi possível entrar (HTTP ${resp.code()})"
                    }
                    Toast.makeText(activityContext, msg, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    activityContext,
                    "Não foi possível conectar ao servidor. Verifique sua conexão.",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                binding.botaoLogin.isEnabled = true
            }
        }
    }
}
