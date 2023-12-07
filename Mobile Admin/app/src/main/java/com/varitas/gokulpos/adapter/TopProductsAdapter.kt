package com.varitas.gokulpos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.databinding.AdapterTopProductsBinding
import com.varitas.gokulpos.response.TopSellingItems

class TopProductsAdapter : RecyclerView.Adapter<TopProductViewHolder>() {

    var list = ArrayList<TopSellingItems>()

    fun setList(product: List<TopSellingItems>) {
        list.clear()
        list.addAll(product)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterTopProductsBinding.inflate(inflater, parent, false)
        return TopProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopProductViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class TopProductViewHolder(private val binding: AdapterTopProductsBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(product: TopSellingItems) {
        binding.product = product
    }
}