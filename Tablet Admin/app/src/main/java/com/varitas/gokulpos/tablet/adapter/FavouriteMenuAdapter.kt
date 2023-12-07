package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterFavMenuBinding
import com.varitas.gokulpos.tablet.response.FavouriteMenu
import com.varitas.gokulpos.tablet.utilities.Enums

class FavouriteMenuAdapter(private var onClickListener: (Enums.Menus) -> Unit) : RecyclerView.Adapter<FavouriteMenuAdapter.ViewHolder>() {

    var list = ArrayList<FavouriteMenu>()

    fun setList(data: List<FavouriteMenu>) {
        list.clear()
        list.addAll(data)
        notifyItemRangeChanged(0, list.size)
    }

    fun clearData() {
        list.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterFavMenuBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: AdapterFavMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: FavouriteMenu) {
            binding.apply {
                menu = data
                buttonMenu.setTextColor(ContextCompat.getColor(itemView.context, if (data.isSelected) R.color.base_color else R.color.darkGrey))
                buttonMenu.setOnClickListener {
                    binding.root.post {
                        onClickListener(data.type)
                    }
                }
            }
        }
    }
}