package com.varitas.gokulpos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterSpecificationBinding
import com.varitas.gokulpos.response.Specification

class SpecificationAdapter(private var onClickListener : (Specification, Int, Boolean) -> Unit) : RecyclerView.Adapter<SpecificationAdapter.SpecificationViewHolder>() {
    var list = ArrayList<Specification>()
    var filteredList = ArrayList<Specification>()

    fun setList(title : List<Specification>) {
        list.clear()
        list.addAll(title)
        filteredList.clear()
        filteredList.addAll(title)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : SpecificationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterSpecificationBinding.inflate(inflater, parent, false)
        return SpecificationViewHolder(binding)
    }

    override fun getItemCount() : Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder : SpecificationViewHolder, position : Int) {
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
                    val tempFilteredList = ArrayList<Specification>()
                    for(product in list) { // search for user type
                        if(product.type!!.lowercase().contains(searchString)) tempFilteredList.add(product)
                    }
                    tempFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged") @Suppress("UNCHECKED_CAST") override fun publishResults(charSequence : CharSequence?, filterResults : FilterResults) {
                filteredList = filterResults.values as ArrayList<Specification>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class SpecificationViewHolder(private val binding : AdapterSpecificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : Specification, position : Int) {
            binding.apply {
                specification = data
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