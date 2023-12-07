package com.varitas.gokulpos.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.varitas.gokulpos.R
import com.varitas.gokulpos.databinding.ActivityCustomerBinding
import com.varitas.gokulpos.response.Customers
import com.varitas.gokulpos.utilities.Constants
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.utilities.Utils
import com.varitas.gokulpos.viewmodel.CustomerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.moallemi.tools.extension.date.toYear
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone


@AndroidEntryPoint class CustomerDetailsActivity : BaseActivity() {
    private lateinit var binding : ActivityCustomerBinding
    private lateinit var startCalendar : Calendar
    private val viewModel : CustomerViewModel by viewModels()
    private var date : String = ""
    private var customerID = 0
    private lateinit var materialDateBuilder : MaterialDatePicker.Builder<Long>
    private lateinit var materialDatePicker : MaterialDatePicker<Long>
    private var userName = ""

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.layoutToolbar.toolbarCommon)
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun initData() {
        customerID = intent.extras?.getInt(Default.CUSTOMER_ID)!!
        materialDateBuilder = MaterialDatePicker.Builder.datePicker().setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        materialDatePicker = materialDateBuilder.build()
        startCalendar = Calendar.getInstance()
        startCalendar.time = 21.toYear().ago
    }

    private fun postInitViews() {
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_CustomerList)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@CustomerDetailsActivity, R.drawable.ic_save))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
            id = customerID
            val selectDate = Constants.dateFormat_MMM_dd_yyyy.format(startCalendar.time)
            date = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = selectDate))
            editTextBirthDate.setText(Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormatTZ, parseDate = date)))
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

        if(customerID > 0) {
            viewModel.getCustomerDetail(customerID).observe(this) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        if(it != null) {
                            binding.apply {
                                userName = it.name!!
                                editTextName.setText(it.name)
                                editTextCompanyName.setText(it.companyName)
                                editTextEmail.setText(it.emailId)
                                editTextMobileNum.setText(it.mobileNo)
                                editTextBirthDate.setText(Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormat_yyyy_MM_dd, parseDate = it.birthDate)))
                                editTextDrivingLicense.setText(it.drivingLicense)
                                editTextAddress.setText(it.address)
                                editTextCity.setText(it.city)
                                editTextState.setText(it.state)
                                editTextZip.setText(it.zip)
                                editTextCountry.setText(it.country)
                                date = it.birthDate!!
                            }
                        }
                    } catch(e : Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    } //endregion


    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewBack.clickWithDebounce {
                openActivity(Intent(this@CustomerDetailsActivity, CustomersActivity::class.java))
            }
            layoutToolbar.imageViewAction.clickWithDebounce {
                val name = binding.editTextName.text.toString().trim()
                val companyName = binding.editTextCompanyName.text.toString().trim()
                val email = binding.editTextEmail.text.toString().trim()
                val mobileNumber = binding.editTextMobileNum.text.toString().trim()
                val drivingLicense = binding.editTextDrivingLicense.text.toString().trim()
                val address = binding.editTextAddress.text.toString().trim()
                val city = binding.editTextCity.text.toString().trim()
                val state = binding.editTextState.text.toString().trim()
                val zip = binding.editTextZip.text.toString().trim()
                val country = binding.editTextCountry.text.toString().trim()

                when {
                    email.isEmpty() && mobileNumber.isEmpty() ->
                        manageCustomer(name, companyName, email, drivingLicense, address, city, state, zip, mobileNumber, country)

                    email.isNotEmpty() && mobileNumber.isEmpty() -> {
                        if(Patterns.EMAIL_ADDRESS.matcher(email).matches())
                            manageCustomer(name, companyName, email, drivingLicense, address, city, state, zip, mobileNumber, country)
                        else showSweetDialog(this@CustomerDetailsActivity, resources.getString(R.string.lbl_EmailFormat))
                    }

                    email.isEmpty() && mobileNumber.isNotEmpty() -> {
                        if(mobileNumber.length == 10)
                            manageCustomer(name, companyName, email, drivingLicense, address, city, state, zip, mobileNumber, country)
                        else showSweetDialog(this@CustomerDetailsActivity, resources.getString(R.string.lbl_PhoneNoFormat))
                    }

                    email.isNotEmpty() && mobileNumber.isNotEmpty() -> {
                        if(Patterns.EMAIL_ADDRESS.matcher(email).matches() && mobileNumber.length == 10)
                            manageCustomer(name, companyName, email, drivingLicense, address, city, state, zip, mobileNumber, country)
                        else if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) showSweetDialog(this@CustomerDetailsActivity, resources.getString(R.string.lbl_PhoneNoFormat))
                        else if(mobileNumber.length == 10) showSweetDialog(this@CustomerDetailsActivity, resources.getString(R.string.lbl_EmailFormat))
                        else showSweetDialog(this@CustomerDetailsActivity, resources.getString(R.string.lbl_PhoneNoFormat) + " or " + resources.getString(R.string.lbl_EmailFormat))
                    }
                }

            }
            materialDatePicker.addOnPositiveButtonClickListener {
                val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                utc.timeInMillis = it
                val format = SimpleDateFormat("MMM dd, yyyy")
                val formatted : String = format.format(utc.time)
                editTextBirthDate.setText(formatted)
                date = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = formatted))
            }

            editTextBirthDate.clickWithDebounce { //setStartEndDate(startCalendar, binding.textViewBirthDate)
                materialDatePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
            }

            buttonOrder.clickWithDebounce {
                val intent = Intent(this@CustomerDetailsActivity, OrderListActivity::class.java)
                intent.putExtra(Default.USER, Enums.Menus.CUSTOMERS)
                intent.putExtra(Default.ID, if(customerID > 0) customerID else 0)
                intent.putExtra(Default.NAME, userName)
                openActivity(intent)
            }
        }
    } //endregion

    private fun manageCustomer(name : String, companyName : String, email : String, drivingLicense : String, address : String, city : String, state : String, zip : String, mobileNumber : String, country : String) {
        if(name.isNotEmpty()) {
            if(customerID == 0) {
                val req = Customers(
                    name = name,
                    companyName = companyName,
                    emailId = email,
                    drivingLicense = drivingLicense,
                    address = address,
                    city = city,
                    state = state,
                    zip = zip,
                    groupId = 16,
                    mobileNo = mobileNumber,
                    country = country,
                    birthDate = date
                )
                viewModel.insertCustomer(req).observe(this@CustomerDetailsActivity) {
                    CoroutineScope(Dispatchers.Main).launch {
                        if(it) openActivity(Intent(this@CustomerDetailsActivity, CustomersActivity::class.java))
                    }
                }
            } else {
                val req = Customers(
                    id = customerID,
                    name = name,
                    companyName = companyName,
                    emailId = email,
                    drivingLicense = drivingLicense,
                    address = address,
                    city = city,
                    state = state,
                    zip = zip,
                    groupId = 16,
                    mobileNo = mobileNumber,
                    country = country,
                    birthDate = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormatT, parseDate = date))
                )
                viewModel.updateCustomer(req).observe(this@CustomerDetailsActivity) {
                    CoroutineScope(Dispatchers.Main).launch {
                        if(it) openActivity(Intent(this@CustomerDetailsActivity, CustomersActivity::class.java))
                    }
                }
            }
        } else showSweetDialog(this@CustomerDetailsActivity, resources.getString(R.string.lbl_NameMissing))
    }

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        openActivity(Intent(this, CustomersActivity::class.java))
        onBackPressedDispatcher.onBackPressed()
    }
}

