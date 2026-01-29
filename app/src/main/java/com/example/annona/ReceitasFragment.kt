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

    // criação de variável que armazena a lista completa de receitas
    private var listaCompleta: List<Receita> = emptyList()

    // criação de variável que armazena o adapter
    // O adapter é responsável por transformar cada objeto Receita em um item visual exibido na tela
    private var adapterGlobal: ReceitasAdapter? = null

    // função que cria a interface visual do fragment, onde é cónectado com layout feino no xml
    // fragment_receitas.xml
    override fun onCreateView(
        inflater: LayoutInflater,       // transforma o xml em tela visível
        container: ViewGroup?,          // local onde será guardado o fragment
        savedInstanceState: Bundle?     // guarda estado dos dados da tela
    ): View {

        // Transforma o arquivo em uma view real
        val view = inflater.inflate(R.layout.fragment_receitas, container, false)

        // Procura no layout o recyclerView responsável por exibir as receitas
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_receitas)
        // Exibe os itens da lista
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val receitas = ReceitasData.receitas  // Recebe as informações dos objetos do arquivo receitas.data

        // Define o adapter inicial do RecyclerView
        // Será executado quando o usuário clicar em uma receita
        recyclerView.adapter = ReceitasAdapter(listaReceitas = receitas) { receita ->
            // Cria um Intent para abrir a tela de detalhes da receita
            val intent = Intent(requireContext(), DetalheReceitaActivity::class.java)

            // Passa o nome da receita para a tela de detalhes
            intent.putExtra("NOME", receita.nome)

            // Inicia a Activity com os dados passados
            startActivity(intent)
        }

        // Filtro de receitas

        // Recebe a lista de receitas, usada como referência para aplicar os filtros
        listaCompleta = ReceitasData.receitas

        // Criação do adapter principal que será usado para exibir as receitas na tela
        val adapter = ReceitasAdapter(listaReceitas = listaCompleta) { receita ->
            // Cria um Intent para abrir a tela de detalhes da receita
            val intent = Intent(requireContext(), DetalheReceitaActivity::class.java)
            intent.putExtra("NOME", receita.nome)
            startActivity(intent)
        }

        recyclerView.adapter = adapter  // Define o adapter do RecyclerView
        adapterGlobal = adapter         // Armazena o adapter em uma variável global

        // Procura no layout o cardView responsável por exibir o filtro de receitas
        val cardFiltro = view.findViewById<View>(R.id.card_filtro_receitas)
        // Procura no layout o TextView responsável por exibir o status atual do filtro de receitas
        val txtStatus = view.findViewById<TextView>(R.id.txt_filtro_selecionado)

        txtStatus.text = "Todos"    // Define o status inicial do filtro

        cardFiltro.setOnClickListener {     // Define a ação que será executada quando o card for clicado
            // Cria um popup menu com as opções de filtro
            val popup = android.widget.PopupMenu(requireContext(), cardFiltro)

            // Adiciona as opções de filtro ao popup menu
            popup.menu.add("Todos")
            popup.menu.add("Prato: Principal")
            popup.menu.add("Acompanhamento")
            popup.menu.add("Entrada")
            popup.menu.add("Sobremesa")

            // Define a ação que será executada quando uma opção for selecionada
            popup.setOnMenuItemClickListener { item ->
                txtStatus.text = item.title

                // Aplica o filtro com base na tag de cada receita
                val listaFiltrada = when (item.title.toString()) {
                    "Todos" -> listaCompleta
                    "Prato: Principal" -> listaCompleta.filter { it.tag == "PRINCIPAL" }
                    "Acompanhamento" -> listaCompleta.filter { it.tag == "ACOMPANHAMENTO" }
                    "Entrada" -> listaCompleta.filter { it.tag == "ENTRADA" }
                    "Sobremesa" -> listaCompleta.filter { it.tag == "SOBREMESA" }
                    else -> listaCompleta
                }

                // Atualiza o adapter do RecyclerView com a lista filtrada
                recyclerView.adapter =
                    ReceitasAdapter(listaFiltrada) { receita ->
                        // Cria um Intent para abrir a tela de detalhes da receita
                        val intent = Intent(requireContext(), DetalheReceitaActivity::class.java)
                        intent.putExtra("NOME", receita.nome)
                        startActivity(intent)
                    }

                true
            }

            popup.show()   // Exibe o filtro na tela
        }

        return view     // Retorna a view final do fragment para ser exibida na tela

    }

}
