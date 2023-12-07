package com.varitas.gokulpos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.databinding.AdapterPaymentTypeBinding
import com.varitas.gokulpos.response.Payments
import com.varitas.gokulpos.utilities.Utils

class PaymentTypeAdapter : RecyclerView.Adapter<PaymentTypeAdapter.PaymentViewHolder>() {

    var list = ArrayList<Payments>()

    fun setList(title: List<Payments>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterPaymentTypeBinding.inflate(inflater, parent, false)
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(list[position], position+1)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class PaymentViewHolder(private val binding: AdapterPaymentTypeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Payments, position: Int) {
            binding.payment = data
            binding.srNum = if (position.toString().length > 1) position.toString() else "0${position}"
            binding.amount = Utils.setAmountWithCurrency(itemView.context, data.orderTotal)
        }
    }
}