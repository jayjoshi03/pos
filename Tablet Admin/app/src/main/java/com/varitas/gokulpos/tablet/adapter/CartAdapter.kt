package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.databinding.AdapterCartBinding
import com.varitas.gokulpos.tablet.response.ItemCartDetail

class CartAdapter(private var onClickListener: (ItemCartDetail) -> Unit) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    var list = ArrayList<ItemCartDetail>()

    fun setList(data: ItemCartDetail) {
        list.add(0, data)
        notifyItemInserted(0)
    }

    fun setResumeList(data: ArrayList<ItemCartDetail>) {
        list.clear()
        list.addAll(data)
        notifyItemRangeChanged(0, list.size)
    }

    fun clearData() {
        list.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterCartBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: AdapterCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cart: ItemCartDetail, position: Int) {
            binding.apply {
                data = cart
                radioButtonIsSelect.isChecked = cart.isSelected
                buttonQuantity.setOnClickListener {
                    binding.root.post {
                        if (cart.quantity > 0) onClickListener(cart)
                    }
                }
                radioButtonIsSelect.setOnClickListener {
                    binding.root.post {
                        if(cart.isSelected){
                            cart.isSelected = false
                            notifyItemChanged(position)
                        } else {
                            for (i in list.indices) list[i].isSelected = (cart.id == list[i].id && cart.specificationId == list[i].specificationId)
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
}