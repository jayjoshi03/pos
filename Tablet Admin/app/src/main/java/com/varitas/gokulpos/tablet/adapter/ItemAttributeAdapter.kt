package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.activity.OrderActivity
import com.varitas.gokulpos.tablet.databinding.AdapterAttributesBinding
import com.varitas.gokulpos.tablet.response.ItemAttributes

class ItemAttributeAdapter(private var onClickListener: (ItemAttributes) -> Unit) : RecyclerView.Adapter<ItemAttributeAdapter.ViewHolder>() {

    var list = ArrayList<ItemAttributes>()
    private lateinit var itemAttributeDetailsAdapter: ItemAttributeDetailsAdapter

    fun setList(menu: List<ItemAttributes>) {
        list.clear()
        list.addAll(menu)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterAttributesBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: AdapterAttributesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(items: ItemAttributes) {
            itemAttributeDetailsAdapter = ItemAttributeDetailsAdapter {
                onClickListener(items)
            }

            binding.apply {
                recycleViewAttributeList.apply {
                    adapter = itemAttributeDetailsAdapter
                    layoutManager = GridLayoutManager(OrderActivity.Instance, 4)
                }
//                buttonFavItem.apply {
//                    setOnClickListener {
//                        onClickListener(items)
//                    }
//                }
                itemAttributeDetailsAdapter.apply {
                    setList(items.listAttributes)
                }
                name = items.attributeName
            }
        }
    }
}