package com.varitas.gokulpos.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.varitas.gokulpos.repositories.LoginRepository
import com.varitas.gokulpos.request.LoginRequest
import com.varitas.gokulpos.response.PostResponse
import com.varitas.gokulpos.utilities.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) : ViewModel() {

    val showProgress: MutableLiveData<Boolean> = MutableLiveData()

    //region To fetch login
    fun fetchLogin(req: LoginRequest): MutableLiveData<PostResponse?> {
        val response = MutableLiveData<PostResponse?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                loginRepository.makeLogin(req, { login ->
                    showProgress.postValue(false)
                    response.postValue(login)
                }, { t ->
                    Log.e("LoginActivity", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return response
    } //endregion

    //region To perform logout
    fun doLogout(): MutableLiveData<PostResponse?> {
        val response = MutableLiveData<PostResponse?>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showProgress.postValue(true)
                loginRepository.makeLogout( { logout ->
                    showProgress.postValue(false)
                    response.postValue(logout)
                }, { t ->
                    Log.e("DashboardActivity", "onFailure: ", t)
                    showProgress.postValue(false)
                })
            } catch (ex: Exception) {
                Utils.printAndWriteException(ex)
                showProgress.postValue(false)
            }
        }
        return response
    } //endregion
}