package com.example.annona

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
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
import java.text.SimpleDateFormat
import java.util.Locale

// Tela principal da Despensa, onde os alimentos aparecem listados e priorizados pelo semáforo
class TelaDespensaFragment : Fragment() {

    private var listaCompleta: MutableList<Item> = mutableListOf()
    private var adapterGlobal: AdaptadorItem? = null
    private var filtroAtual: String = "Todos os Itens"
    private val viewModel: ItensCompartilhadosViewModel by activityViewModels()

    companion object {
        fun newInstance(page: String, position: Int, userId: Int) = TelaDespensaFragment().apply {
            arguments = Bundle().apply {
                putString("page", page)
                putInt("position", position)
                putInt("USER_ID", userId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tela_despensa, container, false)
    }

    override fun onResume() {
        super.onResume()
        carregarItensApi()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(recarregarReceiver, IntentFilter("ATUALIZAR_LISTA_ANONA"))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(recarregarReceiver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Barra de filtro "Filtrar:"
        val cardFiltro = view.findViewById<View>(R.id.card_filtro_status)
        val txtStatus = view.findViewById<TextView>(R.id.txt_status_selecionado)
        txtStatus.text = filtroAtual

        cardFiltro.setOnClickListener {
            val popup = PopupMenu(requireContext(), cardFiltro)
            popup.menu.add("Todos os Itens")
            popup.menu.add("Consumindo")
            popup.menu.add("Por consumir")
            popup.menu.add("Vencido")
            popup.menu.add("Crítico")
            popup.menu.add("Atenção")
            popup.menu.add("Seguro")

            popup.setOnMenuItemClickListener { item ->
                txtStatus.text = item.title
                filtroAtual = item.title.toString()
                atualizarListaNaTela()
                true
            }
            popup.show()
        }

        // Botão de sino no topo e Banner de Alerta para abrir o resumo de alertas de validade
        view.findViewById<View>(R.id.btn_alerta_topo)?.setOnClickListener {
            mostrarDialogoAlertaItens(txtStatus)
        }
        view.findViewById<View>(R.id.card_alerta_despensa)?.setOnClickListener {
            mostrarDialogoAlertaItens(txtStatus)
        }

        // Configurar a lista de produtos (RecyclerView)
        val rv = view.findViewById<RecyclerView>(R.id.rvItems)
        rv.layoutManager = LinearLayoutManager(requireContext())

        val adapter = AdaptadorItem(
            onClick = { item ->
                abrirTelaEdicao(item)
            },
            onOptionsMenuClick = { anchorView, item ->
                mostrarMenuOpcoes(anchorView, item)
            },
            onToggleConsumoClick = { item ->
                // RF13: Alternar "Consumindo" / "Por consumir" ao tocar no selo do card
                alternarConsumoItem(item)
            }
        )

        rv.adapter = adapter
        adapterGlobal = adapter

        // Botões coloridos do topo (contadores do semáforo)
        view.findViewById<View>(R.id.card_vencido).setOnClickListener {
            filtroAtual = "Vencido"
            txtStatus.text = "Vencido"
            atualizarListaNaTela()
        }
        view.findViewById<View>(R.id.card_critico).setOnClickListener {
            filtroAtual = "Crítico"
            txtStatus.text = "Crítico"
            atualizarListaNaTela()
        }
        view.findViewById<View>(R.id.card_atencao).setOnClickListener {
            filtroAtual = "Atenção"
            txtStatus.text = "Atenção"
            atualizarListaNaTela()
        }
        view.findViewById<View>(R.id.card_seguro).setOnClickListener {
            filtroAtual = "Seguro"
            txtStatus.text = "Seguro"
            atualizarListaNaTela()
        }

        // Botão flutuante de adicionar (+)
        val btnAdd = view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_add_item)
        btnAdd.setOnClickListener {
            (activity as? MainActivity)?.abrirAdicionarItem()
        }
    }

    private fun obterUserIdSegura(): Int? {
        val argId = arguments?.getInt("USER_ID", -1) ?: -1
        if (argId > 0) return argId
        return SecaoUsuario(requireContext()).obterUserId()
    }

    // Busca a despensa do usuário na API, ordena por prioridade e atualiza contadores/notificações
    private fun carregarItensApi() {
        val ctx = context ?: return
        val userId = obterUserIdSegura()
        if (userId == null) {
            val intent = Intent(ctx, TelaLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
            return
        }

        val api = ApiClient.getService(ctx)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                try {
                    val info = api.buscarInfoGeral()
                    atualizarListasDeApoio(info)
                } catch (_: Exception) {
                }

                val resp = api.listarItens(userId)
                val itensMapeados = resp.listaItens.onEach { it.mapearCampos(requireContext()) }

                // RF12 / RN10 / B15: Ordenar por prioridade (1=Vencido, 2=Crítico, 3=Atenção, 4=Seguro) e menor prazo primeiro
                listaCompleta = itensMapeados.sortedWith(
                    compareBy<Item> { it.flag }
                        .thenBy { it.diasRestantes ?: Int.MAX_VALUE }
                        .thenBy { it.nome.lowercase() }
                ).toMutableList()

                viewModel.atualizarItensDespensa(listaCompleta.toList())

                val idsForaLocal = SecaoUsuario(requireContext()).obterIdsForaDoacao()
                viewModel.atualizarItensParaDoacao(
                    listaCompleta.filter {
                        (it.flag == 2 || it.flag == 3) &&
                            it.consumindo == 0 &&
                            it.foraDoacao == 0 &&
                            !idsForaLocal.contains(it.id)
                    }
                )

                atualizarListaNaTela()
                view?.let {
                    atualizarContadores(it)
                    atualizarBannerEAlertas(it)
                }

                // RF16 / RN15: Dispara alerta de notificação caso existam itens críticos/vencidos/em atenção
                NotificacaoValidadeHelper.verificarENotificar(requireContext(), listaCompleta)

            } catch (e: Exception) {
                Log.e("TelaDespensa", "Falha ao carregar itens da despensa", e)
                if (isAdded) {
                    Toast.makeText(
                        requireContext(),
                        "Não foi possível carregar a despensa. Verifique a conexão com o servidor.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // Atualiza o banner de alerta na tela e exibe o popup de alerta de validade ao abrir a despensa
    private fun atualizarBannerEAlertas(rootView: View) {
        val ctx = context ?: return
        val session = SecaoUsuario(ctx)
        val cardAlerta = rootView.findViewById<View>(R.id.card_alerta_despensa)
        val txtResumo = rootView.findViewById<TextView>(R.id.txt_resumo_alerta)
        val txtStatus = rootView.findViewById<TextView>(R.id.txt_status_selecionado)

        val vencidos = listaCompleta.count { it.flag == 1 }
        val criticos = listaCompleta.count { it.flag == 2 }
        val atencao = listaCompleta.count { it.flag == 3 }

        if (!session.notificacoesAtivas() || (vencidos == 0 && criticos == 0 && atencao == 0)) {
            cardAlerta?.visibility = View.GONE
            return
        }

        val partes = mutableListOf<String>()
        if (criticos > 0) partes.add("$criticos crítico(s)")
        if (atencao > 0) partes.add("$atencao em atenção")
        if (vencidos > 0) partes.add("$vencidos vencido(s)")

        txtResumo?.text = partes.joinToString("  •  ")
        cardAlerta?.visibility = View.VISIBLE

        if (!NotificacaoValidadeHelper.alertaDialogExibidoNaSessao && (criticos > 0 || vencidos > 0)) {
            NotificacaoValidadeHelper.alertaDialogExibidoNaSessao = true
            mostrarDialogoAlertaItens(txtStatus)
        }
    }

    private fun formatarPrazoElegante(item: Item): String {
        val sdf = SimpleDateFormat("dd/MM", Locale("pt", "BR"))
        val dataCurta = item.dataValidade?.let { sdf.format(it) } ?: ""
        val dias = item.diasRestantes
        val prazo = when {
            item.flag == 1 -> "Vencido"
            dias == 0 -> "Vence hoje"
            dias == 1 -> "Falta 1 dia"
            dias != null && dias > 1 -> "Faltam $dias dias"
            else -> item.prazo?.lowercase()?.replaceFirstChar { it.uppercase() }.orEmpty()
        }
        return if (dataCurta.isNotEmpty() && prazo.isNotEmpty()) "$prazo ($dataCurta)" else prazo.ifEmpty { dataCurta }
    }

    private fun adicionarLinhaItemDialog(
        container: LinearLayout,
        item: Item,
        corTextoPrazo: Int
    ) {
        val ctx = container.context
        val linha = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(4, 8, 4, 8)
        }

        val txtNome = TextView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            text = "•  ${item.nome}"
            textSize = 13f
            setTextColor(android.graphics.Color.parseColor("#4A3B31"))
            typeface = androidx.core.content.res.ResourcesCompat.getFont(ctx, R.font.century_gothic)
        }

        val txtPrazo = TextView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            text = formatarPrazoElegante(item)
            textSize = 11.5f
            setTextColor(corTextoPrazo)
            setTypeface(
                androidx.core.content.res.ResourcesCompat.getFont(ctx, R.font.century_gothic),
                android.graphics.Typeface.BOLD
            )
        }

        linha.addView(txtNome)
        linha.addView(txtPrazo)
        container.addView(linha)
    }

    private fun mostrarDialogoAlertaItens(txtStatus: TextView?) {
        val ctx = context ?: return
        val criticos = listaCompleta.filter { it.flag == 2 }
        val atencao = listaCompleta.filter { it.flag == 3 }
        val vencidos = listaCompleta.filter { it.flag == 1 }

        val dialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_alerta_validade, null)
        val dialog = AlertDialog.Builder(ctx)
            .setView(dialogView)
            .create()

        val secaoCritico = dialogView.findViewById<View>(R.id.secao_alerta_critico)
        val badgeCritico = dialogView.findViewById<TextView>(R.id.badge_qtd_critico)
        val listaCritico = dialogView.findViewById<LinearLayout>(R.id.lista_itens_critico)

        val secaoAtencao = dialogView.findViewById<View>(R.id.secao_alerta_atencao)
        val badgeAtencao = dialogView.findViewById<TextView>(R.id.badge_qtd_atencao)
        val listaAtencao = dialogView.findViewById<LinearLayout>(R.id.lista_itens_atencao)

        val secaoVencido = dialogView.findViewById<View>(R.id.secao_alerta_vencido)
        val badgeVencido = dialogView.findViewById<TextView>(R.id.badge_qtd_vencido)
        val listaVencido = dialogView.findViewById<LinearLayout>(R.id.lista_itens_vencido)

        val txtSubtitulo = dialogView.findViewById<TextView>(R.id.txt_subtitulo_dialog_alerta)
        val txtDica = dialogView.findViewById<TextView>(R.id.txt_dica_dialog_alerta)
        val btnFechar = dialogView.findViewById<View>(R.id.btn_fechar_dialog_alerta)
        val btnDoacao = dialogView.findViewById<View>(R.id.btn_dialog_ir_doacao)
        val btnVerCriticos = dialogView.findViewById<View>(R.id.btn_dialog_ver_criticos)

        if (criticos.isEmpty() && atencao.isEmpty() && vencidos.isEmpty()) {
            txtSubtitulo.text = "Sua despensa está em dia!"
            txtDica.text = "Todos os itens estão dentro do prazo seguro de validade."
            btnDoacao.visibility = View.GONE
        } else {
            if (criticos.isNotEmpty()) {
                secaoCritico.visibility = View.VISIBLE
                badgeCritico.text = "Crítico • ${criticos.size}"
                val corCritico = android.graphics.Color.parseColor("#C94A4A")
                criticos.forEach { adicionarLinhaItemDialog(listaCritico, it, corCritico) }
            }
            if (atencao.isNotEmpty()) {
                secaoAtencao.visibility = View.VISIBLE
                badgeAtencao.text = "Atenção • ${atencao.size}"
                val corAtencao = android.graphics.Color.parseColor("#B07D1A")
                atencao.forEach { adicionarLinhaItemDialog(listaAtencao, it, corAtencao) }
            }
            if (vencidos.isNotEmpty()) {
                secaoVencido.visibility = View.VISIBLE
                badgeVencido.text = "Vencido • ${vencidos.size}"
                val corVencido = android.graphics.Color.parseColor("#7D6856")
                vencidos.forEach { adicionarLinhaItemDialog(listaVencido, it, corVencido) }
            }
        }

        btnFechar.setOnClickListener { dialog.dismiss() }

        btnDoacao.setOnClickListener {
            (activity as? MainActivity)?.irParaDoacao()
            dialog.dismiss()
        }

        btnVerCriticos.setOnClickListener {
            if (criticos.isNotEmpty()) {
                filtroAtual = "Crítico"
                txtStatus?.text = "Crítico"
                atualizarListaNaTela()
            } else if (atencao.isNotEmpty()) {
                filtroAtual = "Atenção"
                txtStatus?.text = "Atenção"
                atualizarListaNaTela()
            }
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun atualizarListaNaTela() {
        val listaFiltrada = when (filtroAtual) {
            "Consumindo" -> listaCompleta.filter { it.consumindo == 1 }
            "Por consumir" -> listaCompleta.filter { it.consumindo == 0 }
            "Vencido" -> listaCompleta.filter { it.flag == 1 }
            "Crítico" -> listaCompleta.filter { it.flag == 2 }
            "Atenção" -> listaCompleta.filter { it.flag == 3 }
            "Seguro" -> listaCompleta.filter { it.flag == 4 }
            else -> listaCompleta
        }
        adapterGlobal?.submitList(listaFiltrada.toList())
    }

    private fun abrirTelaEdicao(item: Item) {
        val intent = Intent(requireContext(), AdicionarItem::class.java)
        intent.putExtra("ITEM_ID", item.id)
        intent.putExtra("NOME", item.nome)
        intent.putExtra("QUANTIDADE", item.quantidade)
        intent.putExtra("PRECO", item.preco)
        intent.putExtra("MEDIDA_ID", item.idMedida)
        intent.putExtra("CATEGORIA_ID", item.idCategoria)
        intent.putExtra("LOCAL_ID", item.idLocal)
        intent.putExtra("LOCAL", item.localNome)
        intent.putExtra("CONSUMINDO", item.consumindo == 1)
        intent.putExtra("IMAGEM", item.imagem ?: "item_padrao.png")

        if (item.possuiDataValidade == 1 && item.dataValidade != null) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            intent.putExtra("DATA_VALIDADE", sdf.format(item.dataValidade))
        }

        startActivity(intent)
    }

    private fun mostrarMenuOpcoes(anchorView: View, item: Item) {
        val popup = PopupMenu(requireContext(), anchorView)
        popup.menu.add("Editar item")

        val textoConsumo = if (item.consumindo == 1) "Por consumir" else "Consumir item"
        popup.menu.add(textoConsumo)
        popup.menu.add("Excluir item")

        popup.setOnMenuItemClickListener { menuIt ->
            when (menuIt.title) {
                "Editar item" -> abrirTelaEdicao(item)
                "Consumir item", "Por consumir" -> alternarConsumoItem(item)
                "Excluir item" -> confirmarExclusao(item)
            }
            true
        }
        popup.show()
    }

    // RF13 / B12: Alterna entre "Por consumir" (0) e "Consumindo" (1) salvando na API
    private fun alternarConsumoItem(item: Item) {
        val userId = obterUserIdSegura() ?: return
        val novoConsumoBool = item.consumindo == 0
        val api = ApiClient.getService(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resp = try {
                    api.alterarConsumoItem(item.id, BodyAlterarConsumo(userId, novoConsumoBool))
                } catch (_: Exception) {
                    null
                }

                val sucesso = if (resp != null && resp.isSuccessful) {
                    true
                } else {
                    val dataStr = if (item.possuiDataValidade == 1 && item.dataValidade != null) {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(item.dataValidade)
                    } else null
                    val bodyCompleto = BodyCriarItem(
                        id_usuario = userId,
                        nome = item.nome,
                        id_categoria = item.idCategoria,
                        imagem = item.imagem ?: "item_padrao.png",
                        consumindo = novoConsumoBool,
                        possui_data_validade = item.possuiDataValidade == 1,
                        data_validade = dataStr,
                        id_local_armazenamento = item.idLocal,
                        quantidade = item.quantidade,
                        id_medida = item.idMedida,
                        preco = item.preco,
                        id_flag = item.flag
                    )
                    val respEdit = api.editarItemUsuario(item.id, bodyCompleto)
                    respEdit.isSuccessful
                }

                if (sucesso) {
                    val msg = if (novoConsumoBool) "Marcado como Consumindo" else "Marcado como Por consumir"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    carregarItensApi()
                } else {
                    Toast.makeText(requireContext(), "Não foi possível alterar o estado do item", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("TelaDespensa", "Erro ao alternar consumo", e)
                Toast.makeText(requireContext(), "Erro ao conectar com o servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // RF07 / RN09 / RF21: Exclusão lógica perguntando o motivo (inclui Item doado para o Histórico)
    private fun confirmarExclusao(item: Item) {
        val motivos = arrayOf("Item consumido", "Item vencido/estragado", "Item doado", "Outro")
        var motivoSelecionado = motivos[0]
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Excluir \"${item.nome}\"")
            .setSingleChoiceItems(motivos, 0) { _, which -> motivoSelecionado = motivos[which] }
            .setPositiveButton("Confirmar Exclusão") { d, _ ->
                deletarItem(item.id.toInt(), motivoSelecionado)
                d.dismiss()
            }
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.vermelho_murango))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
    }

    private fun atualizarContadores(view: View) {
        val v = listaCompleta.count { it.flag == 1 }
        val c = listaCompleta.count { it.flag == 2 }
        val a = listaCompleta.count { it.flag == 3 }
        val s = listaCompleta.count { it.flag == 4 }

        view.findViewById<TextView>(R.id.txt_vencido_contador).text = "Vencido $v"
        view.findViewById<TextView>(R.id.txt_critico_contador).text = "Crítico $c"
        view.findViewById<TextView>(R.id.txt_atencao_contador).text = "Atenção $a"
        view.findViewById<TextView>(R.id.txt_seguro_contador).text = "Seguro $s"
    }

    private fun deletarItem(idItem: Int, razaoDelecao: String) {
        val userId = obterUserIdSegura()
        val bodyDelecao = BodyDeletarItem(razaoDelecao, userId)
        val api = ApiClient.getService(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resp = api.deletarItemUsuario(idItem, bodyDelecao)
                if (resp.isSuccessful) {
                    LocalBroadcastManager.getInstance(requireContext())
                        .sendBroadcast(Intent("ATUALIZAR_LISTA_ANONA"))
                    Toast.makeText(requireContext(), "Item excluído", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Erro ao deletar item", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("TelaDespensa", "Erro ao deletar item", e)
                Toast.makeText(requireContext(), "Erro ao conectar com o servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val recarregarReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            carregarItensApi()
        }
    }
}
