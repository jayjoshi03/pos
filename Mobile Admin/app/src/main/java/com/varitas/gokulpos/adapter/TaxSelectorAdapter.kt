package com.varitas.gokulpos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterTaxSelectorBinding
import com.varitas.gokulpos.response.Group

class TaxSelectorAdapter : RecyclerView.Adapter<TaxSelectorAdapter.ViewHolder>() {

    var list = ArrayList<Group>()

    fun setList(title : List<Group>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ViewHolder {
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
        fun bind(data : Group, position : Int) {
            binding.apply {
                group = data
                checkBoxTaxSelector.background = ContextCompat.getDrawable(itemView.context, if(position % 2 == 0) R.color.white else R.color.lighterGrey)
                checkBoxTaxSelector.setOnCheckedChangeListener { _, isChecked->
                    data.isGroup = isChecked
                }
            }
        }
    }
}