package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterPaymetListBinding
import com.varitas.gokulpos.tablet.response.PaymentInvoice

class PaymentListAdapter(private var onClickListener : (Int) -> Unit) : RecyclerView.Adapter<PaymentListAdapter.PaymentViewHolder>() {
    var list = ArrayList<PaymentInvoice>()
    fun setList(title : List<PaymentInvoice>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    fun addPayList(title : PaymentInvoice) {
        list.add(0, title)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : PaymentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterPaymetListBinding.inflate(inflater, parent, false)
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder : PaymentViewHolder, position : Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount() : Int {
        return list.size
    }

    inner class PaymentViewHolder(private val binding : AdapterPaymetListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : PaymentInvoice, position : Int) {
            binding.apply {
                payment = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if(position % 2 == 0) R.color.white else R.color.lighterGrey)
                imageButtonDelete.setOnClickListener {
                    binding.root.post {
                        onClickListener(position)
                    }
                }
            }
        }
    }
}