package com.varitas.gokulpos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterPurchaseorderBinding
import com.varitas.gokulpos.response.PurchaseOrder
import com.varitas.gokulpos.utilities.Default

class PurchaseOrderAdapter(private var onClickListener : (PurchaseOrder, Int, Boolean, Boolean) -> Unit) : RecyclerView.Adapter<PurchaseOrderAdapter.PurchaseOrderViewHolder>() {
    var list = ArrayList<PurchaseOrder>()
    var filteredList = ArrayList<PurchaseOrder>()

    fun setList(title : List<PurchaseOrder>) {
        list.clear()
        list.addAll(title)
        filteredList.clear()
        filteredList.addAll(title)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : PurchaseOrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterPurchaseorderBinding.inflate(inflater, parent, false)
        return PurchaseOrderViewHolder(binding)
    }

    override fun getItemCount() : Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder : PurchaseOrderViewHolder, position : Int) {
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
                    val tempFilteredList = ArrayList<PurchaseOrder>()
                    for(product in list) { // search for user title
                        if(product.purchaseOrderNo!!.lowercase().contains(searchString)) tempFilteredList.add(product)
                    }
                    tempFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged") @Suppress("UNCHECKED_CAST") override fun publishResults(charSequence : CharSequence?, filterResults : FilterResults) {
                filteredList = filterResults.values as ArrayList<PurchaseOrder>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class PurchaseOrderViewHolder(private val binding : AdapterPurchaseorderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : PurchaseOrder, position : Int) {
            binding.apply {
                purchase = data
                pos = position + 1
                if(data.isCancel) linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, R.color.lightRed)
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if(data.status == Default.CANCEL) R.color.lightRed else if(data.status == Default.DISPATCH) R.color.lightBlue else if(data.status == Default.PARTIALLY_RECEIVED) R.color.lightYellow else if(data.status == Default.FULLY_RECEIVED) R.color.lightGreen else R.color.lighterGrey)
                imageButtonInvoice.visibility = if(data.status == 24) View.INVISIBLE else View.VISIBLE
                imageButtonInvoice.setImageResource(if(data.isMake) R.drawable.ic_complete_star else R.drawable.ic_empty_star)
                imageButtonInvoice.setOnClickListener {
                    onClickListener(data, position, false, true)
                }
                linearLayoutDetails.setOnClickListener {
                    onClickListener(data, position, false, false)
                }
                linearLayoutDetails.setOnLongClickListener {
                    onClickListener(data, position, true, false)
                    return@setOnLongClickListener true
                }
            }
        }
    }
}