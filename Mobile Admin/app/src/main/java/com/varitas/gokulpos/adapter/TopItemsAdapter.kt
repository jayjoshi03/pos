package com.varitas.gokulpos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.databinding.AdapterTopItemsBinding
import com.varitas.gokulpos.response.TopItems
import com.varitas.gokulpos.utilities.Utils

class TopItemsAdapter : RecyclerView.Adapter<TopItemsAdapter.TopItemsViewHolder>() {

    var list = ArrayList<TopItems>()

    fun setList(title: List<TopItems>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopItemsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterTopItemsBinding.inflate(inflater, parent, false)
        return TopItemsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopItemsViewHolder, position: Int) {
        holder.bind(list[position], position + 1)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class TopItemsViewHolder(private val binding: AdapterTopItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TopItems, position: Int) {
            binding.apply {
                item = data
                textViewProductName.isSelected = true
                srNum = if (position.toString().length > 1) position.toString() else "0${position}"
                amount = Utils.setAmountWithCurrency(itemView.context, data.gross!!)
            }
        }
    }
}