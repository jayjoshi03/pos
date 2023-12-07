package com.varitas.gokulpos.tablet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterCommonBinding
import com.varitas.gokulpos.tablet.model.Category

class CategoryAdapter(private var onClickListener : (Category, Int, Boolean, Boolean) -> Unit) : RecyclerView.Adapter<CategoryAdapter.DeptCateViewHolder>() {
    var list = ArrayList<Category>()
    var filteredList = ArrayList<Category>()

    fun setList(title : List<Category>) {
        list.clear()
        list.addAll(title)
        filteredList.clear()
        filteredList.addAll(title)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : DeptCateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterCommonBinding.inflate(inflater, parent, false)
        return DeptCateViewHolder(binding)
    }

    override fun getItemCount() : Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder : DeptCateViewHolder, position : Int) {
        holder.bind(filteredList[position], position)
    }

    //region To get filtered data
    fun getFilter() : Filter {
        return object : Filter() {
            override fun performFiltering(charSequence : CharSequence) : FilterResults {
                val searchString = charSequence.toString().lowercase()
                filteredList = if(searchString.isEmpty()) {
                    list
                } else {
                    val tempFilteredList = ArrayList<Category>()
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
                filteredList = filterResults.values as ArrayList<Category>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class DeptCateViewHolder(private val binding : AdapterCommonBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : Category, position : Int) {
            binding.apply {
                category = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if(position % 2 == 0) R.color.white else R.color.lighterGrey)
                textViewItemCount.setOnClickListener {
                    onClickListener(data, position, false, true)
                }
                imageButtonEdit.setOnClickListener {
                    binding.root.post {
                        onClickListener(data, position, false, false)
                    }
                }
                imageButtonDelete.setOnClickListener {
                    binding.root.post {
                        onClickListener(data, position, true, false)
                    }
                }
            }
        }
    }
}