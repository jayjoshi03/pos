package com.varitas.gokulpos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterAddinvoiceitemBinding
import com.varitas.gokulpos.response.DetailsInvoice

class AddInvoiceItemAdapter(private var onClickListener : (Int) -> Unit) : RecyclerView.Adapter<AddInvoiceItemAdapter.AddInvoiceViewHolder>() {
    var list = ArrayList<DetailsInvoice>()

    fun setList(title : List<DetailsInvoice>) {
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : AddInvoiceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterAddinvoiceitemBinding.inflate(inflater, parent, false)
        return AddInvoiceViewHolder(binding)
    }

    override fun getItemCount() : Int {
        return list.size
    }

    override fun onBindViewHolder(holder : AddInvoiceViewHolder, position : Int) {
        holder.bind(list[position], position)
    }

    inner class AddInvoiceViewHolder(private val binding : AdapterAddinvoiceitemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : DetailsInvoice, position : Int) {
            binding.apply {
                item = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if(position % 2 == 0) R.color.white else R.color.lighterGrey)
                imageButtonDelete.visibility = if(data.quantity!! > 0) View.GONE else View.VISIBLE
                imageButtonDelete.setOnClickListener {
                    onClickListener(position)
                }
            }
        }
    }
}