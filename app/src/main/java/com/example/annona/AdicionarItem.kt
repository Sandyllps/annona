package com.example.annona

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.annona.databinding.ActivityAdicionarItemBinding
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Esta tela é responsável por cadastrar um item na despensa do usuário.
 * Ela consome a API para sugerir nomes de alimentos e salvar o novo item no banco de dados.
 */
class AdicionarItem : AppCompatActivity() {

    // O 'binding' permite acessar os componentes do XML (botões, campos de texto) sem usar 'findViewById'
    private lateinit var binding: ActivityAdicionarItemBinding

    // Guarda a data escolhida no calendário para ser enviada ao banco de dados
    private var dataVencimentoSelecionadaMs: Long = 0

    // [DADOS DA API] Lista que armazena os objetos 'ModeloItemSugestao' (nome, categoria e imagem) vindos do Node.js
    private var listaDeSugestoesBackend: List<ModeloItemSugestao> = emptyList()

    // [DADOS DA API] Nome do arquivo de imagem (ex: "arroz.png") que será persistido no banco de dados
    private var imagemSelecionada: String = "item_padrao.png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdicionarItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura a barra superior (Toolbar) com o botão de voltar
        setSupportActionBar(binding.toolbarAdicionar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarAdicionar.setNavigationOnClickListener { finish() }

        //CONSUMO DE API: BUSCA DE SUGESTÕES
        // Assim que a tela é criada, chamamos o servidor para baixar a lista de itens padrão
        buscarSugestoesDoServidor()

        //LÓGICA DO AUTOCOMPLETE
        // Quando o usuário clica em um nome sugerido (ex: "Milho"), preenchemos os outros campos automaticamente
        binding.autoNomeAlimento.setOnItemClickListener { parent, _, position, _ ->
            val nomeClicado = parent.getItemAtPosition(position).toString()

            // Filtrei a lista local pra achar o objeto completo correspondente ao nome clicado
            val itemEncontrado = listaDeSugestoesBackend.find { it.nome == nomeClicado }

            if (itemEncontrado != null) {
                // 1. O seletor de categoria (Spinner) muda automaticamente para a categoria do item
                binding.spinnerCategoria.setSelection(itemEncontrado.id_categoria)

                // 2. Atualizamos a variável com a imagem real (ex: "enlatado_milho.png") vinda da API
                imagemSelecionada = itemEncontrado.imagem

                // 3. Disparamos a função que mostra a dica de conservação baseada na nova categoria
                atualizarDica()

                Toast.makeText(this, "Item reconhecido pelo sistema!", Toast.LENGTH_SHORT).show()
            }
        }

        // Funções que organizam a interface (Spinners e Calendário)
        configurarListasDeEscolha()
        configurarValidadeECalendario()

        // Clique no botão principal para validar os dados e enviar para a API
        binding.btnAdicionarItem.setOnClickListener {
            validarESalvar()
        }
    }


     //FUNÇÃO DE CONSUMO DE API (GET):
     //Conecta ao Node.js para buscar a lista de alimentos sugeridos.
    private fun buscarSugestoesDoServidor() {
        // Configuramos o Retrofit com a URL base e o conversor de JSON.
        val urlApi = this.getString(R.string.api_base_url)
        val retrofit = Retrofit.Builder()
            .baseUrl(urlApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ServicoApi::class.java)

        // O 'lifecycleScope.launch' inicia uma Coroutine (tarefa em segundo plano)
        // Isso evita que o aplicativo trave (congele) enquanto espera a resposta da internet
        lifecycleScope.launch {
            try {
                // Realiza a chamada para a rota 'itens_padrao' definida na classe ServicoApi.
                val resposta = api.listarItensPadrao()

                // Armazena a lista de objetos vinda do JSON na variável global
                listaDeSugestoesBackend = resposta.listaDeSugestoes

                // Mapeia apenas os nomes (Strings) para que o AutoCompleteTextView possa exibi-los
                val apenasNomes = listaDeSugestoesBackend.map { it.nome }

                // O Adapter serve para "desenhar" a lista de nomes dentro do campo de texto
                val adapterSugestao = ArrayAdapter(this@AdicionarItem, android.R.layout.simple_dropdown_item_1line, apenasNomes)
                binding.autoNomeAlimento.setAdapter(adapterSugestao)

            } catch (e: Exception) {
                // Se o servidor estiver fora do ar ou o IP estiver errado, cai aqui
                println("Erro ao buscar sugestões: ${e.message}")
            }
        }
    }


