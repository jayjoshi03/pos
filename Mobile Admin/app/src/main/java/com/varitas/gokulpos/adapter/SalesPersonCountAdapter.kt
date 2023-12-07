package com.varitas.gokulpos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterSalespersoncountBinding
import com.varitas.gokulpos.response.SalesPerson

class SalesPersonCountAdapter : RecyclerView.Adapter<SalesPersonCountAdapter.SalesPersonViewHolder>() {
    var list = ArrayList<SalesPerson>()

    fun setList(title : List<SalesPerson>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : SalesPersonViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterSalespersoncountBinding.inflate(inflater, parent, false)
        return SalesPersonViewHolder(binding)
    }

    override fun getItemCount() : Int {
        return list.size
    }

    override fun onBindViewHolder(holder : SalesPersonViewHolder, position : Int) {
        holder.bind(list[position], position)
    }

    inner class SalesPersonViewHolder(private val binding : AdapterSalespersoncountBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : SalesPerson, position : Int) {
            binding.apply {
                salesPerson = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if(position % 2 == 0) R.color.white else R.color.lighterGrey)
            }
        }
    }
}