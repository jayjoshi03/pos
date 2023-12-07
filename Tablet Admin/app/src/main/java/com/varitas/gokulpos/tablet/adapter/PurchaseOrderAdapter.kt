package com.varitas.gokulpos.tablet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterPurchaseOrderBinding
import com.varitas.gokulpos.tablet.response.PurchaseOrder
import com.varitas.gokulpos.tablet.utilities.Constants
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.Utils

class PurchaseOrderAdapter(private var onClickListener : (PurchaseOrder, Int, Enums.ClickEvents) -> Unit) : RecyclerView.Adapter<PurchaseOrderAdapter.ViewHolder>() {
    var list = ArrayList<PurchaseOrder>()
    var filteredList = ArrayList<PurchaseOrder>()

    fun setList(title : List<PurchaseOrder>) {
        list.clear()
        list.addAll(title)
        filteredList.clear()
        filteredList.addAll(title)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterPurchaseOrderBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder : ViewHolder, position : Int) {
        holder.bind(filteredList[position], position)
    }

    override fun getItemCount() : Int {
        return filteredList.size
    }

    //region To get filtered data
    fun getFilter() : Filter {
        return object : Filter() {
            override fun performFiltering(charSequence : CharSequence) : FilterResults {
                val searchString = charSequence.toString()
                filteredList = if(searchString.isEmpty()) {
                    list
                } else {
                    val tempFilteredList = ArrayList<PurchaseOrder>()
                    for(product in list) { // search for user title
                        if(product.purchaseOrderNo!!.lowercase().contains(searchString)) tempFilteredList.add(product)
                    }
                    tempFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged") @Suppress("UNCHECKED_CAST") override fun publishResults(charSequence : CharSequence?, filterResults : FilterResults) {
                filteredList = filterResults.values as ArrayList<PurchaseOrder>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class ViewHolder(private val binding : AdapterPurchaseOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : PurchaseOrder, position : Int) {
            binding.apply {
                data.expectedDeliveryDate = Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormatT, parseDate = data.expectedDeliveryDate))
                po = data
                pos = position + 1
                status = if(data.status == Default.ORDER_PLACED) itemView.resources.getString(R.string.lbl_OrderPlaced) else if(data.status == Default.DISPATCH) itemView.resources.getString(R.string.lbl_Dispatch) else if(data.status == Default.PARTIALLY_RECEIVED) itemView.resources.getString(R.string.lbl_PartiallyReceived) else if(data.status == Default.FULLY_RECEIVED) itemView.resources.getString(R.string.lbl_FullyReceived) else if(data.status == Default.CANCEL) itemView.resources.getString(R.string.lbl_Cancel) else ""
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if(position % 2 == 0) R.color.white else R.color.lighterGrey)
                imageButtonInvoice.visibility = if(data.status == 24) View.INVISIBLE else View.VISIBLE
                imageButtonInvoice.setImageResource(if(data.isMake) R.drawable.ic_favourite else R.drawable.ic_star)
                imageButtonDelete.setOnClickListener {
                    binding.root.post {
                        onClickListener(data, position, Enums.ClickEvents.DELETE)
                    }
                }
                imageButtonEdit.setOnClickListener {
                    binding.root.post {
                        onClickListener(data, position, Enums.ClickEvents.EDIT)
                    }
                }
                imageButtonInvoice.setOnClickListener {
                    binding.root.post {
                        onClickListener(data, position, Enums.ClickEvents.INVOICE)
                    }
                }
            }
        }
    }
}