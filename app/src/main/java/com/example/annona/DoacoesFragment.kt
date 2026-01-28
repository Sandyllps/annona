package com.example.annona

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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

        // RecyclerView
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_doacoes)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val adapter = AdaptadorItem()
        recycler.adapter = adapter

        // Observa a lista de itens para doação
        viewModel.itensDoacao.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
        }

        //Ana - lógica da visibilidade do botão de informação
        // Se o texto estiver escondido → mostra
        // Se estiver visível → esconde
        val btnInfo = view.findViewById<TextView>(R.id.btn_info)
        val cardInfo = view.findViewById<View>(R.id.card_info)

        btnInfo.setOnClickListener {
            if (cardInfo.visibility == View.GONE) {
                cardInfo.visibility = View.VISIBLE
            } else {
                cardInfo.visibility = View.GONE
            }
        }

        // BOTÃO(Relatório)

        val btnRelatorio = view.findViewById<Button>(R.id.btn_gerar_lista_doacao)

        btnRelatorio.setOnClickListener {
            mostrarPopupRelatorio()
        }

        return view
    }
    // POPUP DE RELATÓRIO
    private fun mostrarPopupRelatorio() {

        val itens = viewModel.itensDoacao.value

        // Caso não tenha itens
        if (itens.isNullOrEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("Itens para doação")
                .setMessage("Nenhum item disponível para doação.")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        // Monta mensagem automática
        val mensagem = buildString {
            append("Oi, gente!        \n" +
                    "Tô organizando aqui em casa e tenho alguns itens que quero doar. \n" +
                    "Se alguém quiser ou souber de quem precise, é só me avisar:\n\n")
            itens.forEach { item ->
                append("• ${item.nome}\n")
            }
        }

        // Exibe popup
        AlertDialog.Builder(requireContext())
            .setTitle("Itens para doação")
            .setMessage(mensagem)
            .setPositiveButton("Copiar") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "Copiado!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }
}

