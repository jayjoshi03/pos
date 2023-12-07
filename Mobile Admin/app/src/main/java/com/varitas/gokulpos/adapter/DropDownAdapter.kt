package com.varitas.gokulpos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterDropdownBinding
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.response.DynamicDropDown
import com.varitas.gokulpos.utilities.CustomSpinner

class DropDownAdapter(private var onClickListener : (CommonDropDown, Int, String, Int) -> Unit) : RecyclerView.Adapter<DropDownAdapter.ViewHolder>() {
    var list = ArrayList<DynamicDropDown>()
    private lateinit var spinnerAdapter : ArrayAdapter<CommonDropDown>
    lateinit var context : Context

    fun setList(title : DynamicDropDown) {
        list.add(0, title)
        notifyItemInserted(0)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterDropdownBinding.inflate(inflater, parent, false)
        context = parent.context
        return ViewHolder(binding)
    }

    override fun getItemCount() : Int {
        return list.size
    }

    override fun onBindViewHolder(holder : ViewHolder, position : Int) {
        holder.bind(list[position])
    }

    inner class ViewHolder(private val binding : AdapterDropdownBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : DynamicDropDown) {
            if(data.list.size > 0) {
                spinnerAdapter = ArrayAdapter(context, R.layout.spinner_items, data.list)
                binding.apply {
                    spinnerSizePack.apply {
                        adapter = spinnerAdapter
                        setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                            override fun onSpinnerOpened(spinner : Spinner?) {

                            }

                            override fun onSpinnerClosed(spinner : Spinner?) {
                                val selectedItem = spinner?.selectedItem as CommonDropDown
                                //onClickListener(selectedItem, data.list[0].value!!, data.list[0].label!!, selectedItem.value!!, data.editSpecificationId)
                                onClickListener(selectedItem, data.list[0].value!!, data.list[0].label!!, data.specificationId)
                            }
                        })
                    }

                    spinnerAdapter.apply {
                        setDropDownViewResource(R.layout.spinner_dropdown_item)
                        val ind = data.list.indexOfFirst { sizePack-> sizePack.value == data.id }
                        if(ind >= 0) {
                            onClickListener(data.list[ind], data.list[0].value!!, data.list[0].label!!, data.specificationId)
                            binding.spinnerSizePack.setSelection(ind)
                        }
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }
}