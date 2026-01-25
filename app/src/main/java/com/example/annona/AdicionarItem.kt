package com.example.annona

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.annona.databinding.ActivityAdicionarItemBinding // Importante!
import java.util.Calendar

class AdicionarItem : AppCompatActivity() {

    // Declaração do binding
    private lateinit var binding: ActivityAdicionarItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialização do binding
        binding = ActivityAdicionarItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. CONFIGURAÇÃO DOS SPINNERS (Usando binding)

        // Categorias
        val categorias = listOf(
            "Selecione uma categoria", "Bebidas", "Carnes e Peixes", "Enlatados",
            "Grãos", "Hortifruti", "Pães, Massas e Doces", "Laticínios",
            "Ovos", "Higiene, Perfumaria e Farmacia", "Limpeza"
        )
        binding.spinnerCategoria.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Locais
        val locais = listOf("Selecione o local", "Geladeira", "Freezer", "Fruteira", "Armário", "Banheiro", "Lavanderia", "Outro")
        binding.spinnerLocal.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locais).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Estados
        val estados = listOf("Selecione o estado", "Consumindo", "Por consumir")
        binding.spinnerEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Medidas
        val listaMedidas = listOf("unidade", "kg", "g", "ml", "l", "caixa", "pacote")
        binding.spinnerMedida.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaMedidas).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // --- LÓGICA DE RECEBIMENTO DE DADOS PARA EDIÇÃO ---
        val idExistente = intent.getLongExtra("ITEM_ID", -1L)
        if (idExistente != -1L) {
            // Preenche o nome do alimento vindo da Intent
            binding.autoNomeAlimento.setText(intent.getStringExtra("TITULO"))

            // Recupera Local e Estado e seleciona a posição correta nos Spinners
            val localRec = intent.getStringExtra("LOCAL")
            val estadoRec = intent.getStringExtra("ESTADO")

            binding.spinnerLocal.setSelection(locais.indexOf(localRec).takeIf { it != -1 } ?: 0)
            binding.spinnerEstado.setSelection(estados.indexOf(estadoRec).takeIf { it != -1 } ?: 0)

            // Muda o texto do botão para indicar edição
            binding.btnAdicionarItem.text = "Salvar Alterações"
        }

        // 2. CALENDÁRIO NATIVO
        binding.editDataExibicao.setOnClickListener {
            val c = Calendar.getInstance()
            val dpd = DatePickerDialog(this, { _, year, month, day ->
                val dataFormatada = String.format("%02d/%02d/%d", day, month + 1, year)
                binding.editDataExibicao.setText(dataFormatada)
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            dpd.show()
        }

        fun atualizarDica() {
            val categoriaSelecionada = binding.spinnerCategoria.selectedItem.toString()
            val textoDigitado = binding.autoNomeAlimento.text.toString().trim().lowercase()

            if (binding.spinnerCategoria.selectedItemPosition > 0) {
                binding.txtDicaCategoria.visibility = View.VISIBLE

                if (categoriaSelecionada == "Hortifruti") {
                    if (textoDigitado.contains("tomate")) {
                        binding.txtDicaCategoria.text = "Armazene na fruteira. Se estiver muito maduro, armazene na geladeira."
                    }
                    else if (textoDigitado.contains("alface") || textoDigitado.contains("berinjela") ||
                        textoDigitado.contains("brocolis") || textoDigitado.contains("cenoura") ||
                        textoDigitado.contains("maça")) {
                        binding.txtDicaCategoria.text = "Lave bem e seque antes de guardar na gaveta inferior da geladeira."
                    }
                    else if (textoDigitado.contains("banana") || textoDigitado.contains("batata") ||
                        textoDigitado.contains("cebola")) {
                        binding.txtDicaCategoria.text = "Armazene na Fruteira."
                    }
                    else {
                        binding.txtDicaCategoria.text = "Verifique o melhor local para armazenamento 😊"
                    }
                } else if (categoriaSelecionada == "Carnes e Peixes") {
                    binding.txtDicaCategoria.text = "Mantenha no congelador se não for consumir em 24h."
                } else if (categoriaSelecionada == "Laticínios") {
                    binding.txtDicaCategoria.text = "Guarde nas prateleiras superiores onde é mais frio."
                } else if (categoriaSelecionada == "Pães, Massas e Doces") {
                    binding.txtDicaCategoria.text = "Mantenha em local fresco e longe da umidade."
                } else if (categoriaSelecionada == "Ovos") {
                    binding.txtDicaCategoria.text = "Guarde na geladeira."
                } else if (categoriaSelecionada == "Grãos") {
                    binding.txtDicaCategoria.text = "Guarde em potes bem fechados, em local seco e longe da luz."
                } else {
                    binding.txtDicaCategoria.text = "Verifique o melhor local para armazenamento 😊"
                }
            } else {
                binding.txtDicaCategoria.visibility = View.GONE
            }
        }

        // 4. GATILHO
        binding.spinnerCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                atualizarDica()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding.btnAdicionarItem.setOnClickListener {
            // 1. Captura os valores
            val nome = binding.autoNomeAlimento.text.toString().trim()
            val categoriaPosicao = binding.spinnerCategoria.selectedItemPosition
            val categoriaTexto = binding.spinnerCategoria.selectedItem.toString()

            // Outros dados para o seu futuro objeto/banco
            val local = binding.spinnerLocal.selectedItem.toString()
            val quantidade = binding.editQuantidade.text.toString()
            val medida = binding.spinnerMedida.selectedItem.toString()
            val preco = binding.editPreco.text.toString()
            val data = binding.editDataExibicao.text.toString()

            // 2. VALIDAÇÃO OBRIGATÓRIA
            var erro = false

            // Valida o Nome
            if (nome.isEmpty()) {
                binding.autoNomeAlimento.error = "O nome é obrigatório"
                erro = true
            }

            // Valida a Categoria (posição 0 é o "Selecione uma categoria")
            if (categoriaPosicao == 0) {
                // Mostra um aviso visual no Spinner
                Toast.makeText(this, "Por favor, selecione uma categoria", Toast.LENGTH_SHORT).show()
                erro = true
            }

            // 3. CONCLUSÃO DO CADASTRO
            if (!erro) {
                // Aqui você salvaria os dados no Banco ou enviaria via Intent
                // Ex: salvarNoBanco(nome, categoriaTexto, local, ...)

                // Ajuste na mensagem para diferenciar Cadastro de Edição
                val acao = if (idExistente == -1L) "cadastrado" else "atualizado"
                Toast.makeText(this, "$nome $acao com sucesso!", Toast.LENGTH_SHORT).show()

                // Finaliza e fecha a tela
                finish()
            }
        }
    }
}