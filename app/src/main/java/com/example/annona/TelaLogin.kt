package com.example.annona

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.annona.databinding.ActivityTelaLoginBinding

class TelaLogin : AppCompatActivity() {
    private lateinit var binding: ActivityTelaLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botão de login sem verificação
        binding.botaoLogin.setOnClickListener {
            // Direciona direto para a MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // opcional, fecha a tela de login
        }
    }
}