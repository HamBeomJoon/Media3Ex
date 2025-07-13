package com.example.viewbindingex.card

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.viewbindingex.databinding.ItemCardBinding

data class CardItem(
    val title: String,
    val imageResId: Int, // drawable 리소스 ID
)

class CardAdapter(
    private val items: List<CardItem>,
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    inner class CardViewHolder(
        private val binding: ItemCardBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CardItem) {
            binding.title.text = item.title
            binding.image.setImageResource(item.imageResId)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCardBinding.inflate(inflater, parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CardViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
