package com.varitas.gokulpos.tablet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterOrderBinding
import com.varitas.gokulpos.tablet.response.OrderList
import com.varitas.gokulpos.tablet.utilities.Constants
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.Utils


class OrdersAdapter(private var onClickListener: (OrderList, Int, Enums.ClickEvents) -> Unit) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    var list = ArrayList<OrderList>()
    var filteredList = ArrayList<OrderList>()

    fun setList(order: List<OrderList>) {
        list.clear()
        list.addAll(order)
        filteredList.clear()
        filteredList.addAll(order)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterOrderBinding.inflate(inflater, parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(filteredList[position], position)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    //region To get filtered data
    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val searchString = charSequence.toString().lowercase()
                filteredList = if (searchString.isEmpty()) {
                    list
                } else {
                    val tempFilteredList = ArrayList<OrderList>()
                    for (product in list) { // search for user title
                        if (product.orderNo!!.lowercase().contains(searchString)) tempFilteredList.add(product)
                    }
                    tempFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged") @Suppress("UNCHECKED_CAST") override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                filteredList = filterResults.values as ArrayList<OrderList>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class OrderViewHolder(private val binding: AdapterOrderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderList, position: Int) {
            binding.apply {
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if (position % 2 == 0) R.color.white else R.color.lighterGrey)
                order.date = Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormatT, parseDate = order.date))
                textViewSave.setOnClickListener {
                    binding.root.post {
                        onClickListener(order, position, Enums.ClickEvents.SAVE)
                    }
                }

                textViewPrint.setOnClickListener {
                    binding.root.post {
                        onClickListener(order, position, Enums.ClickEvents.PRINT)
                    }
                }

                textViewLoad.setOnClickListener {
                    binding.root.post {
                        onClickListener(order, position, Enums.ClickEvents.LOAD)
                    }
                }

                textViewItems.setOnClickListener {
                    binding.root.post {
                        onClickListener(order, position, Enums.ClickEvents.ITEMS)
                    }
                }

                imageViewDelete.setOnClickListener {
                    binding.root.post {
                        onClickListener(order, position, Enums.ClickEvents.DELETE)
                    }
                }

            }
            binding.order = order
        }
    }
}