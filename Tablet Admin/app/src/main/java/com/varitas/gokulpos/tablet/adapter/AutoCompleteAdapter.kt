package com.varitas.gokulpos.tablet.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import com.varitas.gokulpos.tablet.response.FavouriteItems

class AutoCompleteAdapter(context: Context, resource: Int, private val items: List<FavouriteItems>) :
        ArrayAdapter<FavouriteItems>(context, resource, items) {

    private val filter = CustomFilter()

    override fun getFilter(): Filter {
        return filter
    }

    inner class CustomFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            val filteredItems = ArrayList<FavouriteItems>()

            constraint?.let { query ->
                // Custom logic to filter based on constraint (prefix matching)
                for (item in items) {
                    if (item.name!!.contains(query, ignoreCase = true))
                        filteredItems.add(item)
                }
            }

            results.values = filteredItems
            results.count = filteredItems.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            results?.let {
                clear()
                addAll(it.values as List<FavouriteItems>)
                notifyDataSetChanged()
            }
        }
    }
}