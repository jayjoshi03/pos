package com.varitas.gokulpos.tablet.repositories

import com.varitas.gokulpos.tablet.request.LoginRequest
import com.varitas.gokulpos.tablet.response.PostResponse


interface LoginRepository {

    suspend fun makeLogin(req: LoginRequest, onSuccess: (response: PostResponse) -> Unit, onFailure: (t: Throwable) -> Unit)

    suspend fun makeLogout(onSuccess: (response: PostResponse) -> Unit, onFailure: (t: Throwable) -> Unit)
}