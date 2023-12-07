package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.databinding.AdapterAttributeDetailsBinding
import com.varitas.gokulpos.tablet.response.AttributeDetails

class ItemAttributeDetailsAdapter(private var onClickListener: (AttributeDetails) -> Unit) : RecyclerView.Adapter<ItemAttributeDetailsAdapter.ViewHolder>() {

    var list = ArrayList<AttributeDetails>()

    fun setList(menu: List<AttributeDetails>) {
        list.clear()
        list.addAll(menu)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterAttributeDetailsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: AdapterAttributeDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(items: AttributeDetails) {
            binding.apply {
                checkBoxSelector.apply {
//                    setOnClickListener {
//                        onClickListener(items)
//                    }

                    isChecked = items.isSelected

                    setOnCheckedChangeListener { _, isChecked ->
                        items.isSelected = isChecked
                        onClickListener(items)
                    }
                }
                name = items.name
            }
        }
    }
}