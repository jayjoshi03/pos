package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterDiscountBinding
import com.varitas.gokulpos.tablet.response.DiscountMapList

class OrderDiscountAdapter : RecyclerView.Adapter<OrderDiscountAdapter.ViewHolder>() {

    var list = ArrayList<DiscountMapList>()

    fun setList(menu: List<DiscountMapList>) {
        list.clear()
        list.addAll(menu)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterDiscountBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: AdapterDiscountBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(items: DiscountMapList, position: Int) {
            binding.apply {
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if (position % 2 == 0) R.color.white else R.color.lighterGrey)
                radioButtonIsSelect.isChecked = items.isSelected
                radioButtonIsSelect.setOnClickListener {
                    binding.root.post {
                        if (items.isSelected) {
                            items.isSelected = false
                            notifyItemChanged(position)
                        } else {
                            for (i in list.indices) list[i].isSelected = (items.id == list[i].id && items.specificationId == list[i].specificationId)
                            notifyDataSetChanged()
                        }
                    }
                }

                discount = items
            }
        }
    }
}