     // Dicas de conservação baseadas na categoria selecionada.
    private fun atualizarDica() {
        val categoriaSelecionada = binding.spinnerCategoria.selectedItem.toString()
        if (binding.spinnerCategoria.selectedItemPosition > 0) {
            binding.txtDicaCategoria.visibility = View.VISIBLE

            // O 'when' é uma forma mais limpa de fazer vários 'if/else'
            when (categoriaSelecionada) {
                "Bebidas" -> binding.txtDicaCategoria.text = "Mantenha em local fresco e longe da luz direta."
                "Carnes e Peixes" -> binding.txtDicaCategoria.text = "Mantenha no congelador se não for consumir em 24h."
                "Enlatados" -> binding.txtDicaCategoria.text = "Após aberto, guarde na geladeira."
                "Grãos" -> binding.txtDicaCategoria.text = "Guarde em potes bem fechados, em local seco e longe da luz."
                "Hortifruti" -> binding.txtDicaCategoria.text = "Lave bem antes de guardar."
                "Pães, Massas e Doces" -> binding.txtDicaCategoria.text = "Mantenha em local fresco e longe da umidade."
                "Laticínios" -> binding.txtDicaCategoria.text = "Guarde na geladeira, nas prateleiras superiores onde é mais frio."
                "Ovos" -> binding.txtDicaCategoria.text = "Guarde na geladeira."
                "Higiene, Perfumaria e Farmacia" -> binding.txtDicaCategoria.text = "Mantenha longe do calor."
                "Limpeza" -> binding.txtDicaCategoria.text = "Mantenha em local arejado."
                else -> binding.txtDicaCategoria.text = "Verifique o melhor local para armazenamento 😊"
            }
        } else {
            binding.txtDicaCategoria.visibility = View.GONE
        }
    }

    //VALIDAÇÃO: Garante que o usuário preencheu os campos mínimos antes de enviar ao servidor.
    private fun validarESalvar() {
        val nome = binding.autoNomeAlimento.text.toString().trim()
        val categoriaPosicao = binding.spinnerCategoria.selectedItemPosition
        val estadoPosicao = binding.spinnerEstado.selectedItemPosition
        val localPosicao = binding.spinnerLocal.selectedItemPosition
        val possuiData = binding.checkValidade.isChecked

        if (nome.isEmpty()) {
            binding.autoNomeAlimento.error = "O nome é obrigatório"
            return
        }
        if (categoriaPosicao == 0 || estadoPosicao == 0 || localPosicao == 0) {
            Toast.makeText(this, "Preencha todos os campos de seleção", Toast.LENGTH_SHORT).show()
            return
        }
        if (possuiData && dataVencimentoSelecionadaMs == 0L) {
            Toast.makeText(this, "Selecione a data de validade no calendário", Toast.LENGTH_SHORT).show()
            return
        }

        // Se passar por todas as validações, chama a função que faz o POST
        enviarParaOBanco()
    }

