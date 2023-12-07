package com.varitas.gokulpos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterOrderBinding
import com.varitas.gokulpos.response.OrderList


class OrdersAdapter(private var onClickListener : (OrderList, Int) -> Unit) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    var list = ArrayList<OrderList>()
    var filteredList = ArrayList<OrderList>()

    fun setList(order : List<OrderList>) {
        list.clear()
        list.addAll(order)
        filteredList.clear()
        filteredList.addAll(order)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : OrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterOrderBinding.inflate(inflater, parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder : OrderViewHolder, position : Int) {
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
                    val tempFilteredList = ArrayList<OrderList>()
                    for(order in list) { // search for user title
                        if(order.orderNo.toString().contains(searchString)) tempFilteredList.add(order)
                    }
                    tempFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged") @Suppress("UNCHECKED_CAST") override fun publishResults(charSequence : CharSequence?, filterResults : FilterResults) {
                filteredList = filterResults.values as ArrayList<OrderList>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class OrderViewHolder(private val binding : AdapterOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : OrderList, position : Int) {
            binding.apply {
                order = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if(position % 2 == 0) R.color.white else R.color.lighterGrey)
                linearLayoutDetails.setOnClickListener {
                    onClickListener(data, position)
                }
            }
        }
    }
}