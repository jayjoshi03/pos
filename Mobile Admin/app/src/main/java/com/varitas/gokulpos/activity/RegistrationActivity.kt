package com.varitas.gokulpos.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.viewModels
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.ActivityRegisterBinding
import com.varitas.gokulpos.request.RegisterEmployee
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.viewmodel.EmployeeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint class RegistrationActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: EmployeeViewModel by viewModels()
    private var roleId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initData()
        postInitView()
        loadData()
        manageClicks()
    }

    //region To init data
    private fun initData() {
        roleId = 1
    }

    private fun postInitView() {
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            radioGrpRole.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radioSuperAdmin -> roleId = 1
                    R.id.radioAdmin -> roleId = 2
                    R.id.radioEmployee -> roleId = 4
                }
            }
            buttonSignUp.clickWithDebounce {
                if (!TextUtils.isEmpty(editTextName.text.toString()) && !TextUtils.isEmpty(editTextUserName.text.toString()) && !TextUtils.isEmpty(editTextMobileNum.text.toString()) && !TextUtils.isEmpty(editTextEmail.text.toString()) && !TextUtils.isEmpty(editTextAddress.text.toString()) && !TextUtils.isEmpty(editTextPassword.text.toString())) {
                    val req = RegisterEmployee(
                        id = 0,
                        status = Default.ACTIVE,
                        name = editTextName.text.toString(),
                        address = editTextAddress.text.toString(),
                        email = editTextEmail.text.toString(),
                        mobileNo = editTextMobileNum.text.toString(),
                        userName = editTextUserName.text.toString(),
                        password = editTextPassword.text.toString(),
                        roleId = roleId,
                    )
//                    viewModel.insertEmployee(req).observe(this@RegistrationActivity) {
//                        if (it) {
//                            openActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
//                            onBackPressedDispatcher.onBackPressed()
//                        } else showSweetDialog(this@RegistrationActivity, resources.getString(R.string.lbl_SomethingWrong))
//                    }
                }
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        openActivity(Intent(this, DashboardActivity::class.java))
        onBackPressedDispatcher.onBackPressed()
    }

    //region To load data
    private fun loadData() {
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }

    } //endregion
}