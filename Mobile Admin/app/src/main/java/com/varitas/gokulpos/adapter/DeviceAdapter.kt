package com.varitas.gokulpos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.databinding.AdapterDeviceBinding
import com.varitas.gokulpos.response.DeviceList

class DeviceAdapter(private var onClickListener: (DeviceList, Int) -> Unit) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    var lists = mutableListOf<DeviceList>()
    var filteredList = ArrayList<DeviceList>()

    fun setList(device: List<DeviceList>) {
        lists.clear()
        lists.addAll(device)
        filteredList.clear()
        filteredList.addAll(device)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterDeviceBinding.inflate(inflater, parent, false)
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
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
                    lists as ArrayList<DeviceList>
                } else {
                    val tempFilteredList = ArrayList<DeviceList>()
                    for (product in lists) { // search for user title
                        if (product.deviceName!!.lowercase().contains(searchString) || product.macAddress!!.lowercase().contains(searchString) || product.employeeName!!.lowercase().contains(searchString)) tempFilteredList.add(product)
                    }
                    tempFilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged") @Suppress("UNCHECKED_CAST") override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                filteredList = filterResults.values as ArrayList<DeviceList>
                notifyDataSetChanged()
            }
        }
    } //endregion

    inner class DeviceViewHolder(private val binding: AdapterDeviceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(device: DeviceList, position: Int) {
            binding.device = device
            binding.apply {
                linearLayoutDetails.setOnClickListener {
                    onClickListener(device, position)
                }
            }
        }
    }
}