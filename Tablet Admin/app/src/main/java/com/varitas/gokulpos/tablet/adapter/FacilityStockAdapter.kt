package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterFacilityStockBinding
import com.varitas.gokulpos.tablet.response.Facility

class FacilityStockAdapter : RecyclerView.Adapter<FacilityStockAdapter.FacilityViewHolder>() {
    var list = ArrayList<Facility>()

    fun setList(title: List<Facility>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterFacilityStockBinding.inflate(inflater, parent, false)
        return FacilityViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FacilityViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    inner class FacilityViewHolder(private val binding: AdapterFacilityStockBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Facility, position: Int) {
            binding.apply {
                facility = data
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if (position % 2 == 0) R.color.white else R.color.lighterGrey)
            }
        }
    }
}