package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.databinding.AdapterSpecificationSelectorBinding
import com.varitas.gokulpos.tablet.response.CommonDropDown

class SpecificationSelectorAdapter : RecyclerView.Adapter<SpecificationSelectorAdapter.ViewHolder>() {

    var list = ArrayList<CommonDropDown>()

    fun setList(title: List<CommonDropDown>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterSpecificationSelectorBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: AdapterSpecificationSelectorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CommonDropDown) {
            binding.apply {
                dropDown = data
                binding.checkBoxSelector.setOnCheckedChangeListener { _, isChecked ->
                    data.isSelected = isChecked
                }
            }
        }
    }
}