package com.varitas.gokulpos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.databinding.AdapterOrderAnalyticsBinding
import com.varitas.gokulpos.response.Summary


class OrderAnalyticsAdapter : RecyclerView.Adapter<OrderAnalyticsAdapter.OrderAnalyticsViewHolder>() {

    private var list = mutableListOf<Summary>()

    fun setList(product: List<Summary>) {
        list.clear()
        list.addAll(product)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAnalyticsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterOrderAnalyticsBinding.inflate(inflater, parent, false)
        return OrderAnalyticsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderAnalyticsViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class OrderAnalyticsViewHolder(private val binding: AdapterOrderAnalyticsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Summary) {
            binding.order = order
        }
    }
}