package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.databinding.AdapterCurrencyBinding

class CurrencyAdapter(private var onClickListener: (Double) -> Unit) : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    var list = ArrayList<Double>()

    fun setList(data: List<Double>) {
        list.clear()
        list.addAll(data)
        notifyItemRangeChanged(0, list.size)
    }

    fun clearData() {
        list.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterCurrencyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: AdapterCurrencyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Double) {
            binding.apply {
                currency = data
                buttonCurrency.setOnClickListener {
                    binding.root.post {
                        onClickListener(data)
                    }
                }
            }
        }
    }
}