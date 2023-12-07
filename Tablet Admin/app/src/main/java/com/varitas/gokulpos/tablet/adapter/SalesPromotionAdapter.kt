package com.varitas.gokulpos.tablet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterSalesPromotionBinding
import com.varitas.gokulpos.tablet.response.SalesPromotion
import com.varitas.gokulpos.tablet.utilities.Constants
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.Utils

class SalesPromotionAdapter(private var onClickListener: (SalesPromotion, Int, Enums.ClickEvents) -> Unit) : RecyclerView.Adapter<SalesPromotionAdapter.ViewHolder>() {
    var list = ArrayList<SalesPromotion>()
    var filteredList = ArrayList<SalesPromotion>()

    fun setList(title: List<SalesPromotion>) {
        list.clear()
        list.addAll(title)
        filteredList.clear()
        filteredList.addAll(title)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterSalesPromotionBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position], position)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    //region To get filtered data
    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val searchString = charSequence.toString()
                filteredList = if (searchString.isEmpty()) {
                    list
                } else {
                    val tempFilteredList = ArrayList<SalesPromotion>()
                    for (product in list) { // search for user title
                        if (product.name!!.lowercase().contains(searchString)) tempFilteredList.add(product)
                    }
                    tempFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged") @Suppress("UNCHECKED_CAST") override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                filteredList = filterResults.values as ArrayList<SalesPromotion>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class ViewHolder(private val binding: AdapterSalesPromotionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SalesPromotion, position: Int) {
            binding.apply {
                data.startDate = Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormat_yyyy_MM_dd, parseDate = data.startDate))
                data.endDate = Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormat_yyyy_MM_dd, parseDate = data.endDate))
                po = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if (position % 2 == 0) R.color.white else R.color.lighterGrey)
                imageButtonEdit.setOnClickListener {
                    binding.root.post {
                        onClickListener(data, position, Enums.ClickEvents.EDIT)
                    }
                }
                imageButtonDelete.setOnClickListener {
                    binding.root.post {
                        onClickListener(data, position, Enums.ClickEvents.DELETE)
                    }
                }
            }
        }
    }
}