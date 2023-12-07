package com.varitas.gokulpos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterCommonBinding
import com.varitas.gokulpos.response.Department

class DepartmentAdapter(private var onClickListener : (Department, Int, Boolean, Boolean) -> Unit) : RecyclerView.Adapter<DepartmentAdapter.ViewHolder>() {
    var list = ArrayList<Department>()
    var filteredList = ArrayList<Department>()

    fun setList(title : List<Department>) {
        list.clear()
        list.addAll(title)
        filteredList.clear()
        filteredList.addAll(title)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterCommonBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder : ViewHolder, position : Int) {
        holder.bind(filteredList[position], position)
    }

    override fun getItemCount() : Int {
        return filteredList.size
    }

    //region To get filtered data
    fun getFilter() : Filter {
        return object : Filter() {
            override fun performFiltering(charSequence : CharSequence) : FilterResults {
                val searchString = charSequence.toString().lowercase()
                filteredList = if(searchString.isEmpty()) {
                    list
                } else {
                    val tempFilteredList = ArrayList<Department>()
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
                filteredList = filterResults.values as ArrayList<Department>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class ViewHolder(private val binding : AdapterCommonBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : Department, position : Int) {
            binding.data = data
            binding.pos = position + 1
            binding.apply {
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if(position % 2 == 0) R.color.white else R.color.lighterGrey)
                textViewItemCount.setOnClickListener {
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