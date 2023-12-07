package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.databinding.AdapterItemsBinding
import com.varitas.gokulpos.tablet.response.OrderItemDetails

class OrderItemAdapter(private var isCompleted: Boolean = false) : RecyclerView.Adapter<OrderItemAdapter.ViewHolder>() {
    var list = mutableListOf<OrderItemDetails>()

    fun setList(data: ArrayList<OrderItemDetails>) {
        list.clear()
        list.addAll(data)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterItemsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: AdapterItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cart: OrderItemDetails, position: Int) {
            binding.apply {
                cart.totalPrice = cart.unitPrice!!.times(cart.quantity!!)
                isComplete = isCompleted
                data = cart
                binding.checkBoxSelector.setOnCheckedChangeListener { _, isChecked ->
                    cart.isSelected = isChecked
                }
            }
        }
    }
}