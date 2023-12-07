package com.varitas.gokulpos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varitas.gokulpos.databinding.AdapterNotificationBinding
import com.varitas.gokulpos.response.Notifications

class NotificationAdapter(private var onClickListener : (Notifications, Int) -> Unit) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    var list=ArrayList<Notifications>()

    fun setList(notification : List<Notifications>) {
        list.clear()
        list.addAll(notification)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : NotificationViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val binding=AdapterNotificationBinding.inflate(inflater, parent, false)
        return NotificationViewHolder(binding)
    }

    override fun getItemCount() : Int {
        return list.size
    }

    override fun onBindViewHolder(holder : NotificationViewHolder, position : Int) {
        holder.bind(list[position], position)
    }

    inner class NotificationViewHolder(var binding : AdapterNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : Notifications, position : Int) {
            binding.apply {
                notification=data
                switchNotification.setOnCheckedChangeListener { _, isChecked->
                    if (data.isActive != isChecked) {
                        data.isActive=isChecked
                        onClickListener(data, position)
                    }
                }
            }
        }
    }
}