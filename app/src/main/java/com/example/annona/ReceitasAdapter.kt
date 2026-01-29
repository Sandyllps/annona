package com.example.annona

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Classe que representa o adapter para a lista de receitas
// Define como cada receita será exibida na tela
class ReceitasAdapter(
    private val listaReceitas: List<Receita>,   // Lista de receitas a serem exibidas
    private val onClick: (Receita) -> Unit      // Função de clique para cada receita
) : RecyclerView.Adapter<ReceitasAdapter.ReceitaViewHolder>() {

    // Classe que representa o ViewHolder para cada receita
    // Guarda a referência para os elementos da view que serão utilizados
    class ReceitaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFoto: ImageView = itemView.findViewById(R.id.img_foto_receita)
        val txtNome: TextView = itemView.findViewById(R.id.txt_nome_receita)
        val txtTempo: TextView = itemView.findViewById(R.id.txt_tempo_receita)
        val txtTag: TextView = itemView.findViewById(R.id.txt_tag_receita)
        val container: View = itemView
    }

    // Método chamado pelo RecyclerView para criar um novo ViewHolder
    // o layout é transformado em uma view real
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceitaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itens_receita, parent, false)
        return ReceitaViewHolder(view)
    }

    // Método chamado pelo RecyclerView para exibir os dados de uma receita na tela
    // Recebe a posição da receita na lista e o ViewHolder correspondente
    override fun onBindViewHolder(holder: ReceitaViewHolder, position: Int) {
        val receita = listaReceitas[position]

        holder.imgFoto.setImageResource(receita.imagem)
        holder.txtNome.text = receita.nome
        holder.txtTempo.text = "Tempo: ${receita.tempoPreparo}"
        holder.txtTag.text = receita.tag

        // Define a ação de clique no item da lista
        holder.itemView.setOnClickListener {
            onClick(receita)
        }
    }

    // Retorna o número de itens na lista
    override fun getItemCount(): Int = listaReceitas.size
}
