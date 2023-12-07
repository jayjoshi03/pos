package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterAddpurchaseorderBinding
import com.varitas.gokulpos.tablet.response.PurchaseOrderDetail

class AddPurchaseOrderAdapter(private var onClickListener : (Int) -> Unit) : RecyclerView.Adapter<AddPurchaseOrderAdapter.ViewHolder>() {
    var list = ArrayList<PurchaseOrderDetail>()

    fun setList(title : List<PurchaseOrderDetail>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    fun addPurchaseOrderList(title : PurchaseOrderDetail) {
        list.add(0, title)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterAddpurchaseorderBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder : ViewHolder, position : Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount() : Int {
        return list.size
    }

    inner class ViewHolder(private val binding : AdapterAddpurchaseorderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : PurchaseOrderDetail, position : Int) {
            binding.apply {
                items = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if(position % 2 == 0) R.color.white else R.color.lighterGrey)
                imageViewAction.setOnClickListener {
                    onClickListener(position)
                }
            }
        }
    }
}