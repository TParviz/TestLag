package com.example.testlag.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.testlag.databinding.ItemTablePointBinding
import com.example.testlag.domain.model.Points

class PointTableAdapter : ListAdapter<Points, PointTableViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PointTableViewHolder {
        return PointTableViewHolder(
            binding = ItemTablePointBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PointTableViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Points>() {
            override fun areItemsTheSame(
                oldItem: Points,
                newItem: Points
            ): Boolean =
                oldItem.hashCode() == newItem.hashCode()

            override fun areContentsTheSame(
                oldItem: Points,
                newItem: Points
            ): Boolean =
                oldItem == newItem
        }
    }
}
