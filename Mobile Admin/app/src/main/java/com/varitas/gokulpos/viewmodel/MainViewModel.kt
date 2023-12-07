package com.varitas.gokulpos.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.varitas.gokulpos.model.Movie
import com.varitas.gokulpos.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel class MainViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    val showProgress: MutableLiveData<Boolean> = MutableLiveData()

    private val _fullName = MutableLiveData<String>()
    val fullName: LiveData<String>
        get() = _fullName

    fun searchUser(username: String) {
        userRepository.getUser(username, { user -> _fullName.value = user.name }, { t -> Log.e("MainActivity", "onFailure: ", t) })
    }

    private val _movieList = MutableLiveData<List<Movie>>()

    val movieList: LiveData<List<Movie>>
        get() = _movieList

    fun setMovies() {
        userRepository.getAllMovies({ user -> _movieList.value = user }, { t -> Log.e("MainActivity", "onFailure: ", t) })
    }
}