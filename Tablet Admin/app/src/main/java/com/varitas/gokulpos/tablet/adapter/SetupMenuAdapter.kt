package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.databinding.AdapterFavItemsBinding

class SetupMenuAdapter(private var onClickListener: (String) -> Unit) : RecyclerView.Adapter<SetupMenuAdapter.ViewHolder>() {

    var list = ArrayList<String>()

    fun setList(menu: List<String>) {
        list.clear()
        list.addAll(menu)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterFavItemsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: AdapterFavItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(items: String) {
            binding.apply {
                buttonFavItem.apply {
                    setOnClickListener {
                        onClickListener(items)
                    }
                }
                name = items
            }
        }
    }
}