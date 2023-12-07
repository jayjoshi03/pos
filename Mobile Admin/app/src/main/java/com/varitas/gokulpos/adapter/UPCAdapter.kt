package com.varitas.gokulpos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.databinding.AdapterTopProductsBinding
import com.varitas.gokulpos.response.UPC

class UPCAdapter(private var onClickListener: (UPC, Int) -> Unit) : RecyclerView.Adapter<UPCAdapter.UPCViewHolder>() {
    var list = ArrayList<UPC>()

    fun setList(title: List<UPC>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
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
        fun bind(data: UPC, position: Int) {
            binding.apply {
                upc = data
                val params: ViewGroup.LayoutParams = linearLayoutDetails.layoutParams
                params.height = itemView.context.resources.getDimension(com.varitas.gokulpos.R.dimen.spinnerSize).toInt()
                imageViewDeleteUPC.setOnClickListener {
                    onClickListener(data, position)
                }
            }
        }
    }
}