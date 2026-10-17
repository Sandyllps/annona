package com.example.annona

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class TelaPerfilFragment : Fragment() {

    private val viewModel: ItensCompartilhadosViewModel by activityViewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            NotificacaoValidadeHelper.verificarENotificar(
                requireContext(),
                viewModel.itensDespensa.value ?: emptyList(),
                forcarEnvio = true
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tela_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val session = SecaoUsuario(requireContext())

        val btnSair = view.findViewById<View>(R.id.btn_sair_perfil)
        val btnHistorico = view.findViewById<View>(R.id.btn_historico_perfil)
        val btnAjustes = view.findViewById<View>(R.id.btn_ajustes_perfil)
        val btnFechar = view.findViewById<View>(R.id.btn_fechar_perfil)
        val switchNotif = view.findViewById<SwitchCompat>(R.id.switch_notificacoes_perfil)
        val textEmail = view.findViewById<TextView>(R.id.txt_email)
        val textNome = view.findViewById<TextView>(R.id.txt_nome_perfil)

        textEmail.text = session.obterEmail()
        textNome?.text = session.obterNome() ?: "Meu Perfil"

        // RF16 / RN15: Estado salvo das notificações
        switchNotif.isChecked = session.notificacoesAtivas()

        switchNotif.setOnCheckedChangeListener { _, isChecked ->
            session.salvarNotificacoesAtivas(isChecked)
            val status = if (isChecked) "Ativadas" else "Desativadas"
            Toast.makeText(requireContext(), "Notificações $status", Toast.LENGTH_SHORT).show()

            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    NotificacaoValidadeHelper.verificarENotificar(
                        requireContext(),
                        viewModel.itensDespensa.value ?: emptyList(),
                        forcarEnvio = true
                    )
                }
            } else {
                NotificacaoValidadeHelper.cancelarNotificacoes(requireContext())
            }

            // Salva a preferência no servidor
            val userId = session.obterUserId()
            if (userId != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        ApiClient.getService(requireContext())
                            .atualizarNotificacoes(userId, BodyNotificacoes(isChecked))
                    } catch (_: Exception) {
                    }
                }
            }
        }

        // RF20 / RF21: Abre a tela de Histórico
        btnHistorico.setOnClickListener {
            val intent = Intent(requireContext(), HistoricoActivity::class.java)
            startActivity(intent)
        }

        // RF20 / B18: Abre os Ajustes (permite configurar URL da API para emulador/Wi-Fi/ngrok e restaurar doações)
        btnAjustes.setOnClickListener {
            mostrarDialogoAjustes()
        }

        // RF03: Sair do app e voltar para a tela de login
        btnSair.setOnClickListener {
            session.logout()
            val intent = Intent(requireContext(), TelaLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // B10: Ao tocar no X do Perfil, volta para a aba Despensa sem travar o app (sem chamar findNavController)
        btnFechar.setOnClickListener {
            (activity as? MainActivity)?.irParaDespensa()
        }
    }

    private fun mostrarDialogoAjustes() {
        val ctx = requireContext()
        val session = SecaoUsuario(ctx)
        val urlPadrao = ctx.getString(R.string.api_base_url)
        val urlAtual = session.obterBaseUrl(urlPadrao)

        val container = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 16)
        }

        val label = TextView(ctx).apply {
            text = "Endereço do servidor (API Base URL):\nEx: http://10.0.2.2:8080/ (Emulador) ou endereço do ngrok"
            textSize = 13f
        }

        val inputUrl = EditText(ctx).apply {
            setText(urlAtual)
            hint = "https://..."
        }

        container.addView(label)
        container.addView(inputUrl)

        AlertDialog.Builder(ctx)
            .setTitle("Ajustes do Aplicativo")
            .setView(container)
            .setPositiveButton("Salvar") { _, _ ->
                val novaUrl = inputUrl.text.toString().trim()
                if (novaUrl.isNotEmpty()) {
                    session.salvarBaseUrl(novaUrl)
                    Toast.makeText(ctx, "Endereço da API atualizado!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNeutralButton("Padrão") { _, _ ->
                session.salvarBaseUrl(urlPadrao)
                Toast.makeText(ctx, "Endereço padrão restaurado!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}