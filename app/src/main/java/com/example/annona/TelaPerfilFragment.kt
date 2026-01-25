package com.example.annona

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.navigation.fragment.findNavController
import kotlin.jvm.java

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
            val status = if (isChecked) "Ativadas" else "Desativadas"
            Toast.makeText(requireContext(), "Notificações $status", Toast.LENGTH_SHORT).show()
        }

        // Cliques
        btnHistorico.setOnClickListener {
            Toast.makeText(requireContext(), "Abrindo Histórico...", Toast.LENGTH_SHORT).show()
        }

        btnAjustes.setOnClickListener {
            Toast.makeText(requireContext(), "Abrindo Ajustes...", Toast.LENGTH_SHORT).show()
        }

        // Sair do app e voltar para a tela de login
        btnSair.setOnClickListener {
            val intent = Intent(requireContext(), TelaLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Fecha o APP
        btnFechar.setOnClickListener {
            findNavController().popBackStack()

        }
    }
}