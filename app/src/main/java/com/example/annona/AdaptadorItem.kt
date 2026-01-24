package com.example.annona

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class AdaptadorItem(
    private val onClick: (Item) -> Unit = {},
    // NOVA PONTE: Função que será chamada quando clicar nos três pontinhos
    private val onOptionsMenuClick: (View, Item) -> Unit = { _, _ -> }
) : ListAdapter<Item, AdaptadorItem.VH>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alimento, parent, false)
        return VH(v, onClick, onOptionsMenuClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        itemView: View,
        private val onClick: (Item) -> Unit,
        private val onOptionsMenuClick: (View, Item) -> Unit // Recebendo a nova ponte
    ) : RecyclerView.ViewHolder(itemView) {
        private val imagem: ImageView = itemView.findViewById(R.id.imagem)
        private val titulo: TextView = itemView.findViewById(R.id.titulo)
        private val estado: TextView = itemView.findViewById(R.id.estado)
        private val prazo: TextView = itemView.findViewById(R.id.prazo_vencimento)
        private val dica : TextView = itemView.findViewById(R.id.dica)
        private val flag : View = itemView.findViewById(R.id.flag)
        private val btnOpcoes: ImageView = itemView.findViewById(R.id.opcoes) // Mapeando o botão de opções

        private var currentItem: Item? = null

        init {
            itemView.setOnClickListener {
                currentItem?.let { onClick(it) }
            }

            // CONFIGURAÇÃO DO CLIQUE NOS TRÊS PONTINHOS
            btnOpcoes.setOnClickListener { anchorView ->
                currentItem?.let { item ->
                    onOptionsMenuClick(anchorView, item)
                }
            }
        }

        fun bind(item: Item) {
            currentItem = item
            titulo.text = item.titulo
            estado.text = item.estado
            prazo.text = item.prazo
            dica.text = item.dica

            if(item.flag == 1){
                flag.setBackgroundColor(ContextCompat.getColor(flag.context,R.color.bege_muitas_pedras))
            }else if(item.flag == 2){
                flag.setBackgroundColor(ContextCompat.getColor(flag.context,R.color.vermelho_mais_clarinho))
            }else if(item.flag == 3){
                flag.setBackgroundColor(ContextCompat.getColor(flag.context,R.color.amarelo_sol))
            }else {
                flag.setBackgroundColor(ContextCompat.getColor(flag.context,R.color.verde_alface))
            }

            imagem.setImageResource(item.imageRes)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem == newItem
    }
}