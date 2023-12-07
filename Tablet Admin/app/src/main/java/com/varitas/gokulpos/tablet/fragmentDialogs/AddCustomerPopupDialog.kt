package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.CustomersActivity
import com.varitas.gokulpos.tablet.databinding.FragmentDAddcustomerBinding
import com.varitas.gokulpos.tablet.response.Customers
import com.varitas.gokulpos.tablet.utilities.Constants
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.moallemi.tools.extension.date.toYear
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class AddCustomerPopupDialog : BaseDialogFragment() {

    private var mActivity : Activity? = null
    private var binding : FragmentDAddcustomerBinding? = null
    private var date : String = ""
    private var onButtonClickListener : OnButtonClickListener? = null
    private lateinit var materialDateBuilder : MaterialDatePicker.Builder<Long>
    private lateinit var materialDatePicker : MaterialDatePicker<Long>
    private lateinit var startCalendar : Calendar
    private var customerDetail : Customers? = null

    companion object {
        fun newInstance() : AddCustomerPopupDialog {
            val f = AddCustomerPopupDialog()
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onAttach(activity : Activity) {
        super.onAttach(activity)
        if(context is Activity) mActivity = activity
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(resources.getInteger(R.integer.fragmentWidth), WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?,
    ) : View {
        binding = FragmentDAddcustomerBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            if(arguments!!.getSerializable(Default.CUSTOMER) != null) {
                customerDetail = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arguments?.getSerializable(Default.CUSTOMER, Customers::class.java)!!
                else arguments?.getSerializable(Default.CUSTOMER)!! as Customers
            }
        }
        initDate()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun initDate() {
        materialDateBuilder = MaterialDatePicker.Builder.datePicker().setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        materialDatePicker = materialDateBuilder.build()
        startCalendar = Calendar.getInstance()
        startCalendar.time = 21.toYear().ago
    }

    private fun loadData() {
        CustomersActivity.Instance.viewModel.showProgress.observe(this) {
            CustomersActivity.Instance.manageProgress(it)
        }
        if(customerDetail != null) {
            binding?.apply {
                textInputName.setText(customerDetail!!.name)
                textInputCompanyName.setText(customerDetail!!.companyName)
                textInputEmailId.setText(customerDetail!!.emailId)
                textInputMobileNo.setText(customerDetail!!.phonenNo)
                textInputBirthDate.setText(Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormatT, parseDate = customerDetail!!.birthDate)))
                textInputLicense.setText(customerDetail!!.drivingLicense)
                textInputAddress.setText(customerDetail!!.address)
                textInputCity.setText(customerDetail!!.city)
                textInputState.setText(customerDetail!!.state)
                textInputZip.setText(customerDetail!!.zip)
                textInputCountry.setText(customerDetail!!.country)
                date = customerDetail!!.birthDate!!
            }
        }
    }

    private fun postInitViews() {
        binding!!.layoutToolbar.textViewTitle.text = resources.getString(R.string.Customer, if(customerDetail == null) resources.getString(R.string.lbl_Add) else resources.getString(R.string.lbl_Edit))
        val selectDate = Constants.dateFormat_MMM_dd_yyyy.format(startCalendar.time)
        date = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = selectDate))
        binding!!.textInputBirthDate.setText(Constants.dateFormat_MMM_dd_yyyy.format(Utils.convertStringToDate(formatter = Constants.dateFormatTZ, parseDate = date)))
    }

    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }
            materialDatePicker.addOnPositiveButtonClickListener {
                val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                utc.timeInMillis = it
                val format = SimpleDateFormat("MMM dd, yyyy")
                val formatted : String = format.format(utc.time)
                textInputBirthDate.setText(formatted)
                date = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormat_MMM_dd_yyyy, parseDate = formatted))
            }

            textInputBirthDate.clickWithDebounce { //setStartEndDate(startCalendar, binding.textViewBirthDate)
                materialDatePicker.show(CustomersActivity.Instance.supportFragmentManager, "MATERIAL_DATE_PICKER")
            }

            buttonSave.clickWithDebounce {
                val name = textInputName.text.toString().trim()
                val companyName = textInputCompanyName.text.toString().trim()
                val email = textInputEmailId.text.toString().trim()
                val mobileNumber = textInputMobileNo.text.toString().trim()
                val drivingLicense = textInputLicense.text.toString().trim()
                val address = textInputAddress.text.toString().trim()
                val city = textInputCity.text.toString().trim()
                val state = textInputState.text.toString().trim()
                val zip = textInputZip.text.toString().trim()
                val country = textInputCountry.text.toString().trim()
                when {
                    email.isEmpty() && mobileNumber.isEmpty() ->
                        manageCustomer(name, companyName, email, drivingLicense, address, city, state, zip, mobileNumber, country)

                    email.isNotEmpty() && mobileNumber.isEmpty() -> {
                        if(Patterns.EMAIL_ADDRESS.matcher(email).matches())
                            manageCustomer(name, companyName, email, drivingLicense, address, city, state, zip, mobileNumber, country)
                        else CustomersActivity.Instance.showSweetDialog(CustomersActivity.Instance, resources.getString(R.string.lbl_EmailFormat))
                    }

                    email.isEmpty() && mobileNumber.isNotEmpty() -> {
                        if(mobileNumber.length == 10)
                            manageCustomer(name, companyName, email, drivingLicense, address, city, state, zip, mobileNumber, country)
                        else CustomersActivity.Instance.showSweetDialog(CustomersActivity.Instance, resources.getString(R.string.lbl_PhoneNoFormat))
                    }

                    email.isNotEmpty() && mobileNumber.isNotEmpty() -> {
                        if(Patterns.EMAIL_ADDRESS.matcher(email).matches() && mobileNumber.length == 10)
                            manageCustomer(name, companyName, email, drivingLicense, address, city, state, zip, mobileNumber, country)
                        else if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) CustomersActivity.Instance.showSweetDialog(CustomersActivity.Instance, resources.getString(R.string.lbl_PhoneNoFormat))
                        else if(mobileNumber.length == 10) CustomersActivity.Instance.showSweetDialog(CustomersActivity.Instance, resources.getString(R.string.lbl_EmailFormat))
                        else CustomersActivity.Instance.showSweetDialog(CustomersActivity.Instance, resources.getString(R.string.lbl_PhoneNoFormat) + " or " + resources.getString(R.string.lbl_EmailFormat))
                    }
                }

            }
        }
    }

    private fun manageCustomer(name : String, companyName : String, email : String, drivingLicense : String, address : String, city : String, state : String, zip : String, mobileNumber : String, country : String) {
        if(name.isNotEmpty()) {
            if(customerDetail == null) {
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
                    phonenNo = mobileNumber,
                    country = country,
                    birthDate = date
                )
                CustomersActivity.Instance.viewModel.insertCustomer(req).observe(CustomersActivity.Instance) {
                    CoroutineScope(Dispatchers.Main).launch {
                        if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)
                    }
                }
            } else {
                val req = Customers(
                    id = customerDetail!!.id,
                    name = name,
                    companyName = companyName,
                    emailId = email,
                    drivingLicense = drivingLicense,
                    address = address,
                    city = city,
                    state = state,
                    zip = zip,
                    groupId = 16,
                    phonenNo = mobileNumber,
                    country = country,
                    birthDate = Constants.dateFormatTZ.format(Utils.convertStringToDate(formatter = Constants.dateFormatT, parseDate = date))
                )
                CustomersActivity.Instance.viewModel.updateCustomer(req).observe(CustomersActivity.Instance) {
                    CoroutineScope(Dispatchers.Main).launch {
                        if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.UPDATE, Customers(name = name, phonenNo = mobileNumber, drivingLicense = drivingLicense))
                    }
                }
            }
        } else CustomersActivity.Instance.showSweetDialog(CustomersActivity.Instance, resources.getString(R.string.lbl_NameMissing))
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents, customer : Customers? = null)
    }

}