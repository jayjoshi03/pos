package com.varitas.gokulpos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterProductsBinding
import com.varitas.gokulpos.response.ProductList
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.utilities.Utils

class ProductsAdapter(private var onClickListener : (ProductList, Int, Enums.ClickEvents) -> Unit) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {
    var list = ArrayList<ProductList>()
    var filteredList = ArrayList<ProductList>()

    fun setList(product : List<ProductList>) {
        list.clear()
        list.addAll(product)
        filteredList.clear()
        filteredList.addAll(product)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterProductsBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder : ProductViewHolder, position : Int) {
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
                    val tempFilteredList = ArrayList<ProductList>()
                    for(product in list) { // search for user title
                        if(product.name!!.lowercase().contains(searchString)) tempFilteredList.add(product)
                    }
                    tempFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged") @Suppress("UNCHECKED_CAST") override fun publishResults(charSequence : CharSequence?, filterResults : FilterResults) {
                filteredList = filterResults.values as ArrayList<ProductList>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class ProductViewHolder(private val binding : AdapterProductsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product : ProductList, position : Int) {
            binding.product = product
            binding.position = position
            binding.apply {
                sku = product.sku
                price = itemView.context.resources.getString(R.string.hint_Price) + ": " + Utils.setAmountWithCurrency(itemView.context, product.price!!)
                qty = itemView.context.resources.getString(R.string.lbl_Qty) + ": " + if(product.quantity.toString().length > 1) product.quantity.toString() else "0" + product.quantity
                textViewPrice.setOnClickListener {
                    onClickListener(product, position, Enums.ClickEvents.PRICE)
                }
                textViewQty.setOnClickListener {
                    onClickListener(product, position, Enums.ClickEvents.QUANTITY)
                }
                imageViewDetails.setOnClickListener {
                    onClickListener(product, position, Enums.ClickEvents.VIEW)
                }
            }
        }
    }
}