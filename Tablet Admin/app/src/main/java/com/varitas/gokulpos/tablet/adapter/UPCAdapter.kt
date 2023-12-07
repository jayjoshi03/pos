package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterTopProductsBinding
import com.varitas.gokulpos.tablet.request.ItemUPC

class UPCAdapter(private var onClickListener: (Int) -> Unit) : RecyclerView.Adapter<UPCAdapter.UPCViewHolder>() {
    var list = ArrayList<ItemUPC>()

    fun setList(title: List<ItemUPC>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    fun addUPC(title: ItemUPC) {
        list.add(title)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UPCViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterTopProductsBinding.inflate(inflater, parent, false)
        return UPCViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UPCViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class UPCViewHolder(private val binding: AdapterTopProductsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ItemUPC, position: Int) {
            binding.apply {
                upc = data
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if (position % 2 == 0) R.color.white else R.color.lighterGrey)
                imageViewAction.setOnClickListener {
                    binding.root.post {
                        onClickListener(position)
                    }
                }
            }
        }
    }
}