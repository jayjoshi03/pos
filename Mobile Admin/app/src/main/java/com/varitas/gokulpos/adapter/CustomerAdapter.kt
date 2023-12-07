package com.varitas.gokulpos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterCustomersBinding
import com.varitas.gokulpos.response.Customers

class CustomerAdapter(private var onClickListener : (Customers, Int, Boolean) -> Unit) : RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

    var list = ArrayList<Customers>()
    var filteredList = ArrayList<Customers>()

    fun setList(customer : List<Customers>) {
        list.clear()
        list.addAll(customer)
        filteredList.clear()
        filteredList.addAll(customer)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : CustomerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterCustomersBinding.inflate(inflater, parent, false)
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder : CustomerViewHolder, position : Int) {
        holder.bind(filteredList[position], position)
    }

    override fun getItemCount() : Int {
        return filteredList.size
    }

    //region To get filtered data
    fun getFilter() : Filter {
        return object : Filter() {
            override fun performFiltering(charSequence : CharSequence) : FilterResults {
                val searchString = charSequence.toString()
                filteredList = if(searchString.isEmpty()) {
                    list
                } else {
                    val tempFilteredList = ArrayList<Customers>()
                    for(product in list) { // search for user title
                        if(product.name!!.lowercase().contains(searchString)) tempFilteredList.add(product)
                    }
                    tempFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged") @Suppress("UNCHECKED_CAST") override fun publishResults(charSequence : CharSequence?, filterResults : FilterResults) {
                filteredList = filterResults.values as ArrayList<Customers>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class CustomerViewHolder(private val binding : AdapterCustomersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : Customers, position : Int) {
            binding.apply {
                customer = data
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