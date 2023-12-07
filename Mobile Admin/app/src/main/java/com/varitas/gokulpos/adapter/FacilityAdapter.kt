package com.varitas.gokulpos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterFacilityBinding
import com.varitas.gokulpos.response.Facility

class FacilityAdapter(private var onClickListener : (Facility, Int, Boolean) -> Unit) : RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder>() {
    var list = ArrayList<Facility>()
    var filteredList = ArrayList<Facility>()

    fun setList(title : List<Facility>) {
        list.clear()
        list.addAll(title)
        filteredList.clear()
        filteredList.addAll(title)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : FacilityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterFacilityBinding.inflate(inflater, parent, false)
        return FacilityViewHolder(binding)
    }

    override fun getItemCount() : Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder : FacilityViewHolder, position : Int) {
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
                    val tempFilteredList = ArrayList<Facility>()
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
                filteredList = filterResults.values as ArrayList<Facility>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class FacilityViewHolder(private val binding : AdapterFacilityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : Facility, position : Int) {
            binding.apply {
                facility = data
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