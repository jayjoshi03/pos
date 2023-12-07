package com.varitas.gokulpos.tablet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.varitas.gokulpos.tablet.databinding.AdapterMovieBinding
import com.varitas.gokulpos.tablet.model.Movie

class MainAdapter : RecyclerView.Adapter<MainViewHolder>() {
    private var movies = mutableListOf<Movie>()
    fun setMovieList(movies: List<Movie>) {
        this.movies = movies.toMutableList()
        notifyItemRangeChanged(0, this.movies.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterMovieBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int {
        return movies.size
    }
}

class MainViewHolder(private val binding: AdapterMovieBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(movie: Movie) {
        binding.name.text = movie.name
        Glide.with(itemView.context).load(movie.imageUrl).into(binding.imageview)
    }
}