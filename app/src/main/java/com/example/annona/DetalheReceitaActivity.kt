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

    private lateinit var binding: ActivityDetalheReceitaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetalheReceitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botão voltar
        binding.btnVoltar.setOnClickListener {
            finish()
        }

        // ===== Dados vindos do Fragment =====
        val nome = intent.getStringExtra("NOME") ?: ""


        val receita = ReceitasData.receitas.find {
            it.nome == nome
        }
        if (receita == null) {
            finish()
            return
        }


        // ===== Bindando na tela =====
        binding.txtNomeReceita.text = receita.nome
        binding.txtDescricaoReceita.text = receita.descricao
        binding.txtPorcoesReceita.text = "Porções: " + receita.porcoes
        binding.txtTempoReceita.text = "Tempo: " + receita.tempoPreparo
        binding.txtTagReceita.text = receita.tag

        binding.txtIngredientesReceita.text =
            receita.ingredientes.joinToString("\n")

        binding.imgReceita.setImageResource(receita.imagem)

        binding.rvModoPreparo.layoutManager = LinearLayoutManager(this)
        binding.rvModoPreparo.adapter =
            ModoPreparoAdapter(receita.modoPreparo)
    }
}

private class ModoPreparoAdapter(
    private val etapas: List<EtapaPreparo>
) : RecyclerView.Adapter<ModoPreparoAdapter.EtapaViewHolder>() {

    class EtapaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNumero: TextView = itemView.findViewById(R.id.txtNumeroEtapa)
        val txtDescricao: TextView = itemView.findViewById(R.id.txtDescricaoEtapa)
        val imgEtapa: ImageView = itemView.findViewById(R.id.imgEtapa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EtapaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.modo_preparo, parent, false)
        return EtapaViewHolder(view)
    }

    override fun onBindViewHolder(holder: EtapaViewHolder, position: Int) {
        val etapa = etapas[position]

        holder.txtNumero.text = "${etapa.numero}"
        holder.txtDescricao.text = etapa.descricao
        holder.imgEtapa.setImageResource(etapa.imagem)
    }

    override fun getItemCount(): Int = etapas.size
}