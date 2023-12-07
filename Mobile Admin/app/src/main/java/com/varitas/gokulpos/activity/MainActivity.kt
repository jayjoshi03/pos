package com.varitas.gokulpos.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.varitas.gokulpos.adapter.MainAdapter
import com.varitas.gokulpos.databinding.ActivityMainBinding
import com.varitas.gokulpos.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var movieAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        movieAdapter = MainAdapter()

        binding.recyclerview.apply {
            adapter = movieAdapter
        }

        viewModel.movieList.observe(this) {
            movieAdapter.setMovieList(it)
        }
        viewModel.setMovies()

        /*viewModel.fullName.observe(this, Observer { name ->
            binding.fullName.text = name
        })

        binding.search.setOnClickListener {
            viewModel.searchUser(binding.username.text.toString())
        }*/
    }
}