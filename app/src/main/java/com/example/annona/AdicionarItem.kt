package com.example.annona

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.annona.databinding.ActivityAdicionarItemBinding // Ferramenta que facilita achar os componentes da tela
import java.util.Calendar

// Esta tela serve tanto para cadastrar um novo alimento quanto para editar um que já existe
class AdicionarItem : AppCompatActivity() {

    // O 'binding' é como um mapa da tela; com ele, acessamos botões e textos sem erro
    private lateinit var binding: ActivityAdicionarItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Prepara o mapa (binding) para que o código reconheça os itens do XML
        binding = ActivityAdicionarItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- 1. CONFIGURAÇÃO DAS LISTAS DE ESCOLHA (Spinners) ---

        // Lista de Categorias: Define o tipo do alimento (Grãos, Bebidas, etc.)
        val categorias = listOf(
            "Selecione uma categoria", "Bebidas", "Carnes e Peixes", "Enlatados",
            "Grãos", "Hortifruti", "Pães, Massas e Doces", "Laticínios",
            "Ovos", "Higiene, Perfumaria e Farmacia", "Limpeza"
        )
        // Coloca a lista de categorias dentro do seletor na tela
        binding.spinnerCategoria.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Lista de Locais: Onde o item será guardado (Geladeira, Armário, etc.)
        val locais = listOf("Selecione o local", "Geladeira", "Freezer", "Fruteira", "Armário", "Banheiro", "Lavanderia", "Outro")
        binding.spinnerLocal.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locais).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Lista de Estados: Se o usuário já abriu o item ou se está fechado
        val estados = listOf("Selecione o estado", "Consumindo", "Por consumir")
        binding.spinnerEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Lista de Medidas: Unidade, kg, litros, etc.
        val listaMedidas = listOf("unidade", "kg", "g", "ml", "l", "caixa", "pacote")
        binding.spinnerMedida.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaMedidas).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // --- LÓGICA DE EDIÇÃO (Caso o usuário queira mudar um item já cadastrado) ---
        // Verifica se veio um ID da tela anterior; se veio, significa que é EDIÇÃO
        val idExistente = intent.getLongExtra("ITEM_ID", -1L)
        if (idExistente != -1L) {
            // Pega o nome que veio da outra tela e escreve no campo de texto
            binding.autoNomeAlimento.setText(intent.getStringExtra("TITULO"))

            // Descobre qual local e estado o item tinha e marca na lista de escolha
            val localRec = intent.getStringExtra("LOCAL")
            val estadoRec = intent.getStringExtra("ESTADO")

            binding.spinnerLocal.setSelection(locais.indexOf(localRec).takeIf { it != -1 } ?: 0)
            binding.spinnerEstado.setSelection(estados.indexOf(estadoRec).takeIf { it != -1 } ?: 0)

            // Troca o texto do botão para o usuário saber que está editando e não criando do 0. Então ao invés de "Adicionar Item" aparece "Salvar Alterações".
            binding.btnAdicionarItem.text = "Salvar Alterações"
        }

        // --- 2. CALENDÁRIO (Para escolher a data de validade)  ---
        binding.editDataExibicao.setOnClickListener {
            val c = Calendar.getInstance()
            // Abre aquela janelinha de calendário nativa do Android
            val dpd = DatePickerDialog(this, { _, year, month, day ->
                // Formata a data para o padrão brasileiro: DD/MM/AAAA
                val dataFormatada = String.format("%02d/%02d/%d", day, month + 1, year)
                binding.editDataExibicao.setText(dataFormatada)
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            dpd.show()
        }

        // --- 3. DICAS AUTOMÁTICAS DE CONSERVAÇÃO (Regras de Negócio)
        fun atualizarDica() {
            val categoriaSelecionada = binding.spinnerCategoria.selectedItem.toString()
            val textoDigitado = binding.autoNomeAlimento.text.toString().trim().lowercase()

            // Se o usuário já escolheu uma categoria, o app tenta dar uma dica de conservação
            if (binding.spinnerCategoria.selectedItemPosition > 0) {
                binding.txtDicaCategoria.visibility = View.VISIBLE

                // Se for Hortifruti, o app dá dicas específicas baseadas no nome (ex: tomate, banana)
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
                }
                // Dicas para as outras categorias conforme o documento de requisitos
                else if (categoriaSelecionada == "Carnes e Peixes") {
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
                // Se não escolheu categoria, esconde o campo de dicas
                binding.txtDicaCategoria.visibility = View.GONE
            }
        }

        // --- 4. GATILHO PARA AS DICAS ---
        // Toda vez que o usuário trocar a categoria na lista, a dica muda automaticamente
        binding.spinnerCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                atualizarDica()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        // --- 5. CLIQUE NO BOTÃO ADICIONAR/SALVAR ---
        binding.btnAdicionarItem.setOnClickListener {
            // Pega tudo o que o usuário escreveu ou escolheu nos campos
            val nome = binding.autoNomeAlimento.text.toString().trim()
            val categoriaPosicao = binding.spinnerCategoria.selectedItemPosition
            val local = binding.spinnerLocal.selectedItem.toString()
            val quantidade = binding.editQuantidade.text.toString()
            val medida = binding.spinnerMedida.selectedItem.toString()
            val preco = binding.editPreco.text.toString()
            val data = binding.editDataExibicao.text.toString()

            // --- VALIDAÇÃO (Para evitar salvar campos vazios) ---
            var erro = false

            // O nome não pode estar em branco
            if (nome.isEmpty()) {
                binding.autoNomeAlimento.error = "O nome é obrigatório"
                erro = true
            }

            // O usuário DEVE escolher uma categoria (não pode deixar na opção inicial vazia, sem nome do item)
            if (categoriaPosicao == 0) {
                Toast.makeText(this, "Por favor, selecione uma categoria", Toast.LENGTH_SHORT).show()
                erro = true
            }

            // --- FINALIZAÇÃO ---
            if (!erro) {
                // Se estiver tudo certo, decide se a mensagem é de "cadastrado" ou "atualizado"
                val acao = if (idExistente == -1L) "cadastrado" else "atualizado"
                Toast.makeText(this, "$nome $acao com sucesso!", Toast.LENGTH_SHORT).show()

                // Fecha esta tela e volta para a despensa
                finish()
            }
        }
    }
}