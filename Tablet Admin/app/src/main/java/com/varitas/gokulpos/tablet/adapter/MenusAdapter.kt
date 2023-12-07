package com.varitas.gokulpos.tablet.adapter

import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.AdapterMenuBinding
import com.varitas.gokulpos.tablet.response.Menus
import com.varitas.gokulpos.tablet.utilities.Utils


class MenusAdapter(private var onClickListener: (Menus) -> Unit) : RecyclerView.Adapter<MenusAdapter.MenusViewHolder>() {

    private var list = mutableListOf<Menus>()

    fun setList(menu: List<Menus>) {
        list.clear()
        list.addAll(menu)
        notifyItemRangeChanged(0, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenusViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterMenuBinding.inflate(inflater, parent, false)
        return MenusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenusViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MenusViewHolder(private val binding: AdapterMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Menus) {
            binding.apply {
                linearLayoutDetails.setOnClickListener {
                    binding.root.post {
                        onClickListener(data)
                    }
                }


                if (!TextUtils.isEmpty(data.base64Content)) {
                    try {
                        val options: RequestOptions = RequestOptions()
                            .placeholder(R.drawable.ic_profile)
                            .error(R.drawable.ic_profile)

                        val decodedBytes = Base64.decode(data.base64Content, Base64.DEFAULT)

                        // Resize the decoded Bitmap to a smaller size
                        val factory = BitmapFactory.Options()
                        factory.inSampleSize = 2 // Adjust the sample size as needed

                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size, factory)

                        Glide.with(itemView.context)
                            .load(bitmap)
                            .apply(options)
                            .into(imageViewMenu)
                    } catch (ex: Exception) {
                        Utils.printAndWriteException(ex)
                    }
                }
            }
            binding.data = data
        }
    }
}