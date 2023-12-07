package com.varitas.gokulpos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterInvoiceBinding
import com.varitas.gokulpos.response.Invoice

class InvoiceAdapter(private var onClickListener : (Invoice, Int, Boolean) -> Unit) : RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder>() {
    var list = ArrayList<Invoice>()
    var filteredList = ArrayList<Invoice>()

    fun setList(title : List<Invoice>) {
        list.clear()
        list.addAll(title)
        filteredList.clear()
        filteredList.addAll(title)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : InvoiceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterInvoiceBinding.inflate(inflater, parent, false)
        return InvoiceViewHolder(binding)
    }

    override fun getItemCount() : Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder : InvoiceViewHolder, position : Int) {
        holder.bind(filteredList[position], position)
    }

    //region To get filtered data
    fun getFilter() : Filter {
        return object : Filter() {
            override fun performFiltering(charSequence : CharSequence) : FilterResults {
                val searchString = charSequence.toString().lowercase()
                filteredList = if(searchString.isEmpty()) {
                    list
                } else {
                    val tempFilteredList = ArrayList<Invoice>()
                    for(product in list) { // search for user title
                        if(product.invoiceNo!!.lowercase().contains(searchString) || product.sVendor!!.lowercase().contains(searchString)) tempFilteredList.add(product)
                    }
                    tempFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged") @Suppress("UNCHECKED_CAST") override fun publishResults(charSequence : CharSequence?, filterResults : FilterResults) {
                filteredList = filterResults.values as ArrayList<Invoice>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class InvoiceViewHolder(private val binding : AdapterInvoiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : Invoice, position : Int) {
            binding.apply {
                invoice = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if(position % 2 == 0) R.color.white else R.color.lighterGrey)
                linearLayoutDetails.setOnClickListener {
                    onClickListener(data, position, false)
                }
                linearLayoutDetails.setOnLongClickListener {
                    onClickListener(data, position, true)
                    return@setOnLongClickListener true
                }
            }
        }
    }
}