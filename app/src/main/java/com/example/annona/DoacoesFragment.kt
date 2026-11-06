package com.example.annona

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

// Tela Doação: Lista itens próximos do vencimento (Crítico ou Atenção e fechados),
// permite retirar itens da lista de doação, copiar o texto para a área de transferência e compartilhar (RF15, RN12, RN13, B09, B16)
class DoacoesFragment : Fragment() {

    private val viewModel: ItensCompartilhadosViewModel by activityViewModels()
    private var adapterGlobal: AdaptadorItem? = null
    private var listaAtualDoacao: MutableList<Item> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_doacoes, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_doacoes)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val adapter = AdaptadorItem(
            onClick = { item ->
                confirmarRetirarDaDoacao(item)
            },
            onOptionsMenuClick = { anchorView, item ->
                mostrarMenuDoacao(anchorView, item)
            }
        )
        recycler.adapter = adapter
        adapterGlobal = adapter

        viewModel.itensDoacao.observe(viewLifecycleOwner) { lista ->
            val idsForaLocal = SecaoUsuario(requireContext()).obterIdsForaDoacao()
            val filtrada = lista.filter { it.foraDoacao == 0 && !idsForaLocal.contains(it.id) }
            listaAtualDoacao = filtrada.toMutableList()
            adapter.submitList(filtrada.toList())
        }

        val btnInfo = view.findViewById<TextView>(R.id.btn_info)
        val cardInfo = view.findViewById<View>(R.id.card_info)

        btnInfo.setOnClickListener {
            cardInfo.visibility = if (cardInfo.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        val btnRelatorio = view.findViewById<Button>(R.id.btn_gerar_lista_doacao)
        btnRelatorio.setOnClickListener {
            mostrarPopupRelatorio()
        }

        val btnCopiarDireto = view.findViewById<Button>(R.id.btn_copiar_lista_doacao)
        btnCopiarDireto?.setOnClickListener {
            val texto = gerarTextoDoacao()
            if (texto == null) {
                Toast.makeText(requireContext(), "Nenhum item disponível para doação.", Toast.LENGTH_SHORT).show()
            } else {
                copiarTextoParaClipboard(texto)
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        carregarDoacaoDaApi()
    }

    // B16: Busca a lista de doação diretamente da API caso a aba seja aberta ou atualizada
    private fun carregarDoacaoDaApi() {
        val ctx = context ?: return
        val userId = SecaoUsuario(ctx).obterUserId() ?: return
        val api = ApiClient.getService(ctx)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resp = try {
                    api.listarItensDoacao(userId)
                } catch (_: Exception) {
                    api.listarItens(userId)
                }

                val idsForaLocal = SecaoUsuario(requireContext()).obterIdsForaDoacao()
                val candidatos = resp.listaItens
                    .onEach { it.mapearCampos(requireContext()) }
                    .filter {
                        (it.flag == 2 || it.flag == 3) &&
                            it.consumindo == 0 &&
                            it.foraDoacao == 0 &&
                            !idsForaLocal.contains(it.id)
                    }
                    .sortedWith(compareBy<Item> { it.flag }.thenBy { it.diasRestantes ?: Int.MAX_VALUE })

                listaAtualDoacao = candidatos.toMutableList()
                viewModel.atualizarItensParaDoacao(candidatos)
                adapterGlobal?.submitList(candidatos.toList())
            } catch (_: Exception) {
                // Mantém o que já está no ViewModel caso não haja conexão no momento
            }
        }
    }

    private fun mostrarMenuDoacao(anchorView: View, item: Item) {
        val popup = PopupMenu(requireContext(), anchorView)
        popup.menu.add("Retirar da lista de doação")
        popup.menu.add("Marcar como doado")

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title) {
                "Retirar da lista de doação" -> retirarItemDaDoacao(item)
                "Marcar como doado" -> marcarItemComoDoado(item)
            }
            true
        }
        popup.show()
    }

    private fun confirmarRetirarDaDoacao(item: Item) {
        AlertDialog.Builder(requireContext())
            .setTitle(item.nome)
            .setMessage("Deseja retirar \"${item.nome}\" da lista de doação ou marcar que ele já foi doado?")
            .setPositiveButton("Retirar da doação") { d, _ ->
                retirarItemDaDoacao(item)
                d.dismiss()
            }
            .setNeutralButton("Já foi doado") { d, _ ->
                marcarItemComoDoado(item)
                d.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // RF15 / RN13: Retira o item da lista de doação e persiste no servidor e localmente
    private fun retirarItemDaDoacao(item: Item) {
        val ctx = requireContext()
        val session = SecaoUsuario(ctx)
        session.marcarItemForaDoacao(item.id, true)

        listaAtualDoacao.removeAll { it.id == item.id }
        viewModel.atualizarItensParaDoacao(listaAtualDoacao.toList())
        adapterGlobal?.submitList(listaAtualDoacao.toList())

        val userId = session.obterUserId() ?: return
        val api = ApiClient.getService(ctx)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                api.alterarDisponibilidadeDoacao(item.id, BodyAlterarDoacao(userId, true))
            } catch (_: Exception) {
            }
        }

        Toast.makeText(ctx, "\"${item.nome}\" retirado da lista de doação", Toast.LENGTH_SHORT).show()
    }

    // RF21: Marca o item como "Item doado" (exclusão lógica) para registrar no Histórico
    private fun marcarItemComoDoado(item: Item) {
        val ctx = requireContext()
        val userId = SecaoUsuario(ctx).obterUserId()
        val api = ApiClient.getService(ctx)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resp = api.deletarItemUsuario(item.id.toInt(), BodyDeletarItem("Item doado", userId))
                if (resp.isSuccessful) {
                    listaAtualDoacao.removeAll { it.id == item.id }
                    viewModel.atualizarItensParaDoacao(listaAtualDoacao.toList())
                    adapterGlobal?.submitList(listaAtualDoacao.toList())
                    LocalBroadcastManager.getInstance(ctx).sendBroadcast(Intent("ATUALIZAR_LISTA_ANONA"))
                    Toast.makeText(ctx, "\"${item.nome}\" registrado como doado!", Toast.LENGTH_SHORT).show()
                }
            } catch (_: Exception) {
                Toast.makeText(ctx, "Erro ao conectar com o servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun gerarTextoDoacao(): String? {
        val itens = if (listaAtualDoacao.isNotEmpty()) listaAtualDoacao else (viewModel.itensDoacao.value ?: emptyList())
        if (itens.isEmpty()) return null

        return buildString {
            append(
                "Olá! 😊\n" +
                    "Estou organizando minha despensa e separei alguns alimentos que não irei usar. " +
                    "Se alguém quiser ou souber de quem precise, é só me avisar:\n\n"
            )
            itens.forEach { item ->
                val p = item.prazo?.trim().orEmpty()
                val prazoInfo = if (p.isNotEmpty()) " (vence em ${p.lowercase()})" else ""
                append("• ${item.nome}$prazoInfo\n")
            }
        }.trim()
    }

    // B09: Copia o texto de fato para a área de transferência do Android (ClipboardManager)
    private fun copiarTextoParaClipboard(texto: String) {
        val ctx = requireContext()
        val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Lista de Doação Annona", texto)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(
            ctx,
            "Texto copiado para a área de transferência!",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Gera texto pronto, copia para o ClipboardManager (B09) e permite compartilhar (RF15)
    private fun mostrarPopupRelatorio() {
        val ctx = requireContext()
        val mensagem = gerarTextoDoacao()

        if (mensagem == null) {
            AlertDialog.Builder(ctx)
                .setTitle("Itens para doação")
                .setMessage("Nenhum item disponível para doação.")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val container = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 40, 48, 32)
        }

        val txtTitulo = TextView(ctx).apply {
            text = "Lista de Itens para Doação"
            textSize = 18f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(ctx, R.color.marrom_escuro))
        }

        val scrollMensagem = ScrollView(ctx).apply {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 24, 0, 28)
            layoutParams = params
        }

        val txtMensagem = TextView(ctx).apply {
            text = mensagem
            textSize = 14f
            setTextColor(Color.parseColor("#333333"))
            setBackgroundColor(Color.parseColor("#FAF6F0"))
            setPadding(28, 24, 28, 24)
            setTextIsSelectable(true)
        }
        scrollMensagem.addView(txtMensagem)

        val btnCopiarPopup = Button(ctx).apply {
            text = "📋 Copiar texto para a área de transferência"
            isAllCaps = false
            setTextColor(Color.WHITE)
            setBackgroundColor(ContextCompat.getColor(ctx, R.color.verde_abacate))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = 16
            layoutParams = params
        }

        val linhaBotoes = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
        }

        val btnCompartilhar = Button(ctx).apply {
            text = "Compartilhar"
            isAllCaps = false
            setTextColor(ContextCompat.getColor(ctx, R.color.marrom_cafe))
            setBackgroundColor(Color.TRANSPARENT)
        }

        val btnFechar = Button(ctx).apply {
            text = "Fechar"
            isAllCaps = false
            setTextColor(Color.GRAY)
            setBackgroundColor(Color.TRANSPARENT)
        }

        linhaBotoes.addView(btnCompartilhar)
        linhaBotoes.addView(btnFechar)

        container.addView(txtTitulo)
        container.addView(scrollMensagem)
        container.addView(btnCopiarPopup)
        container.addView(linhaBotoes)

        val dialog = AlertDialog.Builder(ctx)
            .setView(container)
            .create()

        btnCopiarPopup.setOnClickListener {
            copiarTextoParaClipboard(mensagem)
            btnCopiarPopup.text = "✓ Texto copiado!"
        }

        btnCompartilhar.setOnClickListener {
            copiarTextoParaClipboard(mensagem)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, mensagem)
            }
            startActivity(Intent.createChooser(shareIntent, "Compartilhar lista de doação"))
        }

        btnFechar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_dialog_arredondado)
    }
}
