package com.varitas.gokulpos.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.databinding.AdapterStockBinding
import com.varitas.gokulpos.response.ItemStockGet


class StockAdapter(private var onClickListener : (ItemStockGet, Int, Boolean) -> Unit) : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {
    var list = ArrayList<ItemStockGet>()

    fun setList(title : List<ItemStockGet>) {
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : StockViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterStockBinding.inflate(inflater, parent, false)
        return StockViewHolder(binding)
    }

    override fun getItemCount() : Int {
        return list.size
    }

    override fun onBindViewHolder(holder : StockViewHolder, position : Int) {
        holder.bind(list[position], position)
    }

    inner class StockViewHolder(private val binding : AdapterStockBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : ItemStockGet, position : Int) {
            binding.apply {
                stock = data
                val currentQty = data.quantity.toString()
                textInputCurrentQty.setText(currentQty)
                textInputQty.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s : Editable) {}
                    override fun beforeTextChanged(
                        s : CharSequence, start : Int,
                        count : Int, after : Int,
                    ) {
                    }

                    override fun onTextChanged(
                        s : CharSequence, start : Int,
                        before : Int, count : Int,
                    ) {
                        if(s.isNotEmpty()) data.qty = s.toString().toLong()
                    }
                })
                linearLayoutDetails.setOnClickListener {
                    onClickListener(data, position, false)
                }
            }
        }
    }
}