package com.varitas.gokulpos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterAddpurchaseitemBinding
import com.varitas.gokulpos.response.DetailsAt

class AddPurchaseItemAdapter(private var onClickListener : (Int) -> Unit) : RecyclerView.Adapter<AddPurchaseItemAdapter.AddPurchaseViewHolder>() {
    var list = ArrayList<DetailsAt>()

    fun setList(title : List<DetailsAt>) {
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    fun addPurchaseItemList(title : DetailsAt) {
        list.add(0, title)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : AddPurchaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterAddpurchaseitemBinding.inflate(inflater, parent, false)
        return AddPurchaseViewHolder(binding)
    }

    override fun getItemCount() : Int {
        return list.size
    }

    override fun onBindViewHolder(holder : AddPurchaseViewHolder, position : Int) {
        holder.bind(list[position], position)
    }

    inner class AddPurchaseViewHolder(private val binding : AdapterAddpurchaseitemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : DetailsAt, position : Int) {
            binding.apply {
                item = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if(position % 2 == 0) R.color.white else R.color.lighterGrey)
                imageButtonDelete.setOnClickListener {
                    onClickListener(position)
                }
            }
        }
    }
}