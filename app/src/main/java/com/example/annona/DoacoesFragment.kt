package com.example.annona

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DoacoesFragment : Fragment() {

    private val viewModel: ItensCompartilhadosViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_doacoes, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_doacoes)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        // adapter que vai mostrar os itens
        val adapter = AdaptadorItem()
        recycler.adapter = adapter

        viewModel.itensDoacao.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
        }


        //Ana - lógica da visibilidade do botão de informação
        //Se o texto estiver escondido → mostra
        //Se estiver visível → esconde

        val btnInfo = view.findViewById<TextView>(R.id.btn_info)
        val cardInfo = view.findViewById<View>(R.id.card_info)

        btnInfo.setOnClickListener {

            if (cardInfo.visibility == View.GONE) {
                cardInfo.visibility = View.VISIBLE
            } else {
                cardInfo.visibility = View.GONE
            }

        }




        return view
    }
}
