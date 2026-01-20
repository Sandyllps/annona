package com.example.annona

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AdicionarItem : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_item)

        val spinnerCategoria = findViewById<Spinner>(R.id.spinner_categoria)
        val campoNomeAlimento = findViewById<AutoCompleteTextView>(R.id.auto_nome_alimento)
        val txtDica = findViewById<TextView>(R.id.txt_dica_categoria)

        // Lista de categorias
        val categorias = listOf(
            "Selecione uma categoria", "Bebidas", "Carnes e Peixes", "Enlatados",
            "Grãos", "Hortifruti", "Pães, Massas e Doces", "Laticínios",
            "Ovos", "Higiene, Perfumaria e Farmacia", "Limpeza"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adapter

        // 3. Função que decide qual frase mostrar de acordo com o que usuario escolher em categoria
        fun atualizarDica() {
            val categoriaSelecionada = spinnerCategoria.selectedItem.toString()
            val textoDigitado = campoNomeAlimento.text.toString().trim().lowercase()

            if (spinnerCategoria.selectedItemPosition > 0) {
                txtDica.visibility = View.VISIBLE

                if (categoriaSelecionada == "Hortifruti") {
                    if (textoDigitado.contains("tomate")) {
                        txtDica.text = "Dica: Armazene na fruteira. Se estiver muito maduro, armazene na geladeira."
                    }
                    else if (textoDigitado.contains("alface") || textoDigitado.contains("berinjela") ||
                        textoDigitado.contains("brocolis") || textoDigitado.contains("cenoura") ||
                        textoDigitado.contains("maça")) {
                        txtDica.text = "Dica: Lave bem e seque antes de guardar na gaveta inferior da geladeira."
                    }
                    else if (textoDigitado.contains("banana") || textoDigitado.contains("batata") ||
                        textoDigitado.contains("cebola")) {
                        txtDica.text = "Dica: Armazene na Fruteira."
                    }
                    else {
                        txtDica.text = "Dica: Verifique o melhor local para armazenamento 😊"
                    }

                } else if (categoriaSelecionada == "Carnes e Peixes") {
                    txtDica.text = "Dica: Mantenha no congelador se não for consumir em 24h."
                } else if (categoriaSelecionada == "Laticínios") {
                    txtDica.text = "Dica: Guarde nas prateleiras superiores onde é mais frio."
                } else if (categoriaSelecionada == "Pães, Massas e Doces") {
                    txtDica.text = "Dica: Mantenha em local fresco e longe da umidade."
                } else if (categoriaSelecionada == "Ovos") {
                    txtDica.text = "Dica: Guarde na geladeira."
                } else if (categoriaSelecionada == "Grãos") {
                    txtDica.text = "Dica: Guarde em potes bem fechados, em local seco e longe da luz."
                } else {
                    txtDica.text = "Dica: Verifique o melhor local para armazenamento 😊"
                }
            } else {
                txtDica.visibility = View.GONE
            }
        }

        // 4. GATILHO: Diz ao Spinner para rodar a função acima quando o usuário clicar
        spinnerCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                atualizarDica()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }
}