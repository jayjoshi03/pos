package com.varitas.gokulpos.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.text.TextUtils
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.ActivityLoginBinding
import com.varitas.gokulpos.request.LoginRequest
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.PreferenceData
import com.varitas.gokulpos.utilities.SharedPreferencesKeys
import com.varitas.gokulpos.utilities.Utils
import com.varitas.gokulpos.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess


@AndroidEntryPoint class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var fcmToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        getFirebaseToken()
        manageClicks()
        loadData()
        checkNotificationPermission()
    }

    private fun loadData() {
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }
    }

    //region To manage click events
    private fun manageClicks() {
        binding.buttonSignIn.clickWithDebounce {
            val userName = binding.editTextUserName.text.toString()
            val password = binding.editTextPassword.text.toString()
            if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) makeLogin(userName, password)
                        else askNotificationPermission()
                    } else makeLogin(userName, password)
                } else makeLogin(userName, password)

            } else return@clickWithDebounce
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
        finishAffinity()
        exitProcess(0)
    }

    //region To setup Firebase token
    private fun getFirebaseToken() {
        fcmToken = ""
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) return@OnCompleteListener // Get new FCM registration token
                fcmToken = task.result
                Log.e("FCM Token:", fcmToken)
            })
        } catch (ex: Exception) {
            Utils.printAndWriteException(ex)
            fcmToken = ""
        }
    } //endregion

    //region To ask for a permission for using notifications above Android OS 13
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= 33) {
                when {
                    shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> askNotificationPermission()
                    else -> { // The registered ActivityResultCallback gets the result of this request
                        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) { // Permission is granted. Continue the action or workflow in your
            // app.
        } else { // Explain to the user that the feature is unavailable because the
            // features requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
            askNotificationPermission()
        }
    }

    private fun askNotificationPermission() {
        try {
            val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
            sweetAlertDialog.setCanceledOnTouchOutside(false)
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.contentText = resources.getString(R.string.lbl_PermissionRequired)
            sweetAlertDialog.confirmText = resources.getString(R.string.lbl_Okay)
            sweetAlertDialog.confirmButtonBackgroundColor = ContextCompat.getColor(this, R.color.base_color)
            sweetAlertDialog.setConfirmClickListener { sDialog ->
                sDialog.dismissWithAnimation()
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            sweetAlertDialog.show()
        } catch (e: Exception) {
            Utils.printAndWriteException(e)
        }
    } //endregion

    //region To perform login
    private fun makeLogin(userName: String, password: String) {
        val req = LoginRequest(userName, password, fcmToken)
        viewModel.fetchLogin(req).observe(this) {
            if (it != null) {
                if (it.singleResult != null) {
                    if (it.status == Default.SUCCESS_API && !TextUtils.isEmpty(it.singleResult!!.accessToken)) {
                        PreferenceData.putPrefString(key = SharedPreferencesKeys.LOGIN_RESPONSE, value = Gson().toJson(it))
                        PreferenceData.putPrefBoolean(key = SharedPreferencesKeys.IS_LOGIN, value = true)
                        PreferenceData.putPrefString(key = SharedPreferencesKeys.CURRENCY, value = "$")
                        openActivity(Intent(this, DashboardActivity::class.java))
                    } else showSweetDialog(this, resources.getString(R.string.lbl_InvalidLogin))
                } else showSweetDialog(this, resources.getString(R.string.lbl_InvalidLogin))
            } else showSweetDialog(this, resources.getString(R.string.lbl_InvalidLogin))
        }
    } //endregion
}