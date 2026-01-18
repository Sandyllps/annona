package com.example.annona

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.annona.databinding.ActivityTelaUsuarioBinding

class TelaUsuario : AppCompatActivity() {

    private lateinit var binding: ActivityTelaUsuarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTelaUsuarioBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.voltar.setOnClickListener {
            val intent = Intent(this, TelaLogin::class.java)
            startActivity(intent)
        }

    }
}