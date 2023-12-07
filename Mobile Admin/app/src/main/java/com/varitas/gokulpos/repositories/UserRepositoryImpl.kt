package com.varitas.gokulpos.repositories

import com.varitas.gokulpos.api.ApiClient
import com.varitas.gokulpos.model.Movie
import com.varitas.gokulpos.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepositoryImpl(private val api: ApiClient) : UserRepository {

    override fun getUser(username: String, onSuccess: (user: User) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getUser(username).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                response.body()?.let { user ->
                    onSuccess.invoke(user)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                onFailure.invoke(t)
            }
        })
    }

    override fun getAllMovies(onSuccess: (movies: List<Movie>) -> Unit, onFailure: (t: Throwable) -> Unit) {
        api.getAllMovies().enqueue(object : Callback<List<Movie>> {
            override fun onResponse(call: Call<List<Movie>>, response: Response<List<Movie>>) {
                response.body()?.let { user ->
                    onSuccess.invoke(user)
                }
            }

            override fun onFailure(call: Call<List<Movie>>, t: Throwable) {
                onFailure.invoke(t)
            }
        })
    }
}