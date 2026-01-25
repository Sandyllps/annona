package com.example.annona

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager

class ReceitasFragment : Fragment() {

    private var listaCompleta: List<Receita> = emptyList()
    private var adapterGlobal: ReceitasAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_receitas, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_receitas)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val receitas = ReceitasData.receitas

        //Abrir tela de detalhes das receitas
        recyclerView.adapter = ReceitasAdapter(listaReceitas = receitas) { receita ->
            val intent = Intent(requireContext(), DetalheReceitaActivity::class.java)

            intent.putExtra("NOME", receita.nome)

            startActivity(intent)
        }

        // Filtro de receitas

        listaCompleta = ReceitasData.receitas

        val adapter = ReceitasAdapter(listaReceitas = listaCompleta) { receita ->
            val intent = Intent(requireContext(), DetalheReceitaActivity::class.java)
            intent.putExtra("NOME", receita.nome)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        adapterGlobal = adapter

        val cardFiltro = view.findViewById<View>(R.id.card_filtro_receitas)
        val txtStatus = view.findViewById<TextView>(R.id.txt_filtro_selecionado)

        txtStatus.text = "Todos"

        cardFiltro.setOnClickListener {
            val popup = android.widget.PopupMenu(requireContext(), cardFiltro)

            popup.menu.add("Todos")
            popup.menu.add("Prato: Principal")
            popup.menu.add("Acompanhamento")
            popup.menu.add("Entrada")
            popup.menu.add("Sobremesa")

            popup.setOnMenuItemClickListener { item ->
                txtStatus.text = item.title

                val listaFiltrada = when (item.title.toString()) {
                    "Todos" -> listaCompleta
                    "Prato: Principal" -> listaCompleta.filter { it.tag == "PRINCIPAL" }
                    "Acompanhamento" -> listaCompleta.filter { it.tag == "ACOMPANHAMENTO" }
                    "Entrada" -> listaCompleta.filter { it.tag == "ENTRADA" }
                    "Sobremesa" -> listaCompleta.filter { it.tag == "SOBREMESA" }
                    else -> listaCompleta
                }

                recyclerView.adapter =
                    ReceitasAdapter(listaFiltrada) { receita ->
                        val intent = Intent(requireContext(), DetalheReceitaActivity::class.java)
                        intent.putExtra("NOME", receita.nome)
                        startActivity(intent)
                    }

                true
            }

            popup.show()
        }




        return view
    }

}
