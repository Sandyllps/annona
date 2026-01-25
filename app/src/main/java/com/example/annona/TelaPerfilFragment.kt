package com.example.annona

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat

// Esta classe controla a tela de "Perfil" do usuário dentro do app
class TelaPerfilFragment : Fragment() {

    // Aqui o app carrega o desenho XML que eu fiz pro perfil
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tela_perfil, container, false)
    }

    // Aqui é onde damos funções aos botões e ao interruptor (switch) da tela
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- REFERÊNCIAS ---
        // Aqui o código localiza cada botão que eu desenhei no XML pelos IDs dele
        val btnSair = view.findViewById<View>(R.id.btn_sair_perfil)
        val btnHistorico = view.findViewById<View>(R.id.btn_historico_perfil)
        val btnAjustes = view.findViewById<View>(R.id.btn_ajustes_perfil)
        val btnFechar = view.findViewById<View>(R.id.btn_fechar_perfil)
        val switchNotif = view.findViewById<SwitchCompat>(R.id.switch_notificacoes_perfil)

        // --- LÓGICA DO INTERRUPTOR (NOTIFICAÇÕES) ---
        // Este é aquele botão de "deslizar". Ele avisa se as notificações estão ligadas ou desligadas.
        switchNotif.setOnCheckedChangeListener { _, isChecked ->
            // Se o usuário marcar, ele mostra um aviso de "Desativadas" ou "Ativadas"
            val status = if (isChecked) "Desativadas" else "Ativadas"
            Toast.makeText(requireContext(), "Notificações $status", Toast.LENGTH_SHORT).show()
        }

        // --- LÓGICA DOS CLIQUES ---

        // Quando clica em "Histórico", aparece um aviso na tela
        btnHistorico.setOnClickListener {
            Toast.makeText(requireContext(), "Abrindo Histórico...", Toast.LENGTH_SHORT).show()
        }

        // Quando clica em "Ajustes", aparece um aviso confirmando a ação
        btnAjustes.setOnClickListener {
            Toast.makeText(requireContext(), "Abrindo Ajustes...", Toast.LENGTH_SHORT).show()
        }

        // O botão "Sair" fecha o aplicativo por completo
        btnSair.setOnClickListener {
            activity?.finish()
        }

        // O botão de "X" ou fechar avisa que o usuário está voltando para a tela inicial
        btnFechar.setOnClickListener {
            // No futuro, aqui pode ser colocada a função real de trocar de aba
            Toast.makeText(requireContext(), "Voltando para a despensa", Toast.LENGTH_SHORT).show()
        }
    }
}