package com.varitas.gokulpos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterVendorBinding
import com.varitas.gokulpos.response.Vendor

class VendorAdapter(private var onClickListener: (Vendor, Int, Boolean, Boolean) -> Unit) : RecyclerView.Adapter<VendorAdapter.ViewHolder>() {
    var list = ArrayList<Vendor>()
    var filteredList = ArrayList<Vendor>()

    fun setList(title: List<Vendor>) {
        list.clear()
        list.addAll(title)
        filteredList.clear()
        filteredList.addAll(title)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterVendorBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position], position)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    //region To get filtered data
    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val searchString = charSequence.toString().lowercase()
                filteredList = if (searchString.isEmpty()) {
                    list
                } else {
                    val tempFilteredList = ArrayList<Vendor>()
                    for (product in list) { // search for user title
                        if (product.name!!.lowercase().contains(searchString)) tempFilteredList.add(product)
                    }
                    tempFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged") @Suppress("UNCHECKED_CAST") override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                filteredList = filterResults.values as ArrayList<Vendor>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class ViewHolder(private val binding: AdapterVendorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Vendor, position: Int) {
            binding.apply {
                vendor = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if (position % 2 == 0) R.color.white else R.color.lighterGrey)
                textViewSalesPerson.setOnClickListener {
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