package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.databinding.AdapterFavItemsBinding
import com.varitas.gokulpos.tablet.response.PriceList

class ItemSpecificationAdapter(private var onClickListener: (PriceList) -> Unit) : RecyclerView.Adapter<ItemSpecificationAdapter.ViewHolder>() {

    var list = ArrayList<PriceList>()

    fun setList(menu: List<PriceList>) {
        list.clear()
        list.addAll(menu)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterFavItemsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: AdapterFavItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(items: PriceList) {
            binding.apply {
                buttonFavItem.apply {
                    setOnClickListener {
                        onClickListener(items)
                    }
                }
                name = items.specification
            }
        }
    }
}