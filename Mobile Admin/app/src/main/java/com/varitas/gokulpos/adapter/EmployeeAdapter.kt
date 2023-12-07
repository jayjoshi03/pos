package com.varitas.gokulpos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterCustomersBinding
import com.varitas.gokulpos.response.Employee

class EmployeeAdapter(private var onClickListener : (Employee, Int, Boolean) -> Unit) : RecyclerView.Adapter<EmployeeAdapter.CustomerViewHolder>() {

    var list = ArrayList<Employee>()
    var filteredList = ArrayList<Employee>()

    fun setList(employee : List<Employee>) {
        list.clear()
        list.addAll(employee)
        filteredList.clear()
        filteredList.addAll(employee)
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
                    val tempFilteredList = ArrayList<Employee>()
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
                filteredList = filterResults.values as ArrayList<Employee>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class CustomerViewHolder(private val binding : AdapterCustomersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : Employee, position : Int) {
            binding.apply {
                employee = data
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