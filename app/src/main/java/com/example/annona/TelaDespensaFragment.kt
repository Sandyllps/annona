package com.example.annona

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Esta é a tela principal da Despensa, onde os alimentos aparecem listados [cite: 19]
class TelaDespensaFragment : Fragment() {

    // Aqui criamos uma "caixa" (lista) para guardar todos os produtos da despensa [cite: 29]
    private var listaCompleta: MutableList<Item> = mutableListOf()
    // O 'adapter' é o ajudante que pega os dados da lista e desenha eles na tela
    private var adapterGlobal: AdaptadorItem? = null

    companion object {
        // Esta função serve para "abrir" a tela da despensa dentro do menu principal
        fun newInstance(page: String, position: Int, userId: Int) = TelaDespensaFragment().apply {
            arguments = Bundle().apply {
                putString("page", page)
                putInt("position", position)
                putInt("USER_ID", userId) // Lembra qual é o usuário que está logado [cite: 29]
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

    // Aqui é onde damos vida aos botões e listas depois que a tela aparece
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- PARTE 1: O BOTÃO DE FILTRAR (A BARRA AZUL) ---
        val cardFiltro = view.findViewById<View>(R.id.card_filtro_status)
        val txtStatus = view.findViewById<android.widget.TextView>(R.id.txt_status_selecionado)

        txtStatus.text = "Adicionados Recentemente"

        // Quando o usuário clica na barra de filtro, sobe uma listinha de opções
        cardFiltro.setOnClickListener {
            val popup = android.widget.PopupMenu(requireContext(), cardFiltro)

            // Opções que o usuário pode escolher para organizar a despensa
            popup.menu.add("Todos os Itens")
            popup.menu.add("Consumindo")
            popup.menu.add("Por consumir")
            popup.menu.add("Adicionados Recentemente")
            popup.menu.add("Vencido")
            popup.menu.add("Crítico")
            popup.menu.add("Atenção")
            popup.menu.add("Seguro")
            popup.menu.add("Alimentos e Bebidas")
            popup.menu.add("Higiene, Perfumaria, Farmácia e Limpeza")

            // Quando o usuário escolhe uma opção, a lista lá embaixo muda na hora
            popup.setOnMenuItemClickListener { item ->
                txtStatus.text = item.title
                when(item.title) {
                    "Todos os Itens" -> adapterGlobal?.submitList(listaCompleta.toList())
                    // As flags (1, 2, 3, 4) são as cores do semáforo de validade [cite: 29, 33]
                    "Vencido" -> adapterGlobal?.submitList(listaCompleta.filter { it.flag == 1 })
                    "Crítico" -> adapterGlobal?.submitList(listaCompleta.filter { it.flag == 2 })
                    "Atenção" -> adapterGlobal?.submitList(listaCompleta.filter { it.flag == 3 })
                    "Seguro" -> adapterGlobal?.submitList(listaCompleta.filter { it.flag == 4 })
                }
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

        // Aqui o app pega alguns produtos "de mentira" para a gente testar as telas [cite: 26]
        listaCompleta = sampleData().toMutableList()
        adapter.submitList(listaCompleta.toList())
        atualizarContadores(view) // Atualiza os números coloridos lá no topo

        // --- PARTE 3: O SEMÁFORO (BOTÕES COLORIDOS DO TOPO) --- [cite: 29, 33]
        // Se clicar no botão Marrom, mostra só os Vencidos (Flag 1)
        view.findViewById<View>(R.id.card_vencido).setOnClickListener {
            adapter.submitList(listaCompleta.filter { it.flag == 1 })
            txtStatus.text = "Vencido"
        }
        // Se clicar no botão Vermelho, mostra os Críticos (Flag 2)
        view.findViewById<View>(R.id.card_critico).setOnClickListener {
            adapter.submitList(listaCompleta.filter { it.flag == 2 })
            txtStatus.text = "Crítico"
        }
        // Se clicar no botão Amarelo, mostra os de Atenção (Flag 3)
        view.findViewById<View>(R.id.card_atencao).setOnClickListener {
            adapter.submitList(listaCompleta.filter { it.flag == 3 })
            txtStatus.text = "Atenção"
        }
        // Se clicar no botão Verde, mostra os Seguros (Flag 4)
        view.findViewById<View>(R.id.card_seguro).setOnClickListener {
            adapter.submitList(listaCompleta.filter { it.flag == 4 })
            txtStatus.text = "Seguro"
        }

        // --- PARTE 4: BOTÃO DE ADICIONAR (+) ---
        val btnAdd = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_add_item)
        btnAdd.setOnClickListener {
            // Abre a tela para cadastrar um novo alimento na despensa [cite: 29]
            val intent = android.content.Intent(requireContext(), AdicionarItem::class.java)
            startActivity(intent)
        }
    }

    // Esta função mostra o menu de "Editar", "Consumir" ou "Excluir" cada alimento
    private fun mostrarMenuOpcoes(anchorView: View, item: Item) {
        val popup = android.widget.PopupMenu(requireContext(), anchorView)
        popup.menu.add("Editar item")

        // Verifica se o item já está sendo usado para mudar o texto do botão
        val textoConsumo = if (item.estado == "Consumindo") "Por consumir" else "Consumir item"
        popup.menu.add(textoConsumo)

        // Esta opção retira o item da despensa para sugerir na aba de Doação [cite: 29, 33]
        popup.menu.add("Doar Item")
        popup.menu.add("Excluir item")

        popup.setOnMenuItemClickListener { menuIt ->
            when (menuIt.title) {
                "Editar item" -> {
                    // Manda as informações atuais do item para a tela de edição [cite: 29]
                    val intent = android.content.Intent(requireContext(), AdicionarItem::class.java)
                    intent.putExtra("ITEM_ID", item.id)
                    intent.putExtra("TITULO", item.titulo)
                    intent.putExtra("ESTADO", item.estado)
                    intent.putExtra("LOCAL", item.localNome)
                    intent.putExtra("DICA", item.dica)
                    startActivity(intent)
                }
                "Consumir item", "Por consumir" -> {
                    // Troca o estado do produto (ex: de fechado para aberto/usando)
                    val novoEstado = if (item.estado == "Consumindo") "Por consumir" else "Consumindo"
                    val index = listaCompleta.indexOf(item)
                    if (index != -1) {
                        listaCompleta[index] = item.copy(estado = novoEstado)
                        adapterGlobal?.submitList(listaCompleta.toList())
                    }
                }
                "Doar Item" -> {
                    // Remove da lista principal para o usuário doar [cite: 33]
                    listaCompleta.remove(item)
                    adapterGlobal?.submitList(listaCompleta.toList())
                    atualizarContadores(requireView())
                    Toast.makeText(requireContext(), "Item marcado para doação", Toast.LENGTH_SHORT).show()
                }
                "Excluir item" -> confirmarExclusao(item)
            }
            true
        }
        popup.show()
    }

    // Janela que pergunta o motivo antes de apagar o item da lista [cite: 29]
    private fun confirmarExclusao(item: Item) {
        val motivos = arrayOf("Item consumido", "Item vencido/estragado", "Item doado", "Outro")
        var motivoSelecionado = motivos[0]

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Excluir \"${item.titulo}\"")
            .setSingleChoiceItems(motivos, 0) { _, which ->
                motivoSelecionado = motivos[which]
            }
            .setPositiveButton("Confirmar Exclusão") { d, _ ->
                listaCompleta.remove(item) // Tira o produto da lista
                adapterGlobal?.submitList(listaCompleta.toList()) // Atualiza a tela
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

    // Esta função conta quantos produtos têm em cada cor do semáforo (1, 2, 3 ou 4) [cite: 33]
    private fun atualizarContadores(view: View) {
        val v = listaCompleta.count { it.flag == 1 } // Marrom (Vencido)
        val c = listaCompleta.count { it.flag == 2 } // Vermelho (Crítico)
        val a = listaCompleta.count { it.flag == 3 } // Amarelo (Atenção)
        val s = listaCompleta.count { it.flag == 4 } // Verde (Seguro)

        // Escreve os números nos cartões lá do topo da tela [cite: 33]
        view.findViewById<TextView>(R.id.txt_vencido_contador).text = "Vencido $v"
        view.findViewById<TextView>(R.id.txt_critico_contador).text = "Crítico $c"
        view.findViewById<TextView>(R.id.txt_atencao_contador).text = "Atenção $a"
        view.findViewById<TextView>(R.id.txt_seguro_contador).text = "Seguro $s"
    }

    // Estes são os produtos de teste que aparecem assim que você abre o app [cite: 26]
    private fun sampleData(): List<Item> {
        return listOf(
            Item(1, "Feijão", "Consumindo", "4 DIAS", "Armazenar na geladeira", 1, R.drawable.graos_feijao, R.drawable.local_icone_geladeira, "Geladeira"),
            Item(2, "Pão", "Por consumir", "15 DIAS", "Manter em recipiente fechado", 3, R.drawable.paes_massas_e_doces_pao_frances, R.drawable.local_icone_armario, "Armário"),
            Item(3, "Tomate", "Por consumir", "7 DIAS", "Lavar antes de guardar", 4, R.drawable.hortifruti_tomates, R.drawable.local_icone_fruteira, "Fruteira"),
            Item(4, "Leite Fermentando Xamito", "Consumindo", "1 DIA", "Consumir urgente", 2, R.drawable.laticinios_leite, R.drawable.local_icone_freezer, "Freezer")
        )
    }
}