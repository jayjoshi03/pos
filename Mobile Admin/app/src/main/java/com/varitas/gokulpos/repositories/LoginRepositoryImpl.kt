package com.varitas.gokulpos.repositories

import android.util.Log
import com.varitas.gokulpos.api.ApiClient
import com.varitas.gokulpos.request.LoginRequest
import com.varitas.gokulpos.response.PostResponse
import com.varitas.gokulpos.utilities.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepositoryImpl(private val api: ApiClient) : LoginRepository {

    override suspend fun makeLogin(req: LoginRequest, onSuccess: (response: PostResponse) -> Unit, onFailure: (t: Throwable) -> Unit) {
        Log.e("LOGIN REQUEST", req.toString())
        api.makeLogin(req).enqueue(object : Callback<PostResponse> {
            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                response.body()?.let { login ->
                    onSuccess.invoke(login)
                    Log.e("LOGIN SUCCESS", login.toString())
                }
            }

            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("LOGIN ERROR", t.toString())
            }
        })
    }

    override suspend fun makeLogout(onSuccess: (response: PostResponse) -> Unit, onFailure: (t: Throwable) -> Unit) {
        val storeDetails = Utils.fetchLoginResponse()
        val token = storeDetails.singleResult!!.accessToken
        api.makeLogout(token!!).enqueue(object : Callback<PostResponse> {
            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                response.body()?.let { login ->
                    onSuccess.invoke(login)
                    Log.e("LOGOUT SUCCESS", login.toString())
                }
            }

            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                onFailure.invoke(t)
                Log.e("LOGOUT ERROR", t.toString())
            }
        })
    }
}