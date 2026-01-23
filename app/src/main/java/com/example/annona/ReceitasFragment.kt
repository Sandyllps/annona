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

        val receitasMock = listOf(
            Receita(
                id = 1,
                nome = "Feijoada",
                ingredientes = listOf("Feijão", "Couve"),
                tempoPreparo = "40 MIN",
                tag = "PRINCIPAL",
                imagem = R.drawable.ic_launcher_foreground
            ),
            Receita(
                id = 2,
                nome = "Arroz Branco",
                ingredientes = listOf("Arroz"),
                tempoPreparo = "20 MIN",
                tag = "ACOMPANHAMENTO",
                imagem = R.drawable.ic_launcher_foreground
            )
        )

        val ingredientesUsuario = listOf(
            "Feijão",
            "Arroz"
        )



        //Abrir tela de detalhes das receitas
        recyclerView.adapter = ReceitasAdapter(listaReceitas = receitasMock, ingredientesUsuario = ingredientesUsuario) { receita ->
            val intent = Intent(requireContext(), DetalheReceitaActivity::class.java)
            intent.putExtra("NOME", receita.nome)
            intent.putExtra("TEMPO", receita.tempoPreparo)
            intent.putExtra("TAG", receita.tag)
            intent.putStringArrayListExtra(
                "INGREDIENTES",
                ArrayList(receita.ingredientes)
            )
            startActivity(intent)
        }

        return view
    }

}
