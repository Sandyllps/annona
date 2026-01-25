package com.example.annona

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager

class ReceitasFragment : Fragment() {

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


        return view
    }

}
