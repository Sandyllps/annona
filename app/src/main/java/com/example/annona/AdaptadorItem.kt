package com.example.annona

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

// O Adaptador pega os dados (itens) e os desenha na tela (RecyclerView)
class AdaptadorItem(
    private val onClick: (Item) -> Unit = {},
    private val onOptionsMenuClick: (View, Item) -> Unit = { _, _ -> },
    private val onToggleConsumoClick: ((Item) -> Unit)? = null
) : ListAdapter<Item, AdaptadorItem.VH>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alimento, parent, false)
        return VH(v, onClick, onOptionsMenuClick, onToggleConsumoClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        itemView: View,
        private val onClick: (Item) -> Unit,
        private val onOptionsMenuClick: (View, Item) -> Unit,
        private val onToggleConsumoClick: ((Item) -> Unit)?
    ) : RecyclerView.ViewHolder(itemView) {
        private val imagem: ImageView = itemView.findViewById(R.id.imagem)
        private val titulo: TextView = itemView.findViewById(R.id.titulo)
        private val estado: TextView = itemView.findViewById(R.id.estado)
        private val prazo: TextView = itemView.findViewById(R.id.prazo_vencimento)
        private val dica: TextView = itemView.findViewById(R.id.dica)
        private val flag: View = itemView.findViewById(R.id.flag)
        private val btnOpcoes: ImageView = itemView.findViewById(R.id.opcoes)
        private val iconeLocal: ImageView = itemView.findViewById(R.id.icone_local)

        private var currentItem: Item? = null

        init {
            itemView.setOnClickListener {
                currentItem?.let { onClick(it) }
            }

            btnOpcoes.setOnClickListener { anchorView ->
                currentItem?.let { item ->
                    onOptionsMenuClick(anchorView, item)
                }
            }

            // RF13: Alternar estado entre "Por consumir" e "Consumindo" diretamente pelo selo do card
            estado.setOnClickListener {
                currentItem?.let { item ->
                    onToggleConsumoClick?.invoke(item)
                }
            }

            iconeLocal.setOnClickListener { view ->
                currentItem?.let { item ->
                    val popup = PopupMenu(view.context, view)
                    popup.menu.add(item.localNome).isEnabled = false
                    popup.show()
                }
            }
        }

        fun bind(item: Item) {
            currentItem = item
            titulo.text = item.nome
            estado.text = item.estado
            prazo.text = item.prazo
            dica.text = item.dica

            // Visual do selo de consumo
            if (item.consumindo == 1) {
                estado.setBackgroundResource(R.drawable.bg_tag_aberto)
            } else {
                estado.setBackgroundResource(R.drawable.bg_tag_aberto)
            }

            // LÓGICA DO SEMÁFORO (Cores por validade)
            when (item.flag) {
                1 -> flag.setBackgroundColor(ContextCompat.getColor(flag.context, R.color.bege_muitas_pedras))
                2 -> flag.setBackgroundColor(ContextCompat.getColor(flag.context, R.color.vermelho_mais_clarinho))
                3 -> flag.setBackgroundColor(ContextCompat.getColor(flag.context, R.color.amarelo_sol))
                else -> flag.setBackgroundColor(ContextCompat.getColor(flag.context, R.color.verde_alface))
            }

            imagem.setImageResource(item.imageRes ?: R.drawable.item_padrao)
            iconeLocal.setImageResource(item.localIconRes ?: R.drawable.local_icone_armario)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem == newItem
    }
}