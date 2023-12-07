package com.varitas.gokulpos.tablet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterUomBinding
import com.varitas.gokulpos.tablet.response.UOM

class UOMAdapter(private var onClickListener : (UOM, Int, Boolean) -> Unit) : RecyclerView.Adapter<UOMAdapter.UOMViewHolder>() {
    var list = ArrayList<UOM>()
    var filteredList = ArrayList<UOM>()

    fun setList(title : List<UOM>) {
        list.clear()
        list.addAll(title)
        filteredList.clear()
        filteredList.addAll(title)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : UOMViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterUomBinding.inflate(inflater, parent, false)
        return UOMViewHolder(binding)
    }

    override fun getItemCount() : Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder : UOMViewHolder, position : Int) {
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
                    val tempFilteredList = ArrayList<UOM>()
                    for(product in list) { // search for user title
                        if(product.uom!!.lowercase().contains(searchString)) tempFilteredList.add(product)
                    }
                    tempFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged") @Suppress("UNCHECKED_CAST") override fun publishResults(charSequence : CharSequence?, filterResults : FilterResults) {
                filteredList = filterResults.values as ArrayList<UOM>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class UOMViewHolder(private val binding : AdapterUomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : UOM, position : Int) {
            binding.apply {
                uom = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if(position % 2 == 0) R.color.white else R.color.lighterGrey)
                imageButtonEdit.setOnClickListener {
                    binding.root.post {
                        onClickListener(data, position, false)
                    }
                }
                imageButtonDelete.setOnClickListener {
                    binding.root.post {
                        onClickListener(data, position, true)
                    }
                }
            }
        }
    }
}