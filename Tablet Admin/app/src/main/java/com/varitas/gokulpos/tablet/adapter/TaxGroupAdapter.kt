package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.databinding.AdapterGroupTaxBinding
import com.varitas.gokulpos.tablet.response.TaxDetails

class TaxGroupAdapter(private var price: Double) : RecyclerView.Adapter<TaxGroupAdapter.ViewHolder>() {
    var list = ArrayList<TaxDetails>()

    fun setCustomList(data : ArrayList<TaxDetails>){
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterGroupTaxBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: AdapterGroupTaxBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cart: TaxDetails) {
            binding.apply {
                cart.taxAmount = price.times(cart.taxRate!!)/100
                data = cart
            }
        }
    }
}