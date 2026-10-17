package com.example.annona

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

// Tela Receitas: Sugere receitas pela API com base nos itens que o usuário tem na despensa (RF17, RF19, RF22, RN14, B11)
class ReceitasFragment : Fragment() {

    private val viewModel: ItensCompartilhadosViewModel by activityViewModels()
    private var todasAsReceitas: List<Receita> = ReceitasData.receitas
    private var receitasSugeridasPelaDespensa: List<Receita> = emptyList()
    private var filtroAtual: String = "Pela minha despensa"
    private var recyclerViewRef: RecyclerView? = null
    private var txtStatusRef: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_receitas, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_receitas)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewRef = recyclerView

        val cardFiltro = view.findViewById<View>(R.id.card_filtro_receitas)
        val txtStatus = view.findViewById<TextView>(R.id.txt_filtro_selecionado)
        txtStatusRef = txtStatus
        txtStatus.text = filtroAtual

        // Observa mudanças na despensa do usuário para atualizar as sugestões automaticamente
        viewModel.itensDespensa.observe(viewLifecycleOwner) { itens ->
            recalcularSugestoesLocais(itens)
            aplicarFiltroNaTela()
        }

        cardFiltro.setOnClickListener {
            val popup = PopupMenu(requireContext(), cardFiltro)
            popup.menu.add("Pela minha despensa")
            popup.menu.add("Todos")
            popup.menu.add("Prato: Principal")
            popup.menu.add("Acompanhamento")
            popup.menu.add("Entrada")
            popup.menu.add("Sobremesa")

            popup.setOnMenuItemClickListener { item ->
                filtroAtual = item.title.toString()
                txtStatus.text = filtroAtual
                aplicarFiltroNaTela()
                true
            }

            popup.show()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        carregarReceitasDaApi()
    }

    // RF17 / RF22 / B11: Busca receitas pela API e cruza com os itens da despensa do usuário
    private fun carregarReceitasDaApi() {
        val ctx = context ?: return
        val userId = SecaoUsuario(ctx).obterUserId() ?: return
        val api = ApiClient.getService(ctx)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Tenta buscar diretamente da rota de sugestões por usuário (/receitas/sugeridas/:id)
                val respSug = try {
                    api.listarReceitasSugeridas(userId)
                } catch (_: Exception) {
                    null
                }

                if (respSug != null && respSug.todas_receitas.isNotEmpty()) {
                    val todasConvertidas = respSug.todas_receitas.map { ReceitasData.converterDaApi(requireContext(), it) }
                    todasAsReceitas = mesclarComCatalogoCompleto(todasConvertidas)
                    ReceitasData.receitasAtuais = todasAsReceitas

                    val sugeridasApi = respSug.receitas.map { ReceitasData.converterDaApi(requireContext(), it) }
                    receitasSugeridasPelaDespensa = if (sugeridasApi.isNotEmpty()) {
                        sugeridasApi
                    } else {
                        ReceitasData.filtrarPorDespensa(todasAsReceitas, viewModel.itensDespensa.value ?: emptyList())
                    }
                } else {
                    // Fallback para GET /receitas caso o servidor ainda não tenha /receitas/sugeridas
                    val respTodas = api.listarReceitas()
                    if (respTodas.receitas.isNotEmpty()) {
                        val convertidas = respTodas.receitas.map { ReceitasData.converterDaApi(requireContext(), it) }
                        todasAsReceitas = mesclarComCatalogoCompleto(convertidas)
                        ReceitasData.receitasAtuais = todasAsReceitas
                    }
                    recalcularSugestoesLocais(viewModel.itensDespensa.value ?: emptyList())
                }

                aplicarFiltroNaTela()
            } catch (_: Exception) {
                // Se o servidor estiver offline, usa a lista local cruzada com a despensa atual
                recalcularSugestoesLocais(viewModel.itensDespensa.value ?: emptyList())
                aplicarFiltroNaTela()
            }
        }
    }

    // Garante que as 9 receitas estejam disponíveis mesmo se o banco antigo tiver apenas 4 linhas
    private fun mesclarComCatalogoCompleto(vindasDoBanco: List<Receita>): List<Receita> {
        val nomesBanco = vindasDoBanco.map { ReceitasData.normalizar(it.nome) }.toSet()
        val faltantes = ReceitasData.receitas.filter { ReceitasData.normalizar(it.nome) !in nomesBanco }
        return vindasDoBanco + faltantes
    }

    private fun recalcularSugestoesLocais(itensUsuario: List<Item>) {
        val nomesDespensa = itensUsuario.map { it.nome }
        todasAsReceitas = todasAsReceitas.map { rec ->
            rec.copy(ingredientesDisponiveis = ReceitasData.encontrarIngredientesEmComum(rec, nomesDespensa))
        }
        ReceitasData.receitasAtuais = todasAsReceitas
        receitasSugeridasPelaDespensa = todasAsReceitas.filter { it.ingredientesDisponiveis.isNotEmpty() }
    }

    private fun aplicarFiltroNaTela() {
        val rv = recyclerViewRef ?: return

        val listaExibida = when (filtroAtual) {
            "Pela minha despensa" -> receitasSugeridasPelaDespensa
            "Todos" -> todasAsReceitas
            "Prato: Principal" -> todasAsReceitas.filter { it.tag == "PRINCIPAL" }
            "Acompanhamento" -> todasAsReceitas.filter { it.tag == "ACOMPANHAMENTO" }
            "Entrada" -> todasAsReceitas.filter { it.tag == "ENTRADA" }
            "Sobremesa" -> todasAsReceitas.filter { it.tag == "SOBREMESA" }
            else -> receitasSugeridasPelaDespensa
        }

        rv.adapter = ReceitasAdapter(listaExibida) { receita ->
            val intent = Intent(requireContext(), DetalheReceitaActivity::class.java)
            intent.putExtra("NOME", receita.nome)
            startActivity(intent)
        }
    }
}
