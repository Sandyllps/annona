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
 * Esta tela é responsável por cadastrar ou editar um item na despensa do usuário.
 * Ela consome a API para sugerir nomes de alimentos e salvar/atualizar no banco de dados.
 */
class AdicionarItem : AppCompatActivity() {

    private lateinit var binding: ActivityAdicionarItemBinding
    private var dataVencimentoSelecionadaMs: Long = 0
    private var listaDeSugestoesBackend: List<ModeloItemSugestao> = emptyList()
    private var imagemSelecionada: String = "item_padrao.png"

    // [EDIÇÃO] Variável para controlar se estamos editando (-1 significa novo item)
    private var idItemEdicao: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdicionarItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAdicionar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarAdicionar.setNavigationOnClickListener { finish() }

        buscarSugestoesDoServidor()

        // Configura as listas e calendários antes de preencher os dados de edição
        configurarListasDeEscolha()
        configurarValidadeECalendario()

        // [EDIÇÃO] Verifica se recebemos dados para editar
        verificarSeEhEdicao()

        binding.autoNomeAlimento.setOnItemClickListener { parent, _, position, _ ->
            val nomeClicado = parent.getItemAtPosition(position).toString()
            val itemEncontrado = listaDeSugestoesBackend.find { it.nome == nomeClicado }
            if (itemEncontrado != null) {
                binding.spinnerCategoria.setSelection(itemEncontrado.id_categoria)
                imagemSelecionada = itemEncontrado.imagem
                atualizarDica()
            }
        }

        binding.btnAdicionarItem.setOnClickListener {
            validarESalvar()
        }
    }

    // [EDIÇÃO] Função simples para identificar se a tela deve carregar dados de um item existente
    private fun verificarSeEhEdicao() {
        val idRecebido = intent.getLongExtra("ITEM_ID", -1L)
        if (idRecebido != -1L) {
            idItemEdicao = idRecebido

            // Muda os textos da interface
            binding.btnAdicionarItemTopo.text = "Editar Item"
            binding.btnAdicionarItem.text = "Salvar Alterações"

            // Preenche os campos de texto com o que veio da tela anterior
            binding.autoNomeAlimento.setText(intent.getStringExtra("NOME"))
            binding.editQuantidade.setText(intent.getIntExtra("QUANTIDADE", 0).toString())
            binding.editPreco.setText(intent.getDoubleExtra("PRECO", 0.0).toString())

            // Seleciona a categoria correta no Spinner
            val categoriaId = intent.getIntExtra("CATEGORIA_ID", 0)
            binding.spinnerCategoria.setSelection(categoriaId)

            // Seleciona o local e estado (Comparando Strings)
            selecionarSpinnerPorTexto(binding.spinnerLocal, intent.getStringExtra("LOCAL"))
            selecionarSpinnerPorTexto(binding.spinnerEstado, if(intent.getBooleanExtra("CONSUMINDO", false)) "Consumindo" else "Por consumir")

            // Se tiver validade, preenche o campo de data
            val dataVal = intent.getStringExtra("DATA_VALIDADE")
            if (!dataVal.isNullOrBlank()) {
                binding.checkValidade.isChecked = true
                binding.editDataExibicao.setText(dataVal)
            }

            imagemSelecionada = intent.getStringExtra("IMAGEM") ?: "item_padrao.png"
        }
    }

    // Auxiliar para selecionar um item no Spinner baseado no nome
    private fun selecionarSpinnerPorTexto(spinner: Spinner, texto: String?) {
        val adapter = spinner.adapter as ArrayAdapter<String>
        val posicao = adapter.getPosition(texto)
        if (posicao >= 0) spinner.setSelection(posicao)
    }

    private fun buscarSugestoesDoServidor() {
        val urlApi = this.getString(R.string.api_base_url)
        val retrofit = Retrofit.Builder().baseUrl(urlApi).addConverterFactory(GsonConverterFactory.create()).build()
        val api = retrofit.create(ServicoApi::class.java)

        lifecycleScope.launch {
            try {
                val resposta = api.listarItensPadrao()
                listaDeSugestoesBackend = resposta.listaDeSugestoes
                val apenasNomes = listaDeSugestoesBackend.map { it.nome }
                val adapterSugestao = ArrayAdapter(this@AdicionarItem, android.R.layout.simple_dropdown_item_1line, apenasNomes)
                binding.autoNomeAlimento.setAdapter(adapterSugestao)
            } catch (e: Exception) {
                println("Erro ao buscar sugestões: ${e.message}")
            }
        }
    }

    private fun atualizarDica() {
        val categoriaSelecionada = binding.spinnerCategoria.selectedItem.toString()
        if (binding.spinnerCategoria.selectedItemPosition > 0) {
            binding.txtDicaCategoria.visibility = View.VISIBLE
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

    private fun validarESalvar() {
        val nome = binding.autoNomeAlimento.text.toString().trim()
        if (nome.isEmpty()) {
            binding.autoNomeAlimento.error = "O nome é obrigatório"
            return
        }
        if (binding.spinnerCategoria.selectedItemPosition == 0 || binding.spinnerEstado.selectedItemPosition == 0 || binding.spinnerLocal.selectedItemPosition == 0) {
            Toast.makeText(this, "Preencha todos os campos de seleção", Toast.LENGTH_SHORT).show()
            return
        }
        enviarParaOBanco()
    }

    private fun enviarParaOBanco() {
        val secaoUsuario = SecaoUsuario(this)
        val userId = secaoUsuario.obterUserId() ?: 0

        val itemNovo = BodyCriarItem(
            userId,
            binding.autoNomeAlimento.text.toString(),
            Categoria.obterPeloNome(binding.spinnerCategoria.selectedItem.toString()).id_categoria,
            imagemSelecionada,
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
                // [EDIÇÃO] Lógica simples: Se temos um ID, usamos a rota de Editar (PUT), senão Criar (POST)
                val resp = if (idItemEdicao == -1L) {
                    api.criarItemUsuario(itemNovo)
                } else {
                    api.editarItemUsuario(idItemEdicao, itemNovo)
                }

                if (resp.isSuccessful) {
                    LocalBroadcastManager.getInstance(this@AdicionarItem).sendBroadcast(Intent("ATUALIZAR_LISTA_ANONA"))
                    Toast.makeText(this@AdicionarItem, "Sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdicionarItem, "Erro ao conectar com o servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarListasDeEscolha() {
        val categorias = listOf("Selecione uma categoria", "Bebidas", "Carnes e Peixes", "Enlatados", "Grãos", "Hortifruti", "Pães, Massas e Doces", "Laticínios", "Ovos", "Higiene, Perfumaria e Farmacia", "Limpeza")
        binding.spinnerCategoria.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.spinnerCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) { atualizarDica() }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val locais = listOf("Selecione o local", "Geladeira", "Freezer", "Fruteira", "Armário", "Banheiro", "Lavanderia")
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