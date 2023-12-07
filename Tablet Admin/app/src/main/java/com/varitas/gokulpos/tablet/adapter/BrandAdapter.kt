package com.varitas.gokulpos.tablet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterBrandBinding
import com.varitas.gokulpos.tablet.model.Brand

class BrandAdapter(private var onClickListener: (Brand, Int, Boolean, Boolean) -> Unit) : RecyclerView.Adapter<BrandAdapter.ViewHolder>() {
    var list = ArrayList<Brand>()
    var filteredList = ArrayList<Brand>()

    fun setList(title: List<Brand>) {
        list.clear()
        list.addAll(title)
        filteredList.clear()
        filteredList.addAll(title)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterBrandBinding.inflate(inflater, parent, false)
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
                    val tempFilteredList = ArrayList<Brand>()
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
                filteredList = filterResults.values as ArrayList<Brand>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class ViewHolder(private val binding: AdapterBrandBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Brand, position: Int) {
            binding.apply {
                brand = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if (position % 2 == 0) R.color.white else R.color.lighterGrey)
                textViewItemCount.setOnClickListener {
                    onClickListener(data, position, false, true)
                }
                imageButtonEdit.setOnClickListener {
                    onClickListener(data, position, false, false)
                }
                imageButtonDelete.setOnClickListener {
                    onClickListener(data, position, true, false)
                }
            }
        }
    }
}