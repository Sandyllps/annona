package com.example.annona

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat

class TelaPerfilFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tela_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referências
        val btnSair = view.findViewById<View>(R.id.btn_sair_perfil)
        val btnHistorico = view.findViewById<View>(R.id.btn_historico_perfil)
        val btnAjustes = view.findViewById<View>(R.id.btn_ajustes_perfil)
        val btnFechar = view.findViewById<View>(R.id.btn_fechar_perfil)
        val switchNotif = view.findViewById<SwitchCompat>(R.id.switch_notificacoes_perfil)

        // Switch Bolinha Invertida
        switchNotif.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "Desativadas" else "Ativadas"
            Toast.makeText(requireContext(), "Notificações $status", Toast.LENGTH_SHORT).show()
        }

        // Cliques
        btnHistorico.setOnClickListener {
            Toast.makeText(requireContext(), "Abrindo Histórico...", Toast.LENGTH_SHORT).show()
        }

        btnAjustes.setOnClickListener {
            Toast.makeText(requireContext(), "Abrindo Ajustes...", Toast.LENGTH_SHORT).show()
        }

        btnSair.setOnClickListener {
            activity?.finish()
        }

        btnFechar.setOnClickListener {
            // Ação para fechar ou voltar para a despensa
            Toast.makeText(requireContext(), "Voltando para a despensa", Toast.LENGTH_SHORT).show()
        }
    }
}