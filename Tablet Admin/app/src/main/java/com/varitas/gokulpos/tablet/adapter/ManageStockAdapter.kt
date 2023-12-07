package com.varitas.gokulpos.tablet.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.databinding.AdapterStockManageBinding
import com.varitas.gokulpos.tablet.response.Facility

class ManageStockAdapter : RecyclerView.Adapter<ManageStockAdapter.StockViewHolder>() {
    var list = ArrayList<Facility>()

    fun setList(title: List<Facility>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterStockManageBinding.inflate(inflater, parent, false)
        return StockViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    inner class StockViewHolder(private val binding: AdapterStockManageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Facility, position: Int) {
            binding.apply {
                stock = data
                textInputQty.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable) {}
                    override fun beforeTextChanged(
                            s: CharSequence, start: Int,
                            count: Int, after: Int,
                                                  ) {
                    }

                    override fun onTextChanged(
                            s: CharSequence, start: Int,
                            before: Int, count: Int,
                                              ) {
                        if (s.isNotEmpty()) data.quantity = s.toString().toInt()
                    }
                })
            }
        }
    }
}