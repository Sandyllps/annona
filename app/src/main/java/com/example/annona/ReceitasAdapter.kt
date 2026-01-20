package com.example.annona

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReceitasAdapter(
    private val listaReceitas: List<Receita>,
    private val onClick: (Receita) -> Unit
) : RecyclerView.Adapter<ReceitasAdapter.ReceitaViewHolder>() {

    class ReceitaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFoto: ImageView = itemView.findViewById(R.id.img_foto_receita)
        val txtNome: TextView = itemView.findViewById(R.id.txt_nome_receita)
        val txtIngredientes: TextView = itemView.findViewById(R.id.txt_ingredientes_receita)
        val txtTempo: TextView = itemView.findViewById(R.id.txt_tempo_receita)
        val txtTag: TextView = itemView.findViewById(R.id.txt_tag_receita)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceitaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itens_receita, parent, false)
        return ReceitaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceitaViewHolder, position: Int) {
        val receita = listaReceitas[position]

        holder.imgFoto.setImageResource(receita.imagem)
        holder.txtNome.text = receita.nome
        holder.txtIngredientes.text = receita.ingredientesDisponiveis
        holder.txtTempo.text = receita.tempoPreparo
        holder.txtTag.text = receita.tag

        holder.itemView.setOnClickListener {
            onClick(receita)
        }
    }

    override fun getItemCount(): Int = listaReceitas.size
}
