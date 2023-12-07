package com.varitas.gokulpos.tablet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterCustomerDetailsBinding
import com.varitas.gokulpos.tablet.response.Customers
import com.varitas.gokulpos.tablet.utilities.Enums

class CustomerDetailAdapter(private var isSelect: Boolean = false, private var onClickListener: (Customers, Enums.ClickEvents, Int) -> Unit) : RecyclerView.Adapter<CustomerDetailAdapter.CustomerViewHolder>() {

    var list = ArrayList<Customers>()
    var filteredList = ArrayList<Customers>()

    fun setList(customer: List<Customers>) {
        list.clear()
        list.addAll(customer)
        filteredList.clear()
        filteredList.addAll(customer)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterCustomerDetailsBinding.inflate(inflater, parent, false)
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.bind(filteredList[position], position)
    }

    override fun getItemCount(): Int {
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
                    val tempFilteredList = ArrayList<Customers>()
                    for(product in list) { // search for user title
                        if(product.name!!.lowercase().contains(searchString)||product.phonenNo!!.lowercase().contains(searchString)||product.emailId!!.lowercase().contains(searchString)||product.drivingLicense!!.lowercase().contains(searchString)) tempFilteredList.add(product)
                    }
                    tempFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged") @Suppress("UNCHECKED_CAST") override fun publishResults(charSequence : CharSequence?, filterResults : FilterResults) {
                filteredList = filterResults.values as ArrayList<Customers>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class CustomerViewHolder(private val binding: AdapterCustomerDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(customer: Customers, position: Int) {
            binding.customer = customer
            binding.apply {
                pos = position + 1

                imageButtonSelect.visibility = if (isSelect) View.VISIBLE else View.GONE
                linearLayoutActions.visibility = if (isSelect) View.GONE else View.VISIBLE

                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if (position % 2 == 0) R.color.white else R.color.lighterGrey)
                imageButtonSelect.setOnClickListener {
                    binding.root.post {
                        onClickListener(customer, Enums.ClickEvents.VIEW, position)
                    }
                }

                imageButtonEdit.setOnClickListener {
                    binding.root.post {
                        onClickListener(customer, Enums.ClickEvents.EDIT, position)
                    }
                }

                imageButtonDelete.setOnClickListener {
                    binding.root.post {
                        onClickListener(customer, Enums.ClickEvents.DELETE, position)
                    }
                }
            }
        }
    }
}