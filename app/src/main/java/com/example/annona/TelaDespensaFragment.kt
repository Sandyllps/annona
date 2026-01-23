package com.example.annona

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TelaDespensaFragment : Fragment() {

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

        // Define o texto inicial como "Adicionados Recentemente". Esse será o filtro pré selecionado quando o usuário logado abrir o app.
        txtStatus.text = "Adicionados Recentemente"

        cardFiltro.setOnClickListener {
            // Esta linha cria o popup
            val popup = android.widget.PopupMenu(requireContext(), cardFiltro)

            // As 10 opções de filtro
            popup.menu.add("Todos os Itens")
            popup.menu.add("Consumindo")
            popup.menu.add("Por consumir")
            popup.menu.add("Adicionados Recentemente")
            popup.menu.add("Vencidos")
            popup.menu.add("Críticos")
            popup.menu.add("Atenção")
            popup.menu.add("Seguros")
            popup.menu.add("Alimentos e Bebidas")
            popup.menu.add("Higiene, Perfumaria, Farmácia e Limpeza")

            popup.setOnMenuItemClickListener { item ->
                txtStatus.text = item.title // Atualiza o visual sem mudar a fonte/cor
                true
            }
            popup.show()
        }

        // 2. Configuração da LISTA (RecyclerView)
        val rv = view.findViewById<RecyclerView>(R.id.rvItems)
        rv.layoutManager = LinearLayoutManager(requireContext())
        val adapter = AdaptadorItem()
        rv.adapter = adapter
        adapter.submitList(sampleData())

        // Configuração de click para adicionar item. Ao clicar no botão adiiconar, o usuário irá para a activity_adicionar_item.xml
        val btnAdd = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_add_item)



        //ligação com a tela de receitas
        val lista = sampleData()
        adapter.submitList(lista)

        // EXPÕE A DESPENSA PARA O APP (Receitas, futuro backend, etc)
        DespensaRepository.setItens(lista)



        btnAdd.setOnClickListener {
            // Aqui criei  a Intent para navegar do Fragment para a Activity
            val intent = android.content.Intent(requireContext(), AdicionarItem::class.java)
            startActivity(intent)
        }
    }

    private fun sampleData(): List<Item> {
        return listOf(
            Item(
                1,
                "Feijão",
                "Consumindo",
                "4 DIAS",
                "Armazenar na geladeira",
                1,
                R.drawable.hortifruti_tomate
            ),
            Item(
                2,
                "Pão",
                "Por consumir",
                "15 DIAS",
                "Manter em recipiente fechado",
                3,
                R.drawable.item_padrao
            )
        )
    }
}