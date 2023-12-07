package com.varitas.gokulpos.repositories

import com.varitas.gokulpos.model.Movie
import com.varitas.gokulpos.model.User


interface UserRepository {

    fun getUser(username: String, onSuccess: (user: User) -> Unit, onFailure: (t: Throwable) -> Unit)

    fun getAllMovies(onSuccess: (movies: List<Movie>) -> Unit, onFailure: (t: Throwable) -> Unit)
}