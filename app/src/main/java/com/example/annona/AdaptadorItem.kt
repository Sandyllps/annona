package com.example.annona

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlin.time.Duration.Companion.days

class AdaptadorItem(
    private val onClick: (Item) -> Unit = {}
) : ListAdapter<Item, AdaptadorItem.VH>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alimento, parent, false)
        return VH(v, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(itemView: View, private val onClick: (Item) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val imagem: ImageView = itemView.findViewById(R.id.imagem)
        private val titulo: TextView = itemView.findViewById(R.id.titulo)
        private val estado: TextView = itemView.findViewById(R.id.estado)
        private val prazo: TextView = itemView.findViewById(R.id.prazo_vencimento)
        private val dica : TextView = itemView.findViewById(R.id.dica)

        private var currentItem: Item? = null

        init {
            itemView.setOnClickListener {
                currentItem?.let { onClick(it) }
            }
        }

        fun bind(item: Item) {
            currentItem = item
            titulo.text = item.titulo
            estado.text = item.estado
            prazo.text = item.prazo
            dica.text = item.dica

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