package com.example.annona

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.annona.databinding.ActivityTelaCadastroBinding
import kotlinx.coroutines.launch
import org.json.JSONObject

class TelaCadastro : AppCompatActivity() {

    private lateinit var binding: ActivityTelaCadastroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVoltarLogin.setOnClickListener {
            finish()
        }

        binding.botaoCadastrar.setOnClickListener {
            realizarCadastro()
        }
    }

    private fun realizarCadastro() {
        val nome = binding.etNomeCadastro.text.toString().trim()
        val email = binding.etEmailCadastro.text.toString().trim()
        val senha = binding.etSenhaCadastro.text.toString()
        val confirmarSenha = binding.etConfirmarSenhaCadastro.text.toString()

        if (nome.isBlank() || email.isBlank() || senha.isBlank() || confirmarSenha.isBlank()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailCadastro.error = "Informe um e-mail válido"
            return
        }

        if (senha.length < 3) {
            binding.etSenhaCadastro.error = "A senha deve ter pelo menos 3 caracteres"
            return
        }

        if (senha != confirmarSenha) {
            binding.etConfirmarSenhaCadastro.error = "As senhas não coincidem"
            return
        }

        val api = ApiClient.getService(this)
        binding.botaoCadastrar.isEnabled = false

        lifecycleScope.launch {
            try {
                val resp = api.cadastrarUsuario(BodyCadastro(nome, email, senha))
                if (resp.isSuccessful && resp.body() != null) {
                    Toast.makeText(
                        this@TelaCadastro,
                        "Conta criada com sucesso! Faça login para entrar.",
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent().apply {
                        putExtra("EMAIL_CADASTRADO", email)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    val erroStr = resp.errorBody()?.string()
                    val mensagem = try {
                        JSONObject(erroStr ?: "").optString("response", "Não foi possível criar a conta")
                    } catch (_: Exception) {
                        if (resp.code() == 409) "Este e-mail já está cadastrado" else "Erro ao criar conta (HTTP ${resp.code()})"
                    }
                    Toast.makeText(this@TelaCadastro, mensagem, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@TelaCadastro,
                    "Sem conexão com o servidor. Verifique sua internet ou a URL nos Ajustes.",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                binding.botaoCadastrar.isEnabled = true
            }
        }
    }
}

