package com.example.annona

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.annona.databinding.ActivityTelaLoginBinding

class TelaLogin : AppCompatActivity() {

    private lateinit var binding: ActivityTelaLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTelaLoginBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_tela_login)

    }
}