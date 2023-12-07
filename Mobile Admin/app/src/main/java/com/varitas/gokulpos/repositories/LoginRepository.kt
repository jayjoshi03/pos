package com.varitas.gokulpos.repositories

import com.varitas.gokulpos.request.LoginRequest
import com.varitas.gokulpos.response.PostResponse


interface LoginRepository {

    suspend fun makeLogin(req: LoginRequest, onSuccess: (response: PostResponse) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun makeLogout(onSuccess: (response: PostResponse) -> Unit, onFailure: (t: Throwable) -> Unit)
}