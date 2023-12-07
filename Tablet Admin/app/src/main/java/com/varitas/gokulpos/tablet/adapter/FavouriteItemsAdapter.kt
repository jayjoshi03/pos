package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterFavItemsBinding
import com.varitas.gokulpos.tablet.response.DataDetails

class FavouriteItemsAdapter(private var onClickListener: (DataDetails) -> Unit) : RecyclerView.Adapter<FavouriteItemsAdapter.ViewHolder>() {

    var list = ArrayList<DataDetails>()

    fun setList(menu: List<DataDetails>) {
        list.clear()
        list.addAll(menu)
        notifyItemRangeChanged(0, list.size)
    }

    fun clearData() {
        list.clear()
        notifyDataSetChanged()
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
        fun bind(items: DataDetails) {

            binding.apply {
                buttonFavItem.background.setTint(ContextCompat.getColor(itemView.context, if (items.id == 0) R.color.pink else R.color.white))
                buttonFavItem.setTextColor(ContextCompat.getColor(itemView.context, if (items.id == 0) R.color.white else R.color.black))
                buttonFavItem.apply {
                    setOnClickListener {
                        onClickListener(items)
                    }
                }
                name = items.name
            }
        }
    }
}