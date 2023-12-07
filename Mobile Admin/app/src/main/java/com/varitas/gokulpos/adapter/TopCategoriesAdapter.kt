package com.varitas.gokulpos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.R
import com.varitas.gokulpos.activity.ReportDetailsActivity
import com.varitas.gokulpos.databinding.AdapterTopCategoriesBinding
import com.varitas.gokulpos.response.TopCategories
import com.varitas.gokulpos.utilities.Utils


class TopCategoriesAdapter : RecyclerView.Adapter<TopCategoriesAdapter.ViewHolder>() {

    var list=ArrayList<TopCategories>()
    private lateinit var topItemsAdapter : TopItemsAdapter

    fun setList(title : List<TopCategories>) {
        list.clear()
        list.addAll(title)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        topItemsAdapter=TopItemsAdapter()
        val binding=AdapterTopCategoriesBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder : ViewHolder, position : Int) {
        holder.bind(list[position], position + 1)
    }

    override fun getItemCount() : Int {
        return list.size
    }

    inner class ViewHolder(private val binding : AdapterTopCategoriesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : TopCategories, position : Int) {
            val expanded : Boolean=data.isExpanded
            binding.apply {
                recycleViewProducts.visibility=if (expanded) View.VISIBLE else View.GONE
                imageViewAction.startAnimation(animate(expanded))
                recycleViewProducts.apply {
                    adapter=topItemsAdapter
                    layoutManager=LinearLayoutManager(itemView.context)
                }
                if (data.isExpanded) {
                    topItemsAdapter.apply {
                        list.clear()
                        setList(data.topItem!!)
                        recycleViewProducts.smoothScrollToPosition(topItemsAdapter.list.count())
                    }
                }
                imageViewAction.setOnClickListener {
                    data.isExpanded=!expanded
                    notifyItemChanged(position - 1)
                }
                category=data
                textViewProductName.isSelected=true
                srNum=if (position.toString().length > 1) position.toString() else "0${position}"
                amount=Utils.setAmountWithCurrency(itemView.context, data.gross!!)
            }
        }
    }

    private fun animate(up : Boolean) : Animation {
        val anim : Animation=AnimationUtils.loadAnimation(ReportDetailsActivity.Instance, if (up) R.anim.rotate_down else R.anim.rotate_up)
        anim.interpolator=LinearInterpolator() // for smooth animation
        return anim
    }
}