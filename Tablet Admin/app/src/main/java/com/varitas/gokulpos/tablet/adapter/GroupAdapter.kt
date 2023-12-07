package com.varitas.gokulpos.tablet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterGroupBinding
import com.varitas.gokulpos.tablet.model.SelectedGroups
import com.varitas.gokulpos.tablet.utilities.Enums

class GroupAdapter(private var onClickListener: (SelectedGroups, Int, Enums.ClickEvents) -> Unit) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    var list = ArrayList<SelectedGroups>()
    var filteredList = ArrayList<SelectedGroups>()

    fun setList(title: List<SelectedGroups>) {
        list.clear()
        list.addAll(title)
        filteredList.clear()
        filteredList.addAll(title)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterGroupBinding.inflate(inflater, parent, false)
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
                val searchString = charSequence.toString().lowercase()
                filteredList = if(searchString.isEmpty()) {
                    list
                } else {
                    val tempFilteredList = ArrayList<SelectedGroups>()
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
                filteredList = filterResults.values as ArrayList<SelectedGroups>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class ViewHolder(private val binding : AdapterGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SelectedGroups, position: Int) {
            binding.apply {
                group = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if (position % 2 == 0) R.color.white else R.color.lighterGrey)
                imageButtonEdit.setOnClickListener {
                    onClickListener(data, position, Enums.ClickEvents.EDIT)
                }
                buttonItemCount.setOnClickListener {
                    binding.root.post {
                        onClickListener(data, position, Enums.ClickEvents.VIEW)
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