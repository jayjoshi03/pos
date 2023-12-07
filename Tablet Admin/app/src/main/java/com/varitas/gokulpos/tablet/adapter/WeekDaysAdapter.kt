package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterWeekDaysBinding
import com.varitas.gokulpos.tablet.response.DataDetails

class WeekDaysAdapter : RecyclerView.Adapter<WeekDaysAdapter.ViewHolder>() {

    var list = ArrayList<DataDetails>()

    fun setList(title: List<DataDetails>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterWeekDaysBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: AdapterWeekDaysBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataDetails, position: Int) {
            binding.apply {
                day = data
                checkBoxTaxSelector.background = ContextCompat.getDrawable(itemView.context, if (position % 2 == 0) R.color.white else R.color.lighterGrey)
                binding.checkBoxTaxSelector.setOnCheckedChangeListener { _, isChecked ->
                    data.isSelected = isChecked
                }
            }
        }
    }
}