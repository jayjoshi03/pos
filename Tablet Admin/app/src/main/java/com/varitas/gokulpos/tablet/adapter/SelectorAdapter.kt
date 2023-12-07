package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterTaxSelectorBinding
import com.varitas.gokulpos.tablet.model.SelectedGroups

class SelectorAdapter : RecyclerView.Adapter<SelectorAdapter.ViewHolder>() {

    var list = ArrayList<SelectedGroups>()

    fun setList(title: List<SelectedGroups>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterTaxSelectorBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder : ViewHolder, position : Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount() : Int {
        return list.size
    }

    inner class ViewHolder(private val binding : AdapterTaxSelectorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SelectedGroups, position: Int) {
            binding.apply {
                group = data
                checkBoxTaxSelector.background = ContextCompat.getDrawable(itemView.context, if (position % 2 == 0) R.color.white else R.color.lighterGrey)
                checkBoxTaxSelector.setOnCheckedChangeListener { _, isChecked ->
                    data.isGroup = isChecked
                }
            }
        }
    }
}