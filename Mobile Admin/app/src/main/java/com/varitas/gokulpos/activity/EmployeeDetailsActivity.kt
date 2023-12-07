package com.varitas.gokulpos.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.ActivityEmployeeBinding
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.response.Employee
import com.varitas.gokulpos.utilities.CustomSpinner
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.viewmodel.EmployeeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class EmployeeDetailsActivity : BaseActivity() {

    private lateinit var binding : ActivityEmployeeBinding
    private var employeeId = 0
    private val viewModel : EmployeeViewModel by viewModels()
    private lateinit var roleSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var roleList : ArrayList<CommonDropDown>
    private lateinit var role : CommonDropDown
    private var userName = ""

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.layoutToolbar.toolbarCommon)
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun initData() {
        employeeId = intent.extras?.getInt(Default.EMPLOYEE_ID)!!
        roleList = ArrayList()
        roleSpinner = ArrayAdapter(this, R.layout.spinner_items, roleList)
    }

    private fun postInitViews() {
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_EmployeeList)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@EmployeeDetailsActivity, R.drawable.ic_save))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
            id = employeeId
        }
    }

    //region To load data
    private fun loadData() {
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }

        viewModel.errorMsg.observe(this) {
            showSweetDialog(this, it)
        }

        viewModel.fetchRoles().observe(this@EmployeeDetailsActivity) { dropdown->
            CoroutineScope(Dispatchers.Main).launch {
                roleSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    role = CommonDropDown(label = resources.getString(R.string.lbl_SelectRole), value = 0)
                    add(role)
                    addAll(dropdown)
                    notifyDataSetChanged()
                }
                if(employeeId > 0) {
                    viewModel.fetchEmployeeDetails(employeeId).observe(this@EmployeeDetailsActivity) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if(it != null) {
                                binding.apply {
                                    userName = it.name!!
                                    editTextName.setText(if(it.name!!.isNotEmpty()) it.name else "")
                                    editTextEmail.setText(if(it.email!!.isNotEmpty()) it.email else "")
                                    editTextMobileNum.setText(if(it.mobileNo!!.isNotEmpty()) it.mobileNo else "")
                                    editTextAddress.setText(if(it.address1!!.isNotEmpty()) it.address1 else "")
                                    editTextCity.setText(if(it.city!!.isNotEmpty()) it.city else "")
                                    editTextZip.setText(if(it.zip!!.isNotEmpty()) it.zip else "")
                                    editTextState.setText(if(it.state!!.isNotEmpty()) it.state else "")
                                    editTextCountry.setText(if(it.country!!.isNotEmpty()) it.country else "")
                                    editTextUserName.setText(if(it.userName!!.isNotEmpty()) it.userName else "")
                                    if(roleList.size > 0) {
                                        val ind = roleList.indexOfFirst { role-> role.value == it.roleId }
                                        if(ind >= 0) {
                                            binding.spinnerRoles.setSelection(ind)
                                            role = roleList[ind]
                                        }
                                        roleSpinner.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewBack.clickWithDebounce {
                openActivity(Intent(this@EmployeeDetailsActivity, EmployeesActivity::class.java))
            }

            editTextPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {
                }

                override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                }

                override fun afterTextChanged(s : Editable?) {
                    editTextConfirmPassword.isEnabled = s!!.isNotEmpty()
                }
            })

            editTextConfirmPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {
                }

                override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                }

                override fun afterTextChanged(s : Editable?) {
                    editTextConfirmPassword.setTextColor(ContextCompat.getColor(this@EmployeeDetailsActivity, if(editTextPassword.text.toString() == s.toString()) R.color.darkGrey else R.color.red))
                }
            })

            editTextPin.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {
                }

                override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                }

                override fun afterTextChanged(s : Editable?) {
                    editTextConfirmPin.isEnabled = s!!.isNotEmpty()
                }
            })

            editTextConfirmPin.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {
                }

                override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                }

                override fun afterTextChanged(s : Editable?) {
                    editTextConfirmPin.setTextColor(ContextCompat.getColor(this@EmployeeDetailsActivity, if(editTextPin.text.toString() == s.toString()) R.color.darkGrey else R.color.red))
                }
            })


            layoutToolbar.imageViewAction.clickWithDebounce {
                binding.apply {
                    val name = editTextName.text.toString().trim()
                    val userName = editTextUserName.text.toString().trim()
                    val password = editTextConfirmPassword.text.toString().trim()
                    val email = editTextEmail.text.toString().trim()
                    val mobileNo = editTextMobileNum.text.toString().trim()
                    val address = editTextAddress.text.toString().trim()
                    val city = editTextCity.text.toString().trim()
                    val zip = editTextZip.text.toString().trim()
                    val state = editTextState.text.toString().trim()
                    val country = editTextCountry.text.toString().trim()
                    val pin = editTextConfirmPin.text.toString().trim()
                    val cardNo = editTextCardNumber.text.toString().trim()

                    when {
                        email.isEmpty() && mobileNo.isEmpty() ->
                            manageEmployee(name, password, userName, address, city, state, country, zip, mobileNo, email, pin, cardNo)

                        email.isNotEmpty() && mobileNo.isEmpty() -> {
                            if(Patterns.EMAIL_ADDRESS.matcher(email).matches())
                                manageEmployee(name, password, userName, address, city, state, country, zip, mobileNo, email, pin, cardNo)
                            else showSweetDialog(this@EmployeeDetailsActivity, resources.getString(R.string.lbl_EmailFormat))
                        }

                        email.isEmpty() && mobileNo.isNotEmpty() -> {
                            if(mobileNo.length == 10)
                                manageEmployee(name, password, userName, address, city, state, country, zip, mobileNo, email, pin, cardNo)
                            else showSweetDialog(this@EmployeeDetailsActivity, resources.getString(R.string.lbl_PhoneNoFormat))
                        }

                        email.isNotEmpty() && mobileNo.isNotEmpty() -> {
                            if(Patterns.EMAIL_ADDRESS.matcher(email).matches() && mobileNo.length == 10)
                                manageEmployee(name, password, userName, address, city, state, country, zip, mobileNo, email, pin, cardNo)
                            else if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) showSweetDialog(this@EmployeeDetailsActivity, resources.getString(R.string.lbl_PhoneNoFormat))
                            else if(mobileNo.length == 10) showSweetDialog(this@EmployeeDetailsActivity, resources.getString(R.string.lbl_EmailFormat))
                            else showSweetDialog(this@EmployeeDetailsActivity, resources.getString(R.string.lbl_PhoneNoFormat) + " or " + resources.getString(R.string.lbl_EmailFormat))
                        }
                    }
                }
            }

            spinnerRoles.apply {
                adapter = roleSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        role = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            buttonOrder.clickWithDebounce {
                val intent = Intent(this@EmployeeDetailsActivity, OrderListActivity::class.java)
                intent.putExtra(Default.USER, Enums.Menus.EMPLOYEES)
                intent.putExtra(Default.ID, if(employeeId > 0) employeeId else 0)
                intent.putExtra(Default.NAME, userName)
                openActivity(intent)
            }
        }
    } //endregion

    private fun ActivityEmployeeBinding.manageEmployee(name : String, password : String, userName : String, address : String, city : String, state : String, country : String, zip : String, mobileNo : String, email : String, pin : String, cardNo : String) {
        if(name.isNotEmpty()) {
            if(editTextPassword.text.toString() == password) {
                if(employeeId == 0) {
                    if(password.isNotEmpty() && userName.isNotEmpty() && role.value!! > 0) {
                        val req = Employee(
                            name = name,
                            address1 = address,
                            city = city,
                            state = state,
                            country = country,
                            zip = zip,
                            mobileNo = mobileNo,
                            email = email,
                            userName = userName,
                            password = password,
                            pin = pin,
                            cardNo = cardNo,
                            roleId = role.value,
                        )
                        viewModel.insertEmployee(req = req).observe(this@EmployeeDetailsActivity) {
                            if(it) openActivity(Intent(this@EmployeeDetailsActivity, EmployeesActivity::class.java))
                        }
                    } else showSweetDialog(this@EmployeeDetailsActivity, resources.getString(R.string.lbl_MissingEmployee))
                } else {
                    val req = Employee(
                        id = employeeId,
                        name = name,
                        address1 = address,
                        city = city,
                        state = state,
                        country = country,
                        zip = zip,
                        mobileNo = mobileNo,
                        email = email,
                        roleId = role.value,
                    )
                    viewModel.updateEmployee(req = req).observe(this@EmployeeDetailsActivity) {
                        if(it) openActivity(Intent(this@EmployeeDetailsActivity, EmployeesActivity::class.java))
                    }
                }
            } else showSweetDialog(this@EmployeeDetailsActivity, resources.getString(R.string.lbl_PasswordValidation))
        } else if(employeeId == 0) showSweetDialog(this@EmployeeDetailsActivity, resources.getString(R.string.lbl_MissingEmployee)) else showSweetDialog(this@EmployeeDetailsActivity, resources.getString(R.string.lbl_NameMissing))
    }

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        openActivity(Intent(this, EmployeesActivity::class.java))
        onBackPressedDispatcher.onBackPressed()
    }
}