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

        binding.botaoLogin.setOnClickListener {
            val intent = Intent(this, TelaUsuario::class.java)
            startActivity(intent)
        }

    }
}