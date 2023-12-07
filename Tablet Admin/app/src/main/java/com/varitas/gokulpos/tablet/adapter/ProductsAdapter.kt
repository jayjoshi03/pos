package com.varitas.gokulpos.tablet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterProductsBinding
import com.varitas.gokulpos.tablet.response.ProductList
import com.varitas.gokulpos.tablet.utilities.Enums

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

    fun clearData(){
        list.clear()
        filteredList.clear()
        notifyDataSetChanged()
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
            binding.apply {
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if (position % 2 == 0) R.color.white else R.color.lighterGrey)
                imageButtonFavourite.setImageResource(if (product.isShortcut!!) R.drawable.ic_favourite else R.drawable.ic_star)
                radioButtonIsSelect.isChecked = product.isSelected
                radioButtonIsSelect.setOnClickListener {
                    binding.root.post {
                        if (product.isSelected) {
                            product.isSelected = false
                            notifyItemChanged(position)
                        } else {
                            for (i in list.indices) list[i].isSelected = (product.id == list[i].id)
                            notifyDataSetChanged()
                        }
                    }
                }
                buttonPrice.setOnClickListener {
                    binding.root.post {
                        onClickListener(product, position, Enums.ClickEvents.PRICE)
                    }
                }
                imageButtonFavourite.setOnClickListener {
                    binding.root.post {
                        onClickListener(product, position, Enums.ClickEvents.FAVOURITE)
                    }
                }
                buttonQty.setOnClickListener {
                    binding.root.post {
                        onClickListener(product, position, Enums.ClickEvents.QUANTITY)
                    }
                }
            }
        }
    }
}