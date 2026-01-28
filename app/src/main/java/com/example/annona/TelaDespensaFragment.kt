package com.example.annona

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

// Esta é a tela principal da Despensa, onde os alimentos aparecem listados
class TelaDespensaFragment : Fragment() {

    // Aqui criamos uma "caixa" (lista) para guardar todos os produtos da despensa
    private var listaCompleta: MutableList<Item> = mutableListOf()
    // O 'adapter' é o ajudante que pega os dados da lista e desenha eles na tela
    private var adapterGlobal: AdaptadorItem? = null

    // --- VARIÁVEL DE MEMÓRIA PARA O FILTRO ---
    // Guarda qual filtro está selecionado para não "perder" a seleção ao consumir um item [novo]
    private var filtroAtual: String = "Todos os Itens"

    // [NOVO] Este receptor fica "ouvindo" o aviso de que um item foi salvo para recarregar a lista na hora
    private val recarregarReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            carregarItensApi()
        }
    }

    companion object {
        // Esta função serve para "abrir" a tela da despensa dentro do menu principal
        fun newInstance(page: String, position: Int, userId: Int) = TelaDespensaFragment().apply {
            arguments = Bundle().apply {
                putString("page", page)
                putInt("position", position)
                putInt("USER_ID", userId) // Lembra qual é o usuário que está logado
            }
        }
    }

    // Aqui o app "desenha" o visual da tela usando o arquivo XML
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tela_despensa, container, false)
    }

    // [AJUSTE 1] O onResume roda toda vez que a tela volta a ficar visível (ex: após fechar a tela de Adicionar)
    override fun onResume() {
        super.onResume()
        // Chamamos a função de carregar itens sempre que voltamos para a tela para garantir que a lista atualize
        carregarItensApi()
    }

    // [NOVO] Registramos o "ouvido" do app quando a tela inicia
    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(recarregarReceiver, IntentFilter("ATUALIZAR_LISTA_ANONA"))
    }

    // [NOVO] Removemos o receptor ao parar para economizar recursos
    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(recarregarReceiver)
    }

    // Aqui é onde damos vida aos botões e listas depois que a tela aparece
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- PARTE 1: O BOTÃO DE FILTRAR (A BARRA AZUL) ---
        val cardFiltro = view.findViewById<View>(R.id.card_filtro_status)
        val txtStatus = view.findViewById<TextView>(R.id.txt_status_selecionado)

        // Ajustado: Agora o texto inicial padrão é "Todos os Itens"
        txtStatus.text = "Todos os Itens"

        // Quando o usuário clica na barra de filtro, sobe uma listinha de opções
        cardFiltro.setOnClickListener {
            val popup = PopupMenu(requireContext(), cardFiltro)

            // Opções simplificadas: Apenas estados e cores do semáforo
            popup.menu.add("Todos os Itens")
            popup.menu.add("Consumindo")
            popup.menu.add("Por consumir")
            popup.menu.add("Vencido")
            popup.menu.add("Crítico")
            popup.menu.add("Atenção")
            popup.menu.add("Seguro")

            // Quando o usuário escolhe uma opção, a lista lá embaixo muda na hora
            popup.setOnMenuItemClickListener { item ->
                txtStatus.text = item.title
                filtroAtual = item.title.toString() // Salva o nome do filtro escolhido [novo]
                atualizarListaNaTela() // Chama a função que aplica o filtro corretamente [novo]
                true
            }
            popup.show()
        }

        // --- PARTE 2: CONFIGURAR A LISTA DE PRODUTOS ---
        val rv = view.findViewById<RecyclerView>(R.id.rvItems)
        rv.layoutManager = LinearLayoutManager(requireContext()) // Faz a lista ser de cima para baixo

        val adapter = AdaptadorItem(
            onClick = { item -> /* O que acontece se clicar no item (vazio por enquanto) */ },
            onOptionsMenuClick = { anchorView, item ->
                // Quando clica nos 3 pontinhos do item, abre as opções (editar, excluir...)
                mostrarMenuOpcoes(anchorView, item)
            }
        )

        rv.adapter = adapter
        adapterGlobal = adapter

        // --- PARTE 3: O SEMÁFORO (BOTÕES COLORIDOS DO TOPO) ---
        view.findViewById<View>(R.id.card_vencido).setOnClickListener {
            filtroAtual = "Vencido"; txtStatus.text = "Vencido"; atualizarListaNaTela()
        }
        view.findViewById<View>(R.id.card_critico).setOnClickListener {
            filtroAtual = "Crítico"; txtStatus.text = "Crítico"; atualizarListaNaTela()
        }
        view.findViewById<View>(R.id.card_atencao).setOnClickListener {
            filtroAtual = "Atenção"; txtStatus.text = "Atenção"; atualizarListaNaTela()
        }
        view.findViewById<View>(R.id.card_seguro).setOnClickListener {
            filtroAtual = "Seguro"; txtStatus.text = "Seguro"; atualizarListaNaTela()
        }

        // --- PARTE 4: BOTÃO DE ADICIONAR (+) ---
        val btnAdd = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_add_item)
        btnAdd.setOnClickListener {
            (activity as? MainActivity)?.abrirAdicionarItem()
        }
    }

    // [AJUSTE 1] Função isolada para carregar os dados.
    private fun carregarItensApi() {
        val urlApi = requireContext().getString(R.string.api_base_url)
        val retrofit = Retrofit.Builder()
            .baseUrl(urlApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ServicoApi::class.java)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val userId = arguments?.getInt("USER_ID") ?: 1
                val resp = api.listarItens(userId)
                val itens = resp.listaItens.onEach { it.mapearCampos(requireContext()) }

                // --- AJUSTE INTELIGENTE: Pega os dados e já calcula os prazos reais antes de mostrar ---
                listaCompleta = processarValidades(itens.toMutableList())
                atualizarListaNaTela()
                view?.let { atualizarContadores(it) }

            } catch (e: Exception) {
                // Erro tratado silenciosamente para não atrapalhar a UX
            }
        }
    }

    // --- NOVA FUNÇÃO: REAPLICA O FILTRO SELECIONADO NA TELA ---
    private fun atualizarListaNaTela() {
        val listaFiltrada = when (filtroAtual) {
            "Consumindo" -> listaCompleta.filter { it.estado == "Consumindo" }
            "Por consumir" -> listaCompleta.filter { it.estado == "Por consumir" }
            "Vencido" -> listaCompleta.filter { it.flag == 1 }
            "Crítico" -> listaCompleta.filter { it.flag == 2 }
            "Atenção" -> listaCompleta.filter { it.flag == 3 }
            "Seguro" -> listaCompleta.filter { it.flag == 4 }
            else -> listaCompleta
        }
        adapterGlobal?.submitList(listaFiltrada.toList())
    }

    // --- NOVA FUNÇÃO: CALCULA O PRAZO E A COR (FLAG) PARA CADA ITEM DA LISTA ---
    private fun processarValidades(lista: MutableList<Item>): MutableList<Item> {
        val hojeMs = Calendar.getInstance().timeInMillis

        return lista.map { item ->
            if (item.possuiDataValidade == 0) {
                item
            } else {
                val diffMs = (item.dataVencimentoMs ?: 0) - hojeMs
                val dias = if (diffMs > 0) TimeUnit.MILLISECONDS.toDays(diffMs) else 0

                val novaFlag = when {
                    diffMs <= 0 -> 1
                    dias <= 3 -> 2
                    dias <= 7 -> 3
                    else -> 4
                }

                val novoPrazo = if (diffMs <= 0) "VENCIDO" else "$dias DIAS"
                item.copy(prazo = novoPrazo, flag = novaFlag)
            }
        }.toMutableList()
    }

    // Esta função mostra o menu de "Editar", "Consumir" ou "Excluir" cada alimento
    private fun mostrarMenuOpcoes(anchorView: View, item: Item) {
        val popup = PopupMenu(requireContext(), anchorView)
        popup.menu.add("Editar item")

        val textoConsumo = if (item.estado == "Consumindo") "Por consumir" else "Consumir item"
        popup.menu.add(textoConsumo)
        popup.menu.add("Excluir item")

        popup.setOnMenuItemClickListener { menuIt ->
            when (menuIt.title) {
                "Editar item" -> {
                    // [AJUSTE EDIÇÃO] Enviamos os dados EXATOS que a tela AdicionarItem espera receber
                    val intent = Intent(requireContext(), AdicionarItem::class.java)
                    intent.putExtra("ITEM_ID", item.id)
                    intent.putExtra("NOME", item.nome)
                    intent.putExtra("QUANTIDADE", item.id) // Você pode ajustar se tiver o campo qtd real no objeto
                    intent.putExtra("PRECO", 0.0) // Ajustar se tiver o campo preço
                    intent.putExtra("CATEGORIA_ID", item.idCategoria)
                    intent.putExtra("LOCAL", item.localNome)
                    intent.putExtra("CONSUMINDO", item.consumindo == 1)
                    intent.putExtra("IMAGEM", item.imagem)

                    // Formata a data para exibir no campo de texto do formulário
                    if (item.possuiDataValidade == 1 && item.dataValidade != null) {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        intent.putExtra("DATA_VALIDADE", sdf.format(item.dataValidade))
                    }

                    startActivity(intent)
                }
                "Consumir item", "Por consumir" -> {
                    // Lógica de consumo rápido (Offline por enquanto até integrar o PUT de consumo)
                    val novoEstadoNum = if (item.consumindo == 1) 0 else 1
                    val index = listaCompleta.indexOf(item)
                    if (index != -1) {
                        listaCompleta[index] = item.copy(consumindo = novoEstadoNum).apply { mapearCampos(requireContext()) }
                        atualizarListaNaTela()
                    }
                }
                "Excluir item" -> confirmarExclusao(item)
            }
            true
        }
        popup.show()
    }

    private fun confirmarExclusao(item: Item) {
        val motivos = arrayOf("Item consumido", "Item vencido/estragado", "Outro")
        var motivoSelecionado = motivos[0]

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Excluir \"${item.nome}\"")
            .setSingleChoiceItems(motivos, 0) { _, which -> motivoSelecionado = motivos[which] }
            .setPositiveButton("Confirmar Exclusão") { d, _ ->
                deletarItem(item.id.toInt(), motivoSelecionado)
                d.dismiss()
            }
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.vermelho_murango))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
    }

    private fun atualizarContadores(view: View) {
        val v = listaCompleta.count { it.flag == 1 }
        val c = listaCompleta.count { it.flag == 2 }
        val a = listaCompleta.count { it.flag == 3 }
        val s = listaCompleta.count { it.flag == 4 }

        view.findViewById<TextView>(R.id.txt_vencido_contador).text = "Vencido $v"
        view.findViewById<TextView>(R.id.txt_critico_contador).text = "Crítico $c"
        view.findViewById<TextView>(R.id.txt_atencao_contador).text = "Atenção $a"
        view.findViewById<TextView>(R.id.txt_seguro_contador).text = "Seguro $s"
    }




    private fun deletarItem(id_item: Int, razao_delecao: String) {

        val bodyDelecao = BodyDeletarItem(
            razao_delecao
        )

        val urlApi = requireContext().getString(R.string.api_base_url)
        val retrofit = Retrofit.Builder().baseUrl(urlApi).addConverterFactory(GsonConverterFactory.create()).build()
        val api = retrofit.create(ServicoApi::class.java)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resp = api.deletarItemUsuario(id_item,bodyDelecao)

                if (resp.isSuccessful) {
                    LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent("ATUALIZAR_LISTA_ANONA"))
                    Toast.makeText(requireContext(), "Item excluido", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(), "Erro ao deletar item. ", Toast.LENGTH_SHORT).show()

                }
            } catch (e: Exception) {
                val erro = e.message
                Toast.makeText(requireContext(), "Erro ao conectar com o servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


