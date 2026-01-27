package com.example.annona

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.util.Calendar // Importado para pegar a data atual [novo]
import java.util.concurrent.TimeUnit // Importado para converter milissegundos em dias [novo]

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
        val txtStatus = view.findViewById<android.widget.TextView>(R.id.txt_status_selecionado)

        // Ajustado: Agora o texto inicial padrão é "Todos os Itens"
        txtStatus.text = "Todos os Itens"

        // Quando o usuário clica na barra de filtro, sobe uma listinha de opções
        cardFiltro.setOnClickListener {
            val popup = android.widget.PopupMenu(requireContext(), cardFiltro)

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

        // [AJUSTE 1] A chamada inicial foi movida para carregarItensApi() que é executada no onResume

        // --- PARTE 3: O SEMÁFORO (BOTÕES COLORIDOS DO TOPO) ---
        // Se clicar no botão Marrom, mostra só os Vencidos (Flag 1)
        view.findViewById<View>(R.id.card_vencido).setOnClickListener {
            filtroAtual = "Vencido"
            txtStatus.text = "Vencido"
            atualizarListaNaTela()
        }
        // Se clicar no botão Vermelho, mostra os Críticos (Flag 2)
        view.findViewById<View>(R.id.card_critico).setOnClickListener {
            filtroAtual = "Crítico"
            txtStatus.text = "Crítico"
            atualizarListaNaTela()
        }
        // Se clicar no botão Amarelo, mostra os de Atenção (Flag 3)
        view.findViewById<View>(R.id.card_atencao).setOnClickListener {
            filtroAtual = "Atenção"
            txtStatus.text = "Atenção"
            atualizarListaNaTela()
        }
        // Se clicar no botão Verde, mostra os Seguros (Flag 4)
        view.findViewById<View>(R.id.card_seguro).setOnClickListener {
            filtroAtual = "Seguro"
            txtStatus.text = "Seguro"
            atualizarListaNaTela()
        }

        // --- PARTE 4: BOTÃO DE ADICIONAR (+) ---
        val btnAdd = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_add_item)
        btnAdd.setOnClickListener {
            // [AJUSTE] Agora chamamos a função da MainActivity para que o Launcher de lá funcione
            (activity as? MainActivity)?.abrirAdicionarItem()
        }
    }

    // [AJUSTE 1] Função isolada para carregar os dados. Criamos ela para poder chamar tanto no início quanto no retorno da tela de adição.
    private fun carregarItensApi() {
        // --- TRAZENDO A LISTA DE ITENS DO BACKEND DEPENDENDO DO ID DO USUÁRIO ---
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
                atualizarListaNaTela() // Mantém o filtro que o usuário já tinha selecionado
                view?.let { atualizarContadores(it) } // Atualiza os números coloridos lá no topo

            } catch (e: Exception) {
                // Toast.makeText(requireContext(), "Erro ao tentar carregar os itens: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // --- NOVA FUNÇÃO: REAPLICA O FILTRO SELECIONADO NA TELA ---
    // Esta função garante que, ao mudar um status, o app continue respeitando o filtro ativo (ex: Crítico) [novo]
    private fun atualizarListaNaTela() {
        val listaFiltrada = when (filtroAtual) {
            "Consumindo" -> listaCompleta.filter { it.estado == "Consumindo" }
            "Por consumir" -> listaCompleta.filter { it.estado == "Por consumir" }
            "Vencido" -> listaCompleta.filter { it.flag == 1 }
            "Crítico" -> listaCompleta.filter { it.flag == 2 }
            "Atenção" -> listaCompleta.filter { it.flag == 3 }
            "Seguro" -> listaCompleta.filter { it.flag == 4 }
            else -> listaCompleta // "Todos os Itens"
        }
        adapterGlobal?.submitList(listaFiltrada.toList())
    }

    // --- NOVA FUNÇÃO: CALCULA O PRAZO E A COR (FLAG) PARA CADA ITEM DA LISTA ---
    // Esta função percorre a lista e define as cores baseadas no tempo de validade
    private fun processarValidades(lista: MutableList<Item>): MutableList<Item> {
        val hojeMs = Calendar.getInstance().timeInMillis

        return lista.map { item ->

            // [AJUSTE 3] Proteção para itens sem data: se não tem validade, não calculamos o semáforo.
            // Isso evita que itens sem data apareçam como "Vencidos" ou com "0 dias".
            if (item.possuiDataValidade == 0) {
                // Retornamos o item como ele está (com a flag 4 verde que definimos no Item.kt)
                item
            } else {
                // Se o item POSSUI data, fazemos o cálculo normal de diferença de dias
                val diffMs = (item.dataVencimentoMs ?: 0) - hojeMs
                val dias = if (diffMs > 0) TimeUnit.MILLISECONDS.toDays(diffMs) else 0

                // Define a Flag (cor do semáforo) com base nos dias que faltam
                val novaFlag = when {
                    diffMs <= 0 -> 1 // Vencido (Marrom)
                    dias <= 3 -> 2   // Crítico (Vermelho) - Menos de 3 dias
                    dias <= 7 -> 3   // Atenção (Amarelo) - Entre 4 e 7 dias
                    else -> 4        // Seguro (Verde) - Mais de 7 dias
                }

                val novoPrazo = if (diffMs <= 0) "VENCIDO" else "$dias DIAS"

                // Cria uma cópia do item com os dados calculados na hora para exibir no card
                item.copy(prazo = novoPrazo, flag = novaFlag)
            }
        }.toMutableList()
    }

    // Esta função mostra o menu de "Editar", "Consumir" ou "Excluir" cada alimento
    private fun mostrarMenuOpcoes(anchorView: View, item: Item) {
        val popup = android.widget.PopupMenu(requireContext(), anchorView)
        popup.menu.add("Editar item")

        // Verifica se o item já está sendo usado para mudar o texto do botão
        val textoConsumo = if (item.estado == "Consumindo") "Por consumir" else "Consumir item"
        popup.menu.add(textoConsumo)

        // REMOVIDO: A opção "Doar Item" foi excluída conforme solicitado
        popup.menu.add("Excluir item")

        popup.setOnMenuItemClickListener { menuIt ->
            when (menuIt.title) {
                "Editar item" -> {
                    // Manda as informações atuais do item para a tela de edição
                    val intent = android.content.Intent(requireContext(), AdicionarItem::class.java)
                    intent.putExtra("ITEM_ID", item.id)
                    intent.putExtra("TITULO", item.nome)
                    intent.putExtra("ESTADO", item.estado)
                    intent.putExtra("LOCAL", item.localNome)
                    intent.putExtra("DICA", item.dica)
                    // Passamos também a categoria e a data para a tela de edição [novo]
                    intent.putExtra("CATEGORIA", item.categoria)
                    intent.putExtra("DATA_MS", item.dataVencimentoMs)
                    intent.putExtra("POSSUI_DATA", item.possuiDataValidade)
                    startActivity(intent)
                }
                "Consumir item", "Por consumir" -> {
                    // Troca o estado do produto (ex: de fechado para aberto/usando)
                    val novoEstado = if (item.estado == "Consumindo") "Por consumir" else "Consumindo"
                    val index = listaCompleta.indexOf(item)
                    if (index != -1) {
                        listaCompleta[index] = item.copy(estado = novoEstado)
                        // Ajuste: Agora chamamos a função que respeita o filtro selecionado [novo]
                        atualizarListaNaTela()
                    }
                }
                "Excluir item" -> confirmarExclusao(item)
            }
            true
        }
        popup.show()
    }

    // Janela que pergunta o motivo antes de apagar o item da lista
    private fun confirmarExclusao(item: Item) {
        // Ajustado: Removido "Item doado" da lista de motivos de exclusão
        val motivos = arrayOf("Item consumido", "Item vencido/estragado", "Outro")
        var motivoSelecionado = motivos[0]

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Excluir \"${item.nome}\"")
            .setSingleChoiceItems(motivos, 0) { _, which ->
                motivoSelecionado = motivos[which]
            }
            .setPositiveButton("Confirmar Exclusão") { d, _ ->
                listaCompleta.remove(item) // Tira o produto da lista
                // Ajuste: Após excluir, também precisamos respeitar o filtro ativo [novo]
                atualizarListaNaTela()
                atualizarContadores(requireView()) // Atualiza os números do semáforo

                Toast.makeText(requireContext(), "Removido: $motivoSelecionado", Toast.LENGTH_SHORT).show()
                d.dismiss()
            }
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .create()

        dialog.show()

        // Coloca cor nos botões da janelinha para ficar bonito
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.vermelho_murango))
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(android.graphics.Color.GRAY)
    }

    // Esta função conta quantos produtos têm em cada cor do semáforo (1, 2, 3 ou 4)
    private fun atualizarContadores(view: View) {
        val v = listaCompleta.count { it.flag == 1 } // Marrom (Vencido)
        val c = listaCompleta.count { it.flag == 2 } // Vermelho (Crítico)
        val a = listaCompleta.count { it.flag == 3 } // Amarelo (Atenção)
        val s = listaCompleta.count { it.flag == 4 } // Verde (Seguro)

        // Escreve os números nos cartões lá do topo da tela
        view.findViewById<TextView>(R.id.txt_vencido_contador).text = "Vencido $v"
        view.findViewById<TextView>(R.id.txt_critico_contador).text = "Crítico $c"
        view.findViewById<TextView>(R.id.txt_atencao_contador).text = "Atenção $a"
        view.findViewById<TextView>(R.id.txt_seguro_contador).text = "Seguro $s"
    }
}