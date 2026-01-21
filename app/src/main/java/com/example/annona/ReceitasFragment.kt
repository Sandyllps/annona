package com.example.annona

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

        val receitasMock = listOf(
            Receita(
                nome = "Feijoada",
                ingredientesDisponiveis = "Você tem: Feijão | Couve",
                tempoPreparo = "40 MIN",
                tag = "PRINCIPAL",
                imagem = R.drawable.ic_launcher_foreground
            ),
            Receita(
                nome = "Arroz Branco",
                ingredientesDisponiveis = "Você tem: Arroz",
                tempoPreparo = "20 MIN",
                tag = "ACOMPANHAMENTO",
                imagem = R.drawable.ic_launcher_foreground
            )
        )

        recyclerView.adapter = ReceitasAdapter(receitasMock) {

            val detalheFragment = DetalheReceitaFragment()

            (requireActivity() as MainActivity)
                .abrirFragment(detalheFragment)
        }


        return view
    }
}
