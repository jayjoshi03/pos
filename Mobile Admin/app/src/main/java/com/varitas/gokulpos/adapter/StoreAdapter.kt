package com.varitas.gokulpos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterStoreBinding
import com.varitas.gokulpos.response.Store

class StoreAdapter(private var onClickListener : (Store, Int) -> Unit) : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {
    var list = ArrayList<Store>()
    var filteredList = ArrayList<Store>()

    fun setList(title : List<Store>) {
        list.clear()
        list.addAll(title)
        filteredList.clear()
        filteredList.addAll(title)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : StoreViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterStoreBinding.inflate(inflater, parent, false)
        return StoreViewHolder(binding)
    }

    override fun getItemCount() : Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder : StoreViewHolder, position : Int) {
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
                    val tempFilteredList = ArrayList<Store>()
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
                filteredList = filterResults.values as ArrayList<Store>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class StoreViewHolder(private val binding : AdapterStoreBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : Store, position : Int) {
            binding.apply {
                store = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if(position % 2 == 0) R.color.white else R.color.lighterGrey)
                imageButtonSave.setOnClickListener {
                    onClickListener(data, position)
                }
            }
        }
    }
}