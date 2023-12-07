package com.varitas.gokulpos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterTaxBinding
import com.varitas.gokulpos.response.Tax

class TaxAdapter(private var onClickListener: (Tax, Int, Boolean) -> Unit) : RecyclerView.Adapter<TaxAdapter.ViewHolder>() {

    var list = ArrayList<Tax>()
    var filteredList = ArrayList<Tax>()

    fun setList(title: List<Tax>) {
        list.clear()
        list.addAll(title)
        filteredList.clear()
        filteredList.addAll(title)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterTaxBinding.inflate(inflater, parent, false)
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
                    val tempFilteredList = ArrayList<Tax>()
                    for (product in list) { // search for user title
                        if (product.className!!.lowercase().contains(searchString)) tempFilteredList.add(product)
                    }
                    tempFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged") @Suppress("UNCHECKED_CAST") override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                filteredList = filterResults.values as ArrayList<Tax>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class ViewHolder(private val binding: AdapterTaxBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Tax, position: Int) {
            binding.apply {
                tax = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if (position % 2 == 0) R.color.white else R.color.lighterGrey)
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