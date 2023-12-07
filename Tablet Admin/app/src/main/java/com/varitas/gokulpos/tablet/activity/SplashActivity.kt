package com.varitas.gokulpos.tablet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.varitas.gokulpos.tablet.databinding.ActivitySplashBinding
import com.varitas.gokulpos.tablet.utilities.PreferenceData
import com.varitas.gokulpos.tablet.utilities.SharedPreferencesKeys
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen") @AndroidEntryPoint class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        loadData()
    }

    //region To load data
    private fun loadData() {
        val isLogin = PreferenceData.getPrefBoolean(key = SharedPreferencesKeys.IS_LOGIN, defaultValue = false)
        Handler().postDelayed({
            openActivity(Intent(this, if (!isLogin) LoginActivity::class.java else DashboardActivity::class.java))
        }, 2000)
    } //endregion
}