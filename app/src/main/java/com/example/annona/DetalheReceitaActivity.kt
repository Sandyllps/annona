package com.example.annona

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.annona.databinding.ActivityDetalheReceitaBinding

class DetalheReceitaActivity : AppCompatActivity() {

    // Variável que armazena o binding do layout
    private lateinit var binding: ActivityDetalheReceitaBinding

    // Chama o método onCreate da Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o ViewBinding, transformando o XML em objetos acessáveis no código
        binding = ActivityDetalheReceitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botão voltar
        binding.btnVoltar.setOnClickListener {
            finish()
        }

        // Dados vindos do Fragment
        val nome = intent.getStringExtra("NOME") ?: ""

        // Procura o nome da receita na lista
        val receita = ReceitasData.receitas.find {
            it.nome == nome
        }
        if (receita == null) { // Caso não encontre, encerra a Activity
            finish()
            return
        }


        // Bindando na tela
        // Exibe os dados correspondentes na tela
        binding.txtNomeReceita.text = receita.nome
        binding.txtDescricaoReceita.text = receita.descricao
        binding.txtPorcoesReceita.text = "Porções: " + receita.porcoes
        binding.txtTempoReceita.text = "Tempo: " + receita.tempoPreparo
        binding.txtTagReceita.text = receita.tag

        // Converte a lista de ingredientes em um único texto, separado por linhas quebradas
        binding.txtIngredientesReceita.text =
            receita.ingredientes.joinToString("\n")

        binding.imgReceita.setImageResource(receita.imagem)

        // Exibe os dados do modo de preparo na tela
        binding.rvModoPreparo.layoutManager = LinearLayoutManager(this)
        binding.rvModoPreparo.adapter = // Passa a lista de etapas de preparo para o adapter
            ModoPreparoAdapter(receita.modoPreparo)
    }
}

// Adapter para exibir a lista de etapas de preparo
private class ModoPreparoAdapter(
    private val etapas: List<EtapaPreparo>
) : RecyclerView.Adapter<ModoPreparoAdapter.EtapaViewHolder>() {

        // ViewHolder para representar cada item da lista de etapas de preparo
    class EtapaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNumero: TextView = itemView.findViewById(R.id.txtNumeroEtapa)
        val txtDescricao: TextView = itemView.findViewById(R.id.txtDescricaoEtapa)
        val imgEtapa: ImageView = itemView.findViewById(R.id.imgEtapa)
    }

    // Método chamado quando o RecyclerView precisa criar um novo item visual
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EtapaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.modo_preparo, parent, false)
        return EtapaViewHolder(view)
    }

    // Método chamado para exibir os dados de um item na tela
    override fun onBindViewHolder(holder: EtapaViewHolder, position: Int) {
        val etapa = etapas[position]

        holder.txtNumero.text = "${etapa.numero}"
        holder.txtDescricao.text = etapa.descricao
        holder.imgEtapa.setImageResource(etapa.imagem)
    }
    // Retorna o número total de itens na lista
    override fun getItemCount(): Int = etapas.size
}