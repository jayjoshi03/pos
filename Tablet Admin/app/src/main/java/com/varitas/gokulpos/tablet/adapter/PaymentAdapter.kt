package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterPaymentBinding
import com.varitas.gokulpos.tablet.request.OrderPaymentRequest

class PaymentAdapter : RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {

    var list = ArrayList<OrderPaymentRequest>()

    fun addToList(title: OrderPaymentRequest) {
        list.add(0, title)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterPaymentBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: AdapterPaymentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: OrderPaymentRequest, position: Int) {
            binding.apply {
                payment = data
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if (position % 2 == 0) R.color.white else R.color.lighterGrey)
            }
        }
    }
}