package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterSoldAlongBinding
import com.varitas.gokulpos.tablet.request.SoldAlong

class SoldAlongAdapter(private var onClickListener: (Int) -> Unit) : RecyclerView.Adapter<SoldAlongAdapter.ViewHolder>() {
    var list = ArrayList<SoldAlong>()

    fun setList(title: List<SoldAlong>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    fun addSoldAlong(title: SoldAlong) {
        list.add(0, title)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterSoldAlongBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    inner class ViewHolder(private val binding: AdapterSoldAlongBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SoldAlong, position: Int) {

            binding.apply {
                sold = data
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if (position % 2 == 0) R.color.white else R.color.lighterGrey)
                imageViewAction.setOnClickListener {
                    onClickListener(position)
                }
            }
        }
    }
}