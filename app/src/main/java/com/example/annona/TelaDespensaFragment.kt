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

class TelaDespensaFragment : Fragment() {

    private var listaCompleta: MutableList<Item> = mutableListOf()
    private var adapterGlobal: AdaptadorItem? = null

    companion object {
        fun newInstance(key: String, pos: Int) = TelaDespensaFragment().apply {
            arguments = Bundle().apply {
                putInt("pos", pos)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tela_despensa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Configuração do FILTRO (Pop-up)
        val cardFiltro = view.findViewById<View>(R.id.card_filtro_status)
        val txtStatus = view.findViewById<android.widget.TextView>(R.id.txt_status_selecionado)

        txtStatus.text = "Adicionados Recentemente"

        cardFiltro.setOnClickListener {
            val popup = android.widget.PopupMenu(requireContext(), cardFiltro)

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

            popup.setOnMenuItemClickListener { item ->
                txtStatus.text = item.title
                when(item.title) {
                    "Todos os Itens" -> adapterGlobal?.submitList(listaCompleta.toList())
                    "Vencido" -> adapterGlobal?.submitList(listaCompleta.filter { it.flag == 1 })
                    "Crítico" -> adapterGlobal?.submitList(listaCompleta.filter { it.flag == 2 })
                    "Atenção" -> adapterGlobal?.submitList(listaCompleta.filter { it.flag == 3 })
                    "Seguro" -> adapterGlobal?.submitList(listaCompleta.filter { it.flag == 4 })
                }
                true
            }
            popup.show()
        }

        // 2. Configuração da LISTA (RecyclerView)
        val rv = view.findViewById<RecyclerView>(R.id.rvItems)
        rv.layoutManager = LinearLayoutManager(requireContext())

        val adapter = AdaptadorItem(
            onClick = { item -> /* Clique no card inteiro */ },
            onOptionsMenuClick = { anchorView, item ->
                mostrarMenuOpcoes(anchorView, item)
            }
        )

        rv.adapter = adapter
        adapterGlobal = adapter

        listaCompleta = sampleData().toMutableList()
        adapter.submitList(listaCompleta.toList())
        atualizarContadores(view)

        // --- CLIQUES NO SEMÁFORO ---
        view.findViewById<View>(R.id.card_vencido).setOnClickListener {
            adapter.submitList(listaCompleta.filter { it.flag == 1 })
            txtStatus.text = "Vencido"
        }
        view.findViewById<View>(R.id.card_critico).setOnClickListener {
            adapter.submitList(listaCompleta.filter { it.flag == 2 })
            txtStatus.text = "Crítico"
        }
        view.findViewById<View>(R.id.card_atencao).setOnClickListener {
            adapter.submitList(listaCompleta.filter { it.flag == 3 })
            txtStatus.text = "Atenção"
        }
        view.findViewById<View>(R.id.card_seguro).setOnClickListener {
            adapter.submitList(listaCompleta.filter { it.flag == 4 })
            txtStatus.text = "Seguro"
        }

        val btnAdd = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_add_item)
        btnAdd.setOnClickListener {
            val intent = android.content.Intent(requireContext(), AdicionarItem::class.java)
            startActivity(intent)
        }
    }

    private fun mostrarMenuOpcoes(anchorView: View, item: Item) {
        val popup = android.widget.PopupMenu(requireContext(), anchorView)
        popup.menu.add("Editar item")
        val textoConsumo = if (item.estado == "Consumindo") "Por consumir" else "Consumir item"
        popup.menu.add(textoConsumo)
        popup.menu.add("Item Consumido")
        popup.menu.add("Doar Item")
        popup.menu.add("Jogar fora")
        popup.menu.add("Excluir item")

        popup.setOnMenuItemClickListener { menuIt ->
            when (menuIt.title) {
                "Editar item" -> {
                    val intent = android.content.Intent(requireContext(), AdicionarItem::class.java)
                    startActivity(intent)
                }
                "Consumir item", "Por consumir" -> {
                    val novoEstado = if (item.estado == "Consumindo") "Por consumir" else "Consumindo"
                    val index = listaCompleta.indexOf(item)
                    if (index != -1) {
                        listaCompleta[index] = item.copy(estado = novoEstado)
                        adapterGlobal?.submitList(listaCompleta.toList())
                    }
                }
                "Excluir item" -> confirmarExclusao(item)
                "Item Consumido" -> Toast.makeText(requireContext(), "Enviado para o histórico", Toast.LENGTH_SHORT).show()
                "Doar Item" -> Toast.makeText(requireContext(), "Item marcado para doação", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(requireContext(), "Ação: ${menuIt.title}", Toast.LENGTH_SHORT).show()
            }
            true
        }
        popup.show()
    }

    private fun confirmarExclusao(item: Item) {
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Excluir Item")
            .setMessage("Deseja realmente excluir \"${item.titulo}\"?")
            .setPositiveButton("Excluir") { d, _ ->
                listaCompleta.remove(item)
                adapterGlobal?.submitList(listaCompleta.toList())
                atualizarContadores(requireView())
                d.dismiss()
            }
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .create()

        dialog.show()
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.vermelho_murango))
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(android.graphics.Color.GRAY)
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

    // LISTA ATUALIZADA COM OS NOMES DOS LOCAIS PARA O TOAST
    private fun sampleData(): List<Item> {
        return listOf(
            Item(1, "Feijão", "Consumindo", "4 DIAS", "Armazenar na geladeira", 1, R.drawable.graos_feijao, R.drawable.local_icone_geladeira, "Geladeira"),
            Item(2, "Pão", "Por consumir", "15 DIAS", "Manter em recipiente fechado", 3, R.drawable.paes_massas_e_doces_pao_frances, R.drawable.local_icone_armario, "Armário"),
            Item(3, "Tomate", "Por consumir", "7 DIAS", "Lavar antes de guardar", 4, R.drawable.hortifruti_tomates, R.drawable.local_icone_fruteira, "Fruteira"),
            Item(4, "Leite Fermentando Xamito", "Consumindo", "1 DIA", "Consumir urgente", 2, R.drawable.laticinios_leite, R.drawable.local_icone_freezer, "Freezer")
        )
    }
}