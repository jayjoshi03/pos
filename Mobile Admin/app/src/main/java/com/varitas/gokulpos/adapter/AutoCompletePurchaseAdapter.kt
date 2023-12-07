package com.varitas.gokulpos.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import com.varitas.gokulpos.response.PurchaseOrder

class AutoCompletePurchaseAdapter(context : Context, resource : Int, private val purchaseOrder : List<PurchaseOrder>) :
    ArrayAdapter<PurchaseOrder>(context, resource, purchaseOrder) {

    private val filter = CustomFilter()

    override fun getFilter() : Filter {
        return filter
    }

    inner class CustomFilter : Filter() {
        override fun performFiltering(constraint : CharSequence?) : FilterResults {
            val results = FilterResults()
            val filteredItems = ArrayList<PurchaseOrder>()

            constraint?.let { query->
                // Custom logic to filter based on constraint (prefix matching)
                for(search in purchaseOrder) {
                    if(search.purchaseOrderNo!!.contains(query, ignoreCase = true))
                        filteredItems.add(search)
                }
            }

            results.values = filteredItems
            results.count = filteredItems.size
            return results
        }

        override fun publishResults(constraint : CharSequence?, results : FilterResults?) {
            results?.let {
                clear()
                addAll(it.values as List<PurchaseOrder>)
                notifyDataSetChanged()
            }
        }
    }
}