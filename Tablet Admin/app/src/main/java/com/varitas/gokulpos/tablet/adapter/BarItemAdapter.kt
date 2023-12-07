package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterBarItemBinding
import com.varitas.gokulpos.tablet.request.Attributes

class BarItemAdapter(private var onClickListener: (Int) -> Unit) : RecyclerView.Adapter<BarItemAdapter.ViewHolder>() {

    var list = ArrayList<Attributes>()

    fun setList(title: List<Attributes>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterBarItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: AdapterBarItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Attributes, position: Int) {
            binding.apply {
                item = data
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if (position % 2 == 0) R.color.white else R.color.lighterGrey)
                imageViewAction.setOnClickListener {
                    onClickListener(position)
                }
            }
        }
    }
}