package com.example.annona

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log.e
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


 //A tela AdicionarItem é responsável por cadastrar ou editar um item na despensa do usuário.
 //Ela consome a API para sugerir nomes de alimentos e salvar/atualizar no banco de dados.
class AdicionarItem : AppCompatActivity() {

     //VARIÁVEIS DE CONTROLE (O que o app precisa lembrar enquanto o usuario digita)
    private lateinit var binding: ActivityAdicionarItemBinding // essa sintaxe é um atalho para os componentes da tela.
    private var dataVencimentoSelecionadaMs: Long = 0 // Guarda a data de validade em milissegundos.
    private var listaDeSugestoesBackend: List<ModeloItemSugestao> = emptyList() // A lista de nomes que o servidor sugere.
    private var imagemSelecionada: String = "item_padrao.png" // Nome da imagem que será salva (se o usuario não selecionar um item da lista padrão)

    // Essa variável serve para controlar se estamos editando. -1 significa novo item. Se for outro número, estamos editando um que já existe.
    private var idItemEdicao: Long = -1L



    override fun onCreate(savedInstanceState: Bundle?) { // É aqui que preparamos o esqueleto e os botões da tela.
        super.onCreate(savedInstanceState) //Essa sintaxe ao Android para executar as configurações padrão de sistema.
        binding = ActivityAdicionarItemBinding.inflate(layoutInflater) //aqui cria-se a conexão entre o código Kotlin e o lauout xml.
        setContentView(binding.root) //Aqui define-se qual arquivo visual deve aparecer para o usuário (o conteúdo da tela).

        setSupportActionBar(binding.toolbarAdicionar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //aqui habilito a setinha de voltar no topo da tela.
        supportActionBar?.setDisplayShowTitleEnabled(false) //essa sintaxe remove o título padrão annona para ficar mais bonitinho
        binding.toolbarAdicionar.setNavigationOnClickListener { finish() } //Ao clicar na setinha de voltar, a tela atual deve ser fechada (finish).

        buscarSugestoesDoServidor() //aqui faz-se uma chamada para a API pra buscar nomes de alimentos e sugerir enquanto o usuário digita.

        // Configura as listas de categoria, local de arm. e calendários antes de preencher os dados de edição
        configurarListasDeEscolha()
        configurarValidadeECalendario()

        //Verifica se recebemos dados para editar
        verificarSeEhEdicao()


        //LÓGICA DO AUTOCOMPLETE (sugestões de nome)
        //Quando o usuário clica em um nome sugerido (ex: clicou em "Leite"):
        binding.autoNomeAlimento.setOnItemClickListener { parent, _, position, _ ->

            // Descobre qual nome foi clicado
            val nomeClicado = parent.getItemAtPosition(position).toString()

            // Procura na lista que veio do servidor qual é o ícone e a categoria desse alimento.
            val itemEncontrado = listaDeSugestoesBackend.find { it.nome == nomeClicado }

            if (itemEncontrado != null) {
                // Se encontrou, já seleciona a categoria certa no Spinner automaticamente.
                binding.spinnerCategoria.setSelection(itemEncontrado.id_categoria)

                // Guarda o nome do arquivo de imagem (ex: "leite.png") para salvar depois.
                imagemSelecionada = itemEncontrado.imagem

                // Atualiza a frase de dica (função que fiz mais abaixo).
                atualizarDica()
            }
        }

        //BOTÃO DE FINALIZAR
        // Define a ação do botão principal (Adicionar ou Salvar Alterações).
        binding.btnAdicionarItem.setOnClickListener {
            validarESalvar() //Antes de enviar para o banco, ele verifica se os campos estão preenchidos corretamente
        }
    }

    //Função para identificar se a tela deve carregar dados de um item existente (se o usuario for editar)
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


     //FUNÇÃO DE API
    // Esta função vai até o servidor buscar nomes de alimentos (que estejam na lista de itens padrão) para sugerir ao usuário.
    private fun buscarSugestoesDoServidor() {
         // aqui pegamos o endereço do servidor e preparamos o Retrofit
        val urlApi = this.getString(R.string.api_base_url)
        val retrofit = Retrofit.Builder().baseUrl(urlApi).addConverterFactory(GsonConverterFactory.create()).build() //Tradutor de JSON para Kotlin
        val api = retrofit.create(ServicoApi::class.java)

         //Usamos o lifecycleScope para que se o usuário fechar a tela, a busca pare na hora.
        lifecycleScope.launch {
            try {
                val resposta = api.listarItensPadrao() //Pedindo para a API a lista de itens padrão (sugestões)
                listaDeSugestoesBackend = resposta.listaDeSugestoes //Guardamos a lista completa (com nomes, ícones e categorias) na nossa variável global.
                val apenasNomes = listaDeSugestoesBackend.map { it.nome } //aqui extraímos apenas os nomes para mostrar na lista de sugestão do campo de texto.

                //criando o adapter que vai mostrar esses nomes enquanto o usuário digita
                val adapterSugestao = ArrayAdapter(this@AdicionarItem, android.R.layout.simple_dropdown_item_1line, apenasNomes)
                binding.autoNomeAlimento.setAdapter(adapterSugestao) //Conectando esse adaptador ao campo de AutoComplete da tela.
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
        enviarParaOBanco() //chamando a função que estou criando logo abaixo
    }





     //A FUNÇÃO DE ENVIO
     // Ela serve pra criar um item NOVO ou SALVAR as alterações de um item já existente.
    private fun enviarParaOBanco() {
        val secaoUsuario = SecaoUsuario(this)
        val userId = secaoUsuario.obterUserId() ?: 0

        val itemNovo = BodyCriarItem( //aqui estou criando um objeto com a informação do cadastro, e esse objeto vai ser enviado como body pra API e a API manda pro banco de dados
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


        //resumo dessas 3 linhas:
        // 1. estou pegando a URL que está no arquivo string XML
        // 2. depois, dizendo pro retrofit qual é a url base (a url base é a url sem a parte da rota)
        // 3. por fim, digo pro retrofit qual é a interface com a lista de rotas e funções (que é ServicoApi.kt)
        val urlApi = this.getString(R.string.api_base_url)
        val retrofit = Retrofit.Builder().baseUrl(urlApi).addConverterFactory(GsonConverterFactory.create()).build()
        val api = retrofit.create(ServicoApi::class.java)


        lifecycleScope.launch { //lembrete: isso é pra não travar a tela enquanto realizo a requisição
            try {
                //Se temos um ID, usamos a rota de Editar (PUT), senão Criar (POST)
                val resp = if (idItemEdicao == -1L) {
                    api.criarItemUsuario(itemNovo)
                } else {
                    api.editarItemUsuario(idItemEdicao, itemNovo)
                }

                if (resp.isSuccessful) {
                    LocalBroadcastManager.getInstance(this@AdicionarItem)
                        .sendBroadcast(Intent("ATUALIZAR_LISTA_ANONA")) //o grito nvamente

                    // Decide a mensagem dependendo se é adição ou edição
                    val mensagem = if (idItemEdicao == -1L) {
                        "Item adicionado com sucesso"
                    } else {
                        "Item editado com sucesso"
                    }

                    Toast.makeText(this@AdicionarItem, mensagem, Toast.LENGTH_SHORT).show()
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
        binding.editDataExibicao.isEnabled = false //Começa com o campo de data desativado (cinza).

        // Se o usuário marcar/desmarcar a caixinha Possui validade:
        binding.checkValidade.setOnCheckedChangeListener { _, isChecked ->
            binding.editDataExibicao.isEnabled = isChecked //Ativa ou desativa o campo.
            if (!isChecked) { binding.editDataExibicao.setText(""); dataVencimentoSelecionadaMs = 0 } //Limpa o texto se desmarcar.
        }
        binding.editDataExibicao.setOnClickListener { //Ao clicar no campo de texto da data, abre o calendário do Android.
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                val dataFormatada = String.format("%02d/%02d/%d", day, month + 1, year) //formatando para o usuário ler: DD/MM/AAAA.
                binding.editDataExibicao.setText(dataFormatada)

                // Converte a data escolhida para Milissegundos, muito imoortante para cáculos matemáticos.
                val cal = Calendar.getInstance().apply { set(year, month, day, 23, 59, 59) }
                dataVencimentoSelecionadaMs = cal.timeInMillis
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
    }




     // Transforma o texto "20/05/2026" em um objeto do tipo Date.
    private fun obterDataValidade(binding: ActivityAdicionarItemBinding): Date? {
        val str = binding.editDataExibicao.text.toString()
        if (str.isBlank()) return null
        return try { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(str) } catch (e: Exception) { null }
    }




     // Transforma o objeto Date em uma String formatada para o Banco de Dados (ex: 2026-05-20).
    private fun dateToString(date: Date?, pattern: String): String? {
        if (date == null) return null
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }
}