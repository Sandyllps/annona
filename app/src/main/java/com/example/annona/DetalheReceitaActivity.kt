package com.example.annona

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.annona.databinding.ActivityDetalheReceitaBinding

class DetalheReceitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalheReceitaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetalheReceitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // botao voltar
        binding.btnVoltar.setOnClickListener {
            finish()
        }

        // dados da intent
        binding.txtNomeReceita.text =
            intent.getStringExtra("NOME")

        binding.txtTempoReceita.text =
            intent.getStringExtra("TEMPO")

        binding.txtTagReceita.text =
            intent.getStringExtra("TAG")

        val ingredientes =
            intent.getStringArrayListExtra("INGREDIENTES") ?: emptyList()

        binding.txtIngredientesReceita.text =
            ingredientes.joinToString(" • ")

    }

}
