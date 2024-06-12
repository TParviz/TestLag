package com.example.testlag.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.testlag.databinding.ItemTablePointBinding
import com.example.testlag.domain.model.Points

class PointTableViewHolder(
    private val binding: ItemTablePointBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Points) = with(binding) {
        tvValueX.text = "X = " + "${item.x}"
        tvValueY.text = "Y = " + "${item.y}"
    }
}