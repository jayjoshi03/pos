package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterDrawerTransactionBinding
import com.varitas.gokulpos.tablet.response.DrawerTransaction
import com.varitas.gokulpos.tablet.utilities.Constants
import com.varitas.gokulpos.tablet.utilities.Utils

class DrawerAdapter : RecyclerView.Adapter<DrawerAdapter.ViewHolder>() {
    var list = ArrayList<DrawerTransaction>()

    fun setList(title: List<DrawerTransaction>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterDrawerTransactionBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(private val binding: AdapterDrawerTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DrawerTransaction, position: Int) {
            binding.apply {
                data.transactionDateTime = Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormatT, parseDate = data.transactionDateTime))
                drawer = data
                pos = position + 1
                linearLayoutDetails.background = ContextCompat.getDrawable(itemView.context, if (position % 2 == 0) R.color.white else R.color.lighterGrey)
            }
        }
    }
}