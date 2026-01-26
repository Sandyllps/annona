package com.example.annona

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.annona.databinding.ActivityAdicionarItemBinding // Ferramenta que facilita achar os componentes da tela
import java.util.Calendar
import java.util.concurrent.TimeUnit // Importado para ajudar no cálculo de dias

// Esta tela serve tanto para cadastrar um novo alimento quanto para editar um que já existe
class AdicionarItem : AppCompatActivity() {

    // O 'binding' é como um mapa da tela; com ele, acessamos botões e textos sem erro
    private lateinit var binding: ActivityAdicionarItemBinding

    // --- VARIÁVEIS PARA O CÁLCULO DE DATA ---
    private var dataVencimentoSelecionadaMs: Long = 0 // Armazena a data final em milissegundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Prepara o mapa (binding) para que o código reconheça os itens do XML
        binding = ActivityAdicionarItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- CONFIGURAÇÃO DA BARRA SUPERIOR (SETINHA DE VOLTAR) [novo] ---
        // Aqui configuramos a toolbar para aceitar o botão de "voltar" nativo
        setSupportActionBar(binding.toolbarAdicionar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Ativa a setinha no canto esquerdo
        supportActionBar?.setDisplayShowTitleEnabled(false) // Esconde o título padrão da Activity

        // Faz a setinha fechar a tela e voltar para a despensa quando clicada
        binding.toolbarAdicionar.setNavigationOnClickListener {
            finish()
        }

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

        // --- LÓGICA DO CHECKBOX DE VALIDADE ---
        // Se o checkbox estiver marcado, o campo de data fica habilitado; se não, fica bloqueado.
        binding.editDataExibicao.isEnabled = false // Começa bloqueado por padrão
        binding.checkValidade.setOnCheckedChangeListener { _, isChecked ->
            binding.editDataExibicao.isEnabled = isChecked
            if (!isChecked) {
                binding.editDataExibicao.setText("") // Limpa o texto se desmarcar
                dataVencimentoSelecionadaMs = 0
            }
        }

        // --- LÓGICA DE EDIÇÃO (Caso o usuário queira mudar um item já cadastrado) ---
        // Verifica se veio um ID da tela anterior; se veio, significa que é EDIÇÃO
        val idExistente = intent.getLongExtra("ITEM_ID", -1L)
        if (idExistente != -1L) {
            // AJUSTE: Muda o título no topo da página para "Editar" quando for edição [novo]
            binding.btnAdicionarItemTopo.text = "Editar"

            // Pega o nome que veio da outra tela e escreve no campo de texto
            binding.autoNomeAlimento.setText(intent.getStringExtra("TITULO"))

            // Descobre qual local e estado o item tinha e marca na lista de escolha
            val localRec = intent.getStringExtra("LOCAL")
            val estadoRec = intent.getStringExtra("ESTADO")

            binding.spinnerLocal.setSelection(locais.indexOf(localRec).takeIf { it != -1 } ?: 0)
            binding.spinnerEstado.setSelection(estados.indexOf(estadoRec).takeIf { it != -1 } ?: 0)

            // Troca o texto do botão para o usuário saber que está editando e não criando do 0. Então ao invés de "Adicionar Item" aparece "Salvar Alterações".
            binding.btnAdicionarItem.text = "Salvar Alterações"
        } else {
            // Se não for edição, garante que o título seja "Adicionar novo item" [novo]
            binding.btnAdicionarItemTopo.text = "Adicionar novo item"
        }

        // --- 2. CALENDÁRIO (Para escolher a data de validade)  ---
        binding.editDataExibicao.setOnClickListener {
            val c = Calendar.getInstance()
            // Abre aquela janelinha de calendário nativa do Android
            val dpd = DatePickerDialog(this, { _, year, month, day ->
                // Formata a data para o padrão brasileiro: DD/MM/AAAA
                val dataFormatada = String.format("%02d/%02d/%d", day, month + 1, year)
                binding.editDataExibicao.setText(dataFormatada)

                // --- ARMAZENAMENTO DA DATA EM MILISSEGUNDOS ---
                val calendarVencimento = Calendar.getInstance()
                calendarVencimento.set(year, month, day, 23, 59, 59)
                dataVencimentoSelecionadaMs = calendarVencimento.timeInMillis
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
            val categoriaTexto = binding.spinnerCategoria.selectedItem.toString()
            val local = binding.spinnerLocal.selectedItem.toString()
            val quantidade = binding.editQuantidade.text.toString()
            val medida = binding.spinnerMedida.selectedItem.toString()
            val preco = binding.editPreco.text.toString()
            val possuiData = binding.checkValidade.isChecked

            // --- VALIDAÇÃO (Para evitar salvar campos vazios) ---
            var erro = false

            // O nome não pode estar em branco
            if (nome.isEmpty()) {
                binding.autoNomeAlimento.error = "O nome é obrigatório"
                erro = true
            }

            // O usuário DEVE escolher uma categoria
            if (categoriaPosicao == 0) {
                Toast.makeText(this, "Por favor, selecione uma categoria", Toast.LENGTH_SHORT).show()
                erro = true
            }

            // Se marcou que possui data, mas não selecionou no calendário
            if (possuiData && dataVencimentoSelecionadaMs == 0L) {
                Toast.makeText(this, "Por favor, selecione a data no calendário", Toast.LENGTH_SHORT).show()
                erro = true
            }

            // --- CÁLCULO INTELIGENTE DE PRAZO (REGRA DA TABELA) ---
            if (!erro) {
                val dataFinalMs: Long
                val diasRestantes: Long
                val hojeMs = Calendar.getInstance().timeInMillis

                if (possuiData) {
                    // Se tem rotulo com data de validade, usa a data que o usuário adicionar
                    dataFinalMs = dataVencimentoSelecionadaMs
                } else {
                    // --- TABELA DE PRAZOS FIXOS (Simulação do BD com regras de conservação) ---
                    var prazoPadrao = when (categoriaTexto) {
                        "Hortifruti" -> 9
                        "Carnes e Peixes" -> 3
                        "Laticínios" -> 7
                        "Ovos" -> 20
                        "Pães, Massas e Doces" -> 5
                        "Bebidas" -> 30
                        else -> 15 // Prazo padrão para outras categorias
                    }

                    // --- REGRA DE INCREMENTO PARA CONGELAMENTO ---
                    // Se o item for guardado no Freezer, o prazo aumenta consideravelmente
                    if (local == "Freezer") {
                        if (categoriaTexto == "Carnes e Peixes") {
                            prazoPadrao = 90 // Carnes ganham 90 dias de validade no freezer
                        } else if (categoriaTexto == "Laticínios") {
                            prazoPadrao = 60 // Laticínios ganham 60 dias de validade no freezer
                        }
                    }

                    val cal = Calendar.getInstance()
                    cal.add(Calendar.DAY_OF_YEAR, prazoPadrao)
                    dataFinalMs = cal.timeInMillis
                }

                // Calcula a diferença entre a data final e hoje para exibir no card
                val diff = dataFinalMs - hojeMs
                diasRestantes = if (diff > 0) TimeUnit.MILLISECONDS.toDays(diff) else 0
                val prazoTexto = if (diasRestantes <= 0) "VENCIDO" else "$diasRestantes DIAS"

                // --- FINALIZAÇÃO ---
                val acao = if (idExistente == -1L) "cadastrado" else "atualizado"
                Toast.makeText(this, "$nome $acao com sucesso! Restam $prazoTexto", Toast.LENGTH_SHORT).show()

                // Fecha esta tela e volta para a despensa
                finish()
            }
        }
    }
}