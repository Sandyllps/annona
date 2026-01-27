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

class AdicionarItem : AppCompatActivity() {

    private lateinit var binding: ActivityAdicionarItemBinding
    private var dataVencimentoSelecionadaMs: Long = 0

    // [NOVO] Aqui vamos guardar a lista de itens que o seu Node.js vai enviar
    private var listaDeSugestoesBackend: List<ModeloItemSugestao> = emptyList()

    // [NOVO] Guarda o nome da imagem que vamos enviar para o banco (ex: arroz.png)
    // Começa com o nome padrão, caso o usuário digite algo que não está na lista
    private var imagemSelecionada: String = "item_padrao.png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdicionarItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuração da Toolbar e botão voltar
        setSupportActionBar(binding.toolbarAdicionar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarAdicionar.setNavigationOnClickListener { finish() }

        // --- [PASSO 4] CHAMADA PARA BUSCAR OS ITENS DO BACKEND ---
        // Assim que a tela abre, ela "liga" para o servidor para buscar os nomes
        buscarSugestoesDoServidor()

        // --- [PASSO 5] LÓGICA DO AUTOCOMPLETE (QUANDO O USUÁRIO CLICA) ---
        binding.autoNomeAlimento.setOnItemClickListener { parent, _, position, _ ->
            // Descobre qual nome foi clicado (ex: "Milho em Conserva")
            val nomeClicado = parent.getItemAtPosition(position).toString()

            // Procura na nossa lista qual objeto tem esse nome para pegarmos a categoria e a imagem
            val itemEncontrado = listaDeSugestoesBackend.find { it.nome == nomeClicado }

            if (itemEncontrado != null) {
                // 1. Muda o Spinner de categoria sozinho (ex: pula para "Enlatados")
                binding.spinnerCategoria.setSelection(itemEncontrado.id_categoria)

                // 2. Guarda o nome da imagem correta (ex: enlatados_milho.png)
                imagemSelecionada = itemEncontrado.imagem

                // 3. Atualiza a dica de conservação automaticamente
                atualizarDica()

                Toast.makeText(this, "Sugestão aplicada!", Toast.LENGTH_SHORT).show()
            }
        }

        // Configuração dos Spinners (Categorias, Locais, etc)
        configurarListasDeEscolha()

        // Lógica do Checkbox de Validade e Calendário
        configurarValidadeECalendario()

        // Botão Salvar
        binding.btnAdicionarItem.setOnClickListener {
            validarESalvar()
        }
    }

    // [NOVO] Função simples que busca os dados no seu Node.js
    private fun buscarSugestoesDoServidor() {
        val urlApi = this.getString(R.string.api_base_url)
        val retrofit = Retrofit.Builder()
            .baseUrl(urlApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ServicoApi::class.java)

        lifecycleScope.launch {
            try {
                // Chama a rota que você testou no Postman
                val resposta = api.listarItensPadrao()

                // Guarda a lista que chegou na nossa variável de memória
                listaDeSugestoesBackend = resposta.listaDeSugestoes

                // Cria uma lista só com os nomes para mostrar nas sugestões do campo de texto
                val apenasNomes = listaDeSugestoesBackend.map { it.nome }

                // Avisa o campo de texto (AutoComplete) quais nomes ele deve sugerir
                val adapterSugestao = ArrayAdapter(this@AdicionarItem, android.R.layout.simple_dropdown_item_1line, apenasNomes)
                binding.autoNomeAlimento.setAdapter(adapterSugestao)

            } catch (e: Exception) {
                // Se o servidor estiver desligado, o app apenas não mostra sugestões, mas não trava
            }
        }
    }

    // --- FUNÇÕES DE APOIO (ORGANIZAÇÃO DO CÓDIGO) ---

    private fun atualizarDica() {
        val categoriaSelecionada = binding.spinnerCategoria.selectedItem.toString()
        if (binding.spinnerCategoria.selectedItemPosition > 0) {
            binding.txtDicaCategoria.visibility = View.VISIBLE
            // Dicas baseadas na categoria
            when (categoriaSelecionada) {
                "Bebidas" -> binding.txtDicaCategoria.text = "Mantenha em local fresco e longe da luz direta."
                "Carnes e Peixes" -> binding.txtDicaCategoria.text = "Mantenha no congelador se não for consumir em 24h."
                "Enlatados" -> binding.txtDicaCategoria.text = "Após aberto, guarde na geladeira."
                "Grãos" -> binding.txtDicaCategoria.text = "Guarde em potes bem fechados, em local seco e longe da luz."
                "Hortifruti" -> binding.txtDicaCategoria.text = "Lave bem antes de guardar."
                "Pães, Massas e Doces" -> binding.txtDicaCategoria.text = "Mantenha em local fresco e longe da umidade."
                "Laticínios" -> binding.txtDicaCategoria.text = "Guarde nas prateleiras superiores onde é mais frio."
                "Ovos" -> binding.txtDicaCategoria.text = "Guarde na geladeira."
                "Higiene, Perfumaria e Farmacia" -> binding.txtDicaCategoria.text = "Mantenha longe do calor."
                "Limpeza" -> binding.txtDicaCategoria.text = "Mantenha em local arejado."
                else -> binding.txtDicaCategoria.text = "Verifique o melhor local para armazenamento 😊"
            }
        } else {
            binding.txtDicaCategoria.visibility = View.GONE
        }
    }

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
            Toast.makeText(this, "Selecione a data de validade", Toast.LENGTH_SHORT).show()
            return
        }

        enviarParaOBanco()
    }

    private fun enviarParaOBanco() {
        val secaoUsuario = SecaoUsuario(this)
        val userId = secaoUsuario.obterUserId() ?: 0

        // Criamos o objeto que será enviado para o servidor
        val itemNovo = BodyCriarItem(
            userId,
            binding.autoNomeAlimento.text.toString(),
            Categoria.obterPeloNome(binding.spinnerCategoria.selectedItem.toString()).id_categoria,
            imagemSelecionada, // Envia o nome da imagem que o Autocomplete descobriu!
            binding.spinnerEstado.selectedItem.toString() == "Consumindo",
            binding.checkValidade.isChecked,
            dateToString(obterDataValidade(binding), "yyyy-MM-dd"),
            LocalArmazenamento.obterPeloNome(binding.spinnerLocal.selectedItem.toString()).idLocal,
            binding.editQuantidade.text.toString().toIntOrNull() ?: 0,
            Medida.obterPeloNome(binding.spinnerMedida.selectedItem.toString()).idMedida,
            binding.editPreco.text.toString().toDoubleOrNull() ?: 0.0,
            1
        )

        val urlApi = this.getString(R.string.api_base_url)
        val retrofit = Retrofit.Builder().baseUrl(urlApi).addConverterFactory(GsonConverterFactory.create()).build()
        val api = retrofit.create(ServicoApi::class.java)

        lifecycleScope.launch {
            try {
                val resp = api.criarItemUsuario(itemNovo)
                if (resp.isSuccessful) {
                    // Avisa a tela principal para atualizar a lista
                    LocalBroadcastManager.getInstance(this@AdicionarItem).sendBroadcast(Intent("ATUALIZAR_LISTA_ANONA"))
                    Toast.makeText(this@AdicionarItem, "Sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdicionarItem, "Erro de conexão", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- FUNÇÕES DE CONFIGURAÇÃO INICIAL ---

    private fun configurarListasDeEscolha() {
        val categorias = listOf("Selecione uma categoria", "Bebidas", "Carnes e Peixes", "Enlatados", "Grãos", "Hortifruti", "Pães, Massas e Doces", "Laticínios", "Ovos", "Higiene, Perfumaria e Farmacia", "Limpeza")
        binding.spinnerCategoria.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
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
        binding.editDataExibicao.isEnabled = false
        binding.checkValidade.setOnCheckedChangeListener { _, isChecked ->
            binding.editDataExibicao.isEnabled = isChecked
            if (!isChecked) { binding.editDataExibicao.setText(""); dataVencimentoSelecionadaMs = 0 }
        }
        binding.editDataExibicao.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                val dataFormatada = String.format("%02d/%02d/%d", day, month + 1, year)
                binding.editDataExibicao.setText(dataFormatada)
                val cal = Calendar.getInstance().apply { set(year, month, day, 23, 59, 59) }
                dataVencimentoSelecionadaMs = cal.timeInMillis
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun obterDataValidade(binding: ActivityAdicionarItemBinding): Date? {
        val str = binding.editDataExibicao.text.toString()
        if (str.isBlank()) return null
        return try { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(str) } catch (e: Exception) { null }
    }

    private fun dateToString(date: Date?, pattern: String): String? {
        if (date == null) return null
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }
}