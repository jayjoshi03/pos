package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterPromoteadditemBinding
import com.varitas.gokulpos.tablet.response.DiscountMapList

class PromoteAddItemAdapter(private var onClickListener: (Int) -> Unit) : RecyclerView.Adapter<PromoteAddItemAdapter.ViewHolder>() {
    var list = ArrayList<DiscountMapList>()

    fun setList(title : List<DiscountMapList>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    fun addPromoteList(title : DiscountMapList) {
        list.add(0, title)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterPromoteadditemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder : ViewHolder, position : Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount() : Int {
        return list.size
    }

    inner class ViewHolder(private val binding : AdapterPromoteadditemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : DiscountMapList, position : Int) {
            binding.apply {
                items = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if(position % 2 == 0) R.color.white else R.color.lighterGrey)
                imageViewAction.setOnClickListener {
                    onClickListener(position)
                }
            }
        }
    }
}