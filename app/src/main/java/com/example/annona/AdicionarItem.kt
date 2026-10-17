package com.example.annona

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.annona.databinding.ActivityAdicionarItemBinding
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// A tela AdicionarItem é responsável por cadastrar ou editar um item na despensa do usuário.
class AdicionarItem : AppCompatActivity() {

    private lateinit var binding: ActivityAdicionarItemBinding
    private var dataVencimentoSelecionadaMs: Long = 0
    private var listaDeSugestoesBackend: List<ModeloItemSugestao> = emptyList()
    private var imagemSelecionada: String = "item_padrao.png"
    private var idItemEdicao: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Confere se há usuário logado (B08)
        val session = SecaoUsuario(this)
        if (!session.estaLogado()) {
            val intent = Intent(this, TelaLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityAdicionarItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAdicionar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarAdicionar.setNavigationOnClickListener { finish() }

        configurarListasDeEscolha()
        configurarValidadeECalendario()
        verificarSeEhEdicao()
        buscarSugestoesEInfoDoServidor()

        // Autocomplete de sugestões de itens padrão (RF08, RN06)
        binding.autoNomeAlimento.setOnItemClickListener { parent, _, position, _ ->
            val nomeClicado = parent.getItemAtPosition(position).toString()
            val itemEncontrado = listaDeSugestoesBackend.find { it.nome == nomeClicado }

            if (itemEncontrado != null) {
                val catObj = Categoria.obterPeloId(itemEncontrado.id_categoria)
                selecionarSpinnerPorTexto(binding.spinnerCategoria, catObj.nomeCategoria)
                imagemSelecionada = itemEncontrado.imagem
                atualizarDica()
            }
        }

        binding.btnAdicionarItem.setOnClickListener {
            validarESalvar()
        }
    }

    // Identifica se a tela foi aberta para editar um item existente (RF06, B02)
    private fun verificarSeEhEdicao() {
        val idRecebido = intent.getLongExtra("ITEM_ID", -1L)
        if (idRecebido != -1L) {
            idItemEdicao = idRecebido

            binding.btnAdicionarItemTopo.text = "Editar Item"
            binding.btnAdicionarItem.text = "Salvar Alterações"

            binding.autoNomeAlimento.setText(intent.getStringExtra("NOME"))

            // Preserva quantidade, preço e medida reais na edição (B02, B13)
            val qtd = intent.getDoubleExtra("QUANTIDADE", 0.0)
            val qtdTexto = if (qtd % 1.0 == 0.0) qtd.toInt().toString() else qtd.toString()
            binding.editQuantidade.setText(qtdTexto)

            val preco = intent.getDoubleExtra("PRECO", 0.0)
            binding.editPreco.setText(String.format(Locale.US, "%.2f", preco))

            val categoriaId = intent.getIntExtra("CATEGORIA_ID", 0)
            if (categoriaId > 0) {
                val catNome = Categoria.obterPeloId(categoriaId).nomeCategoria
                selecionarSpinnerPorTexto(binding.spinnerCategoria, catNome)
            }

            val localId = intent.getIntExtra("LOCAL_ID", 0)
            val localTexto = if (localId > 0) LocalArmazenamento.obterPeloId(localId).nomeLocal else intent.getStringExtra("LOCAL")
            selecionarSpinnerPorTexto(binding.spinnerLocal, localTexto)

            val medidaId = intent.getIntExtra("MEDIDA_ID", 1)
            val medidaNome = Medida.obterPeloId(medidaId).nomeMedida
            selecionarSpinnerPorTexto(binding.spinnerMedida, medidaNome)

            val consumindo = intent.getBooleanExtra("CONSUMINDO", false)
            selecionarSpinnerPorTexto(
                binding.spinnerEstado,
                if (consumindo) "Consumindo" else "Por consumir"
            )

            val dataVal = intent.getStringExtra("DATA_VALIDADE")
            if (!dataVal.isNullOrBlank()) {
                binding.checkValidade.isChecked = true
                binding.editDataExibicao.isEnabled = true
                binding.editDataExibicao.setText(dataVal)
            }

            imagemSelecionada = intent.getStringExtra("IMAGEM") ?: "item_padrao.png"
        }
    }

    private fun selecionarSpinnerPorTexto(spinner: Spinner, texto: String?) {
        if (texto.isNullOrBlank()) return
        val adapter = spinner.adapter as? ArrayAdapter<*> ?: return
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i)?.toString().equals(texto, ignoreCase = true)) {
                spinner.setSelection(i)
                return
            }
        }
    }

    // Busca as listas de apoio (/infoGeral) e sugestões (/itens_padrao) do servidor (RF08, RF23, B04, B11)
    private fun buscarSugestoesEInfoDoServidor() {
        val api = ApiClient.getService(this)

        lifecycleScope.launch {
            try {
                // Tenta buscar listas completas pela rota /infoGeral
                try {
                    val info = api.buscarInfoGeral()
                    atualizarListasDeApoio(info)
                    if (info.itens_padrao.isNotEmpty()) {
                        listaDeSugestoesBackend = info.itens_padrao
                    }
                } catch (_: Exception) {
                    // Fallback para /itens_padrao caso o servidor esteja com versão anterior
                }

                if (listaDeSugestoesBackend.isEmpty()) {
                    val resposta = api.listarItensPadrao()
                    listaDeSugestoesBackend = resposta.listaDeSugestoes
                }

                val apenasNomes = listaDeSugestoesBackend.map { it.nome }
                val adapterSugestao = ArrayAdapter(
                    this@AdicionarItem,
                    android.R.layout.simple_dropdown_item_1line,
                    apenasNomes
                )
                binding.autoNomeAlimento.setAdapter(adapterSugestao)
            } catch (e: Exception) {
                Log.w("AdicionarItem", "Aviso ao buscar sugestões: ${e.message}")
            }
        }
    }

    // Dica centralizada em Categoria.obterDica considerando categoria e local (RN07, RN16, B04)
    private fun atualizarDica() {
        if (binding.spinnerCategoria.selectedItemPosition > 0) {
            val nomeCat = binding.spinnerCategoria.selectedItem.toString()
            val catId = Categoria.obterPeloNome(nomeCat).id_categoria
            val localId = if (binding.spinnerLocal.selectedItemPosition > 0) {
                LocalArmazenamento.obterPeloNome(binding.spinnerLocal.selectedItem.toString()).idLocal
            } else null

            binding.txtDicaCategoria.visibility = View.VISIBLE
            binding.txtDicaCategoria.text = Categoria.obterDica(catId, localId)
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

        if (binding.spinnerCategoria.selectedItemPosition == 0 ||
            binding.spinnerEstado.selectedItemPosition == 0 ||
            binding.spinnerLocal.selectedItemPosition == 0
        ) {
            Toast.makeText(this, "Preencha todos os campos de seleção (Categoria, Local e Estado)", Toast.LENGTH_SHORT).show()
            return
        }

        // B13 / RN03: Se marcou que possui data de validade, a data precisa ser informada
        if (binding.checkValidade.isChecked) {
            val dataValidade = obterDataValidade(binding)
            if (dataValidade == null) {
                Toast.makeText(this, "Informe a data de validade ou desmarque a opção", Toast.LENGTH_SHORT).show()
                return
            }

            // RN04 / B13: A validade informada manualmente no cadastro novo não pode ser anterior à data atual
            val (_, diasRestantes, _) = calcularSemaforoData(true, dataValidade)
            if (idItemEdicao == -1L && diasRestantes != null && diasRestantes < 0) {
                Toast.makeText(this, "A data de validade não pode ser anterior à data de hoje", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Se o nome digitado bate com algum item padrão e ainda estava com imagem genérica, aplica a imagem padrão (RN06)
        if (imagemSelecionada == "item_padrao.png") {
            val matchPadrao = listaDeSugestoesBackend.firstOrNull { it.nome.equals(nome, ignoreCase = true) }
            if (matchPadrao != null) {
                imagemSelecionada = matchPadrao.imagem
            }
        }

        enviarParaOBanco()
    }

    private fun enviarParaOBanco() {
        val secaoUsuario = SecaoUsuario(this)
        val userId = secaoUsuario.obterUserId()
        if (userId == null) {
            Toast.makeText(this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val possuiValidade = binding.checkValidade.isChecked
        val dataVal = if (possuiValidade) obterDataValidade(binding) else null
        val (flagCalculada, _, _) = calcularSemaforoData(possuiValidade, dataVal)

        val qtdStr = binding.editQuantidade.text.toString().replace(",", ".").trim()
        val precoStr = binding.editPreco.text.toString().replace(",", ".").trim()

        val itemNovo = BodyCriarItem(
            id_usuario = userId,
            nome = binding.autoNomeAlimento.text.toString().trim(),
            id_categoria = Categoria.obterPeloNome(binding.spinnerCategoria.selectedItem.toString()).id_categoria,
            imagem = imagemSelecionada,
            consumindo = binding.spinnerEstado.selectedItem.toString() == "Consumindo",
            possui_data_validade = possuiValidade,
            data_validade = dateToString(dataVal, "yyyy-MM-dd"),
            id_local_armazenamento = LocalArmazenamento.obterPeloNome(binding.spinnerLocal.selectedItem.toString()).idLocal,
            quantidade = qtdStr.toDoubleOrNull() ?: 0.0,
            id_medida = Medida.obterPeloNome(binding.spinnerMedida.selectedItem.toString()).idMedida,
            preco = precoStr.toDoubleOrNull() ?: 0.0,
            id_flag = flagCalculada
        )

        val api = ApiClient.getService(this)
        binding.btnAdicionarItem.isEnabled = false

        lifecycleScope.launch {
            try {
                val resp = if (idItemEdicao == -1L) {
                    api.criarItemUsuario(itemNovo)
                } else {
                    api.editarItemUsuario(idItemEdicao, itemNovo)
                }

                if (resp.isSuccessful) {
                    LocalBroadcastManager.getInstance(this@AdicionarItem)
                        .sendBroadcast(Intent("ATUALIZAR_LISTA_ANONA"))

                    val mensagem = if (idItemEdicao == -1L) {
                        "Item adicionado com sucesso"
                    } else {
                        "Item editado com sucesso"
                    }

                    Toast.makeText(this@AdicionarItem, mensagem, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // B06: Tratar !isSuccessful e exibir mensagem clara ao usuário
                    val errBody = resp.errorBody()?.string()
                    val msg = try {
                        JSONObject(errBody ?: "").optString("response", "Não foi possível salvar o item")
                    } catch (_: Exception) {
                        "Não foi possível salvar o item (HTTP ${resp.code()})"
                    }
                    Toast.makeText(this@AdicionarItem, msg, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("AdicionarItem", "Erro ao salvar item", e)
                Toast.makeText(this@AdicionarItem, "Erro ao conectar com o servidor", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnAdicionarItem.isEnabled = true
            }
        }
    }

    private fun configurarListasDeEscolha() {
        val categorias = listOf("Selecione uma categoria") + listaCategorias.map { it.nomeCategoria }
        binding.spinnerCategoria.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                atualizarDica()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val locais = listOf("Selecione o local") + listaLocais.map { it.nomeLocal }
        binding.spinnerLocal.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locais).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerLocal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                atualizarDica()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val estados = listOf("Selecione o estado", "Consumindo", "Por consumir")
        binding.spinnerEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Usa a listaMedidas sincronizada com os IDs do banco (B01, B04)
        val medidas = listaMedidas.map { it.nomeMedida }
        binding.spinnerMedida.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, medidas).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun configurarValidadeECalendario() {
        binding.editDataExibicao.isEnabled = false

        binding.checkValidade.setOnCheckedChangeListener { _, isChecked ->
            binding.editDataExibicao.isEnabled = isChecked
            if (!isChecked) {
                binding.editDataExibicao.setText("")
                dataVencimentoSelecionadaMs = 0
            }
        }

        binding.editDataExibicao.setOnClickListener {
            if (!binding.checkValidade.isChecked) return@setOnClickListener

            val c = Calendar.getInstance()
            val dialog = DatePickerDialog(this, { _, year, month, day ->
                val dataFormatada = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year)
                binding.editDataExibicao.setText(dataFormatada)

                val cal = Calendar.getInstance().apply { set(year, month, day, 0, 0, 0) }
                dataVencimentoSelecionadaMs = cal.timeInMillis
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

            // RN04: No cadastro de novo item, impede selecionar datas no passado no calendário
            if (idItemEdicao == -1L) {
                val hojeInicio = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                dialog.datePicker.minDate = hojeInicio
            }

            dialog.show()
        }
    }

    private fun obterDataValidade(binding: ActivityAdicionarItemBinding): Date? {
        val str = binding.editDataExibicao.text.toString()
        if (str.isBlank()) return null
        return try {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(str)
        } catch (e: Exception) {
            null
        }
    }

    private fun dateToString(date: Date?, pattern: String): String? {
        if (date == null) return null
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }
}