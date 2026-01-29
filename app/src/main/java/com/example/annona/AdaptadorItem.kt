package com.example.annona

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

// O Adaptador é como um garçom: ele pega os dados (itens) e os serve na tela (RecyclerView)
class AdaptadorItem(
    private val onClick: (Item) -> Unit = {}, // O que acontece se clicar no card
    private val onOptionsMenuClick: (View, Item) -> Unit = { _, _ -> } // Clique nos 3 pontinhos
) : ListAdapter<Item, AdaptadorItem.VH>(DiffCallback()) {

    // Esta parte cria o "corpo" visual de cada item usando o XML desenhado
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alimento, parent, false)
        return VH(v, onClick, onOptionsMenuClick)
    }

    // Liga as informações de um alimento específico à posição dele na lista
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    // O ViewHolder (VH) é quem realmente segura os componentes (textos, imagens) de cada card
    class VH(
        itemView: View,
        private val onClick: (Item) -> Unit,
        private val onOptionsMenuClick: (View, Item) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        // Localizando os elementos do XML dentro do código
        private val imagem: ImageView = itemView.findViewById(R.id.imagem)
        private val titulo: TextView = itemView.findViewById(R.id.titulo)
        private val estado: TextView = itemView.findViewById(R.id.estado)
        private val prazo: TextView = itemView.findViewById(R.id.prazo_vencimento)
        private val dica : TextView = itemView.findViewById(R.id.dica)
        private val flag : View = itemView.findViewById(R.id.flag)
        private val btnOpcoes: ImageView = itemView.findViewById(R.id.opcoes)
        private val iconeLocal: ImageView = itemView.findViewById(R.id.icone_local)

        private var currentItem: Item? = null

        init {
            //TODO: Para atualizações futuras
            // Configura o clique no card inteiro (ainda não acontece nada ao clicar no card inteiro)
            itemView.setOnClickListener {
                currentItem?.let { onClick(it) }
            }

            // Configura o clique no botão de 3 pontinhos (opções)
            btnOpcoes.setOnClickListener { anchorView ->
                currentItem?.let { item ->
                    onOptionsMenuClick(anchorView, item)
                }
            }

            // Lógica para mostrar o nome do local (ex: "Geladeira") ao clicar no ícone pequeno
            iconeLocal.setOnClickListener { view ->
                currentItem?.let { item ->
                    // Cria uma pequena etiqueta que aparece em cima do ícone
                    val popup = PopupMenu(view.context, view)
                    popup.menu.add(item.localNome).isEnabled = false // Só mostra o nome, pro usuário saber que aquele icone de local se refere a onde seu item está guardado.
                    popup.show()
                }
            }
        }

        // Esta função preenche o card com os dados reais de cada alimento
        fun bind(item: Item) {
            currentItem = item
            titulo.text = item.nome
            estado.text = item.estado
            prazo.text = item.prazo
            dica.text = item.dica

            //LÓGICA DO SEMÁFORO (Cores por validade)
            // Aqui o código decide qual cor a barrinha lateral (flag) terá
            if(item.flag == 1){
                // Marrom: Vencido
                flag.setBackgroundColor(ContextCompat.getColor(flag.context,R.color.bege_muitas_pedras))
            }else if(item.flag == 2){
                // Vermelho: Crítico (Urgente)
                flag.setBackgroundColor(ContextCompat.getColor(flag.context,R.color.vermelho_mais_clarinho))
            }else if(item.flag == 3){
                // Amarelo: Atenção (Sugestão para doação)
                flag.setBackgroundColor(ContextCompat.getColor(flag.context,R.color.amarelo_sol))
            }else {
                // Verde: Seguro (Dentro do prazo)
                flag.setBackgroundColor(ContextCompat.getColor(flag.context,R.color.verde_alface))
            }

            // Coloca a foto do produto e o ícone do local de armazenamento
            imagem.setImageResource(item.imageRes ?: R.drawable.item_padrao)
            iconeLocal.setImageResource(item.localIconRes ?: R.drawable.local_icone_armario)
        }
    }

    // Esta parte serve para o app ser rápido: ela só atualiza os itens que realmente mudaram
    class DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem == newItem
    }
}