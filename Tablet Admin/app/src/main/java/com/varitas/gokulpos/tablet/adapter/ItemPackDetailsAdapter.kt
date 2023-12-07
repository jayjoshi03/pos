package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.databinding.AdapterPacksBinding
import com.varitas.gokulpos.tablet.model.ItemDetailsPack
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums

class ItemPackDetailsAdapter(private var onClickListener: (ItemDetailsPack, Int, Int, Enums.ClickEvents) -> Unit) : RecyclerView.Adapter<ItemPackDetailsAdapter.ViewHolder>() {

    var list = ArrayList<ItemDetailsPack>()
    private var previouslySelectedPosition = 0

    fun setList(menu: List<ItemDetailsPack>) {
        list.clear()
        list.addAll(menu)
        notifyItemRangeChanged(0, list.size)
    }

    fun addData(data: ItemDetailsPack) {
        list.add(data)
        notifyItemInserted(list.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterPacksBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: AdapterPacksBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(items: ItemDetailsPack, position: Int) {
            binding.apply {
                buttonFavItem.apply {
                    setOnClickListener {
                        // Store the previously selected position
                        val previousPosition = previouslySelectedPosition

                        // Update the previouslySelectedPosition with the current position
                        previouslySelectedPosition = position

                        // Notify the adapter that the item at the previous position has changed
                        if (previousPosition != RecyclerView.NO_POSITION) {
                            notifyItemChanged(previousPosition)
                        }

                        // Notify the adapter that the item at the current position has changed
                        notifyItemChanged(position)

                        // Handle the click event here or pass it to your click listener
                        onClickListener(items, position, previousPosition, Enums.ClickEvents.VIEW)
                    }

                    // Set the selected state of the button based on the position
                    isSelected = position == previouslySelectedPosition
                }

                name = items.name

                imageButtonCancel.apply {
                    setOnClickListener {
                        items.itemDetails!!.flag = Default.DELETE
                        onClickListener(items, position, 0, Enums.ClickEvents.DELETE)
                    }
                }
            }
        }
    }
}