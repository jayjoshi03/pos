package com.varitas.gokulpos.activity

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.multidex.MultiDex
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Utils
import dagger.hilt.android.HiltAndroidApp

private lateinit var INSTANCE: Application

@HiltAndroidApp class MainApp : Application() {

    private var mContext: Context? = null

    fun getContext(): Context? {
        return mContext
    }

    fun setContext(mContext: Context?) {
        this.mContext = mContext
    }

    override fun onCreate() {
        super.onCreate()
        mainApp = this
        INSTANCE = this
        isAidl = true

        ConnectivityLiveData().observeForever {
            isConnected = it ?: false
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    companion object {
        var mainApp: MainApp? = null
        var isAidl = false
        var isConnected = false
    }
}

object AppContext : ContextWrapper(INSTANCE)

//region To check connectivity
class ConnectivityLiveData : LiveData<Boolean>() {

    val cm = AppContext.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onLost(network: Network) {
            postValue(false)
            INSTANCE.sendBroadcast(Intent(Default.WIFI).putExtra(Default.IS_CONNECTED, false))
            super.onLost(network)
        }

        override fun onAvailable(network: Network) {
            postValue(true)
            INSTANCE.sendBroadcast(Intent(Default.WIFI).putExtra(Default.IS_CONNECTED, Utils.isInternetAvailable(MainApp.mainApp!!)))
            super.onAvailable(network)
        }
    }

    override fun onActive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cm.registerDefaultNetworkCallback(networkCallback)
        } else {
            val networkRequest = NetworkRequest.Builder().build()
            cm.registerNetworkCallback(networkRequest, networkCallback)
        }
        super.onActive()
    }

    override fun onInactive() {
        cm.unregisterNetworkCallback(networkCallback)
        INSTANCE.sendBroadcast(Intent(Default.WIFI).putExtra(Default.IS_CONNECTED, false))
        super.onInactive()
    }
}
//endregion
