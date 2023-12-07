package com.varitas.gokulpos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.AdapterMultipackBinding
import com.varitas.gokulpos.request.ItemPrice

class MultiPackAdapter(private var onClickListener : (ItemPrice, Int) -> Unit) : RecyclerView.Adapter<MultiPackAdapter.MultiPackViewHolder>() {
    var list = ArrayList<ItemPrice>()
    var filteredList = ArrayList<ItemPrice>()

    fun setList(title : List<ItemPrice>) {
        list.clear()
        list.addAll(title)
        filteredList.clear()
        filteredList.addAll(title)
        notifyItemRangeChanged(0, filteredList.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : MultiPackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterMultipackBinding.inflate(inflater, parent, false)
        return MultiPackViewHolder(binding)
    }

    override fun getItemCount() : Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder : MultiPackViewHolder, position : Int) {
        holder.bind(filteredList[position], position)
    }

    inner class MultiPackViewHolder(private val binding : AdapterMultipackBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : ItemPrice, position : Int) {
            binding.apply {
                item = data
                //textInputUnitCost.setText(Utils.getTwoDecimalValue(data.unitCost!!))
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if(position % 2 == 0) R.color.white else R.color.lighterGrey)
                imageViewDetails.setOnClickListener {
                    binding.root.post {
                        onClickListener(data, position)
                    }
                }
            }
        }
    }
}