    //FUNÇÃO DE CONSUMO DE API (POST):
     //Envia o novo item para ser salvo definitivamente no banco de dados.
    private fun enviarParaOBanco() {
        // Recuperamos o ID do usuário logado através da sessão, por isso criei a classe SecaoUsuario.
        val secaoUsuario = SecaoUsuario(this)
        val userId = secaoUsuario.obterUserId() ?: 0

        // Criei o 'BodyCriarItem', que é o "pacote" de dados que o Node.js espera receber (a ideia era fazer de uma forma mais fácil mas não deu)
        val itemNovo = BodyCriarItem(
            userId,
            binding.autoNomeAlimento.text.toString(),
            Categoria.obterPeloNome(binding.spinnerCategoria.selectedItem.toString()).id_categoria,
            imagemSelecionada, // Envia o nome da imagem (arroz.png) descoberto pelo Autocomplete
            binding.spinnerEstado.selectedItem.toString() == "Consumindo",
            binding.checkValidade.isChecked,
            dateToString(obterDataValidade(binding), "yyyy-MM-dd"),
            LocalArmazenamento.obterPeloNome(binding.spinnerLocal.selectedItem.toString()).idLocal,
            binding.editQuantidade.text.toString().toIntOrNull() ?: 0,
            Medida.obterPeloNome(binding.spinnerMedida.selectedItem.toString()).idMedida,
            binding.editPreco.text.toString().toDoubleOrNull() ?: 0.0,
            1 // ID da flag inicial (será recalculado pela regra da despensa)
        )

        val urlApi = this.getString(R.string.api_base_url)
        val retrofit = Retrofit.Builder().baseUrl(urlApi).addConverterFactory(GsonConverterFactory.create()).build()
        val api = retrofit.create(ServicoApi::class.java)

        lifecycleScope.launch {
            try {
                // Envia os dados para a rota 'users/itensUsuario'
                val resp = api.criarItemUsuario(itemNovo)

                if (resp.isSuccessful) {
                    // Aqui avisamos a tela da Despensa para recarregar a lista automaticamente
                    LocalBroadcastManager.getInstance(this@AdicionarItem).sendBroadcast(Intent("ATUALIZAR_LISTA_ANONA"))

                    Toast.makeText(this@AdicionarItem, "Alimento salvo com sucesso!", Toast.LENGTH_SHORT).show()
                    finish() // Fecha a tela de cadastro e volta para a lista
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdicionarItem, "Erro ao conectar com o servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //FUNÇÕES DE INTERFACE/CONFIGURAÇÕES TÉCNICAS

    private fun configurarListasDeEscolha() {
        // Preenchemos os Spinners com as opções de texto
        val categorias = listOf("Selecione uma categoria", "Bebidas", "Carnes e Peixes", "Enlatados", "Grãos", "Hortifruti", "Pães, Massas e Doces", "Laticínios", "Ovos", "Higiene, Perfumaria e Farmacia", "Limpeza")
        binding.spinnerCategoria.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Listener que "escuta" quando o usuário muda a categoria manualmente para trocar a dica
        binding.spinnerCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) { atualizarDica() }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val locais = listOf("Selecione o local", "Geladeira", "Freezer", "Fruteira", "Armário", "Banheiro", "Lavanderia", "Outro")
        binding.spinnerLocal.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locais).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        val estados = listOf("Selecione o estado", "Consumindo", "Por consumir")
        binding.spinnerEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        val medidas = listOf("unidade", "kg", "g", "ml", "l", "caixa", "pacote")
        binding.spinnerMedida.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, medidas).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }

    private fun configurarValidadeECalendario() {
        // Bloqueia o campo de data de validade se o usuario não clicar na caixinha checkbox, ou libera o campo se ele selecionar.
        binding.editDataExibicao.isEnabled = false
        binding.checkValidade.setOnCheckedChangeListener { _, isChecked ->
            binding.editDataExibicao.isEnabled = isChecked
            if (!isChecked) {
                binding.editDataExibicao.setText("")
                dataVencimentoSelecionadaMs = 0
            }
        }
        // Abre o seletor de data de validade (Calendário nativo) ao clicar no campo
        binding.editDataExibicao.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                val dataFormatada = String.format("%02d/%02d/%d", day, month + 1, year)
                binding.editDataExibicao.setText(dataFormatada)

                // Converte a data para milissegundos para facilitar cálculos de prazo
                val cal = Calendar.getInstance().apply { set(year, month, day, 23, 59, 59) }
                dataVencimentoSelecionadaMs = cal.timeInMillis
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    //AUXILIAR: Transforma o texto do campo de data em um objeto 'Date'
    private fun obterDataValidade(binding: ActivityAdicionarItemBinding): Date? {
        val str = binding.editDataExibicao.text.toString()
        if (str.isBlank()) return null
        return try { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(str) } catch (e: Exception) { null }
    }

    //AUXILIAR: Transforma um objeto 'Date' em String formatada para o Banco de Dados (yyyy-MM-dd)
    private fun dateToString(date: Date?, pattern: String): String? {
        if (date == null) return null
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }
}