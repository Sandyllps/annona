package com.example.annona

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.annona.databinding.ActivityHistoricoBinding
import kotlinx.coroutines.launch

// Tela Histórico: Exibe itens consumidos, desperdiçados (vencidos/estragados) e doados (RF20, RF21)
class HistoricoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoricoBinding
    private var listaHistorico: List<Item> = emptyList()
    private var filtroAtual: String = "Todos"
    private lateinit var adapter: AdaptadorItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoricoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarHistorico)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarHistorico.setNavigationOnClickListener { finish() }

        adapter = AdaptadorItem()
        binding.rvHistorico.layoutManager = LinearLayoutManager(this)
        binding.rvHistorico.adapter = adapter

        binding.cardHistTodos.setOnClickListener {
            filtroAtual = "Todos"
            aplicarFiltro()
        }
        binding.cardHistConsumidos.setOnClickListener {
            filtroAtual = "Consumidos"
            aplicarFiltro()
        }
        binding.cardHistVencidos.setOnClickListener {
            filtroAtual = "Desperdício"
            aplicarFiltro()
        }
        binding.cardHistDoados.setOnClickListener {
            filtroAtual = "Doados"
            aplicarFiltro()
        }

        carregarHistorico()
    }

    private fun carregarHistorico() {
        val userId = SecaoUsuario(this).obterUserId() ?: return
        val api = ApiClient.getService(this)

        lifecycleScope.launch {
            try {
                val resp = api.listarHistorico(userId)
                listaHistorico = resp.historico.map { item ->
                    item.mapearCampos(this@HistoricoActivity)
                    val motivo = item.razaoDelecao ?: "Excluído"
                    val flagMotivo = when {
                        motivo.contains("consumido", ignoreCase = true) -> 4 // Verde
                        motivo.contains("doado", ignoreCase = true) || motivo.contains("doação", ignoreCase = true) -> 3 // Amarelo
                        motivo.contains("vencido", ignoreCase = true) || motivo.contains("estragado", ignoreCase = true) -> 2 // Vermelho
                        else -> 1 // Marrom
                    }
                    item.copy(
                        estado = motivo,
                        dica = "Motivo: $motivo",
                        flag = flagMotivo,
                        prazo = ""
                    )
                }

                val consumidos = listaHistorico.count { (it.razaoDelecao ?: "").contains("consumido", ignoreCase = true) }
                val vencidos = listaHistorico.count {
                    val r = it.razaoDelecao ?: ""
                    r.contains("vencido", ignoreCase = true) || r.contains("estragado", ignoreCase = true)
                }
                val doados = listaHistorico.count {
                    val r = it.razaoDelecao ?: ""
                    r.contains("doado", ignoreCase = true) || r.contains("doação", ignoreCase = true)
                }

                binding.txtHistTodos.text = "Todos\n${listaHistorico.size}"
                binding.txtHistConsumidos.text = "Consumidos\n$consumidos"
                binding.txtHistVencidos.text = "Desperdício\n$vencidos"
                binding.txtHistDoados.text = "Doados\n$doados"

                aplicarFiltro()
            } catch (e: Exception) {
                binding.txtVazioHistorico.visibility = View.VISIBLE
                binding.txtVazioHistorico.text = "Não foi possível carregar o histórico do servidor."
                Toast.makeText(this@HistoricoActivity, "Erro ao conectar com o servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun aplicarFiltro() {
        val filtrada = when (filtroAtual) {
            "Consumidos" -> listaHistorico.filter { (it.razaoDelecao ?: "").contains("consumido", ignoreCase = true) }
            "Desperdício" -> listaHistorico.filter {
                val r = it.razaoDelecao ?: ""
                r.contains("vencido", ignoreCase = true) || r.contains("estragado", ignoreCase = true)
            }
            "Doados" -> listaHistorico.filter {
                val r = it.razaoDelecao ?: ""
                r.contains("doado", ignoreCase = true) || r.contains("doação", ignoreCase = true)
            }
            else -> listaHistorico
        }

        binding.txtVazioHistorico.visibility = if (filtrada.isEmpty()) View.VISIBLE else View.GONE
        adapter.submitList(filtrada)
    }
}

