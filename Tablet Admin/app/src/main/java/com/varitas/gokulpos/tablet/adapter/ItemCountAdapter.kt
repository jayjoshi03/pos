package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterItemcountBinding
import com.varitas.gokulpos.tablet.response.ItemCount

class ItemCountAdapter() : RecyclerView.Adapter<ItemCountAdapter.ItemCountViewHolder>() {
    var list = ArrayList<ItemCount>()

    fun setList(title : List<ItemCount>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ItemCountViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterItemcountBinding.inflate(inflater, parent, false)
        return ItemCountViewHolder(binding)
    }

    override fun getItemCount() : Int {
        return list.size
    }

    override fun onBindViewHolder(holder : ItemCountViewHolder, position : Int) {
        holder.bind(list[position], position)
    }

    inner class ItemCountViewHolder(private val binding : AdapterItemcountBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : ItemCount, position : Int) {
            binding.apply {
                itemCount = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if(position % 2 == 0) R.color.white else R.color.lighterGrey)
            }
        }
    }
}