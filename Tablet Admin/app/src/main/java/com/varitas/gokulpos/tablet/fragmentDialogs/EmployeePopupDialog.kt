package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.ContextCompat
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.EmployeesActivity
import com.varitas.gokulpos.tablet.databinding.FragmentDEmployeeBinding
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.Employee
import com.varitas.gokulpos.tablet.utilities.CustomSpinner
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmployeePopupDialog : BaseDialogFragment() {

    private var mActivity : Activity? = null
    private var binding : FragmentDEmployeeBinding? = null
    private var onButtonClickListener : OnButtonClickListener? = null
    private var employeeDetails : Employee? = null
    private lateinit var roleSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var roleList : ArrayList<CommonDropDown>
    private lateinit var role : CommonDropDown

    companion object {
        fun newInstance() : EmployeePopupDialog {
            val f = EmployeePopupDialog()
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
        binding = FragmentDEmployeeBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            if(arguments!!.getSerializable(Default.EMPLOYEE) != null) {
                employeeDetails = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arguments?.getSerializable(Default.EMPLOYEE, Employee::class.java)!!
                else arguments?.getSerializable(Default.EMPLOYEE)!! as Employee
            }
        }
        initDate()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun initDate() {
        roleList = ArrayList()
        roleSpinner = ArrayAdapter(EmployeesActivity.Instance, R.layout.spinner_items, roleList)
    }

    private fun loadData() {
        EmployeesActivity.Instance.viewModel.showProgress.observe(this) {
            EmployeesActivity.Instance.manageProgress(it)
        }
        binding!!.employee = employeeDetails
        EmployeesActivity.Instance.viewModel.fetchRoles().observe(this) { dropdown->
            CoroutineScope(Dispatchers.Main).launch {
                roleSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    role = CommonDropDown(label = resources.getString(R.string.lbl_SelectRole), value = 0)
                    add(role)
                    addAll(dropdown)
                    if(employeeDetails != null) {
                        val ind = roleList.indexOfFirst { brand-> brand.value == employeeDetails!!.roleId }
                        if(ind >= 0) {
                            binding!!.spinnerRoles.setSelection(ind)
                            role = CommonDropDown(roleList[ind].value, roleList[ind].label)
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }

        if(employeeDetails != null) {
            binding?.apply {
                textInputName.setText(if(employeeDetails!!.name!!.isNotEmpty()) employeeDetails!!.name else "")
                textInputEmailId.setText(if(employeeDetails!!.email!!.isNotEmpty()) employeeDetails!!.email else "")
                textInputMobileNo.setText(if(employeeDetails!!.mobileNo!!.isNotEmpty()) employeeDetails!!.mobileNo else "")
                textInputAddress.setText(if(employeeDetails!!.address1!!.isNotEmpty()) employeeDetails!!.address1 else "")
                textInputCity.setText(if(employeeDetails!!.city!!.isNotEmpty()) employeeDetails!!.city else "")
                textInputZip.setText(if(employeeDetails!!.zip!!.isNotEmpty()) employeeDetails!!.zip else "")
                textInputState.setText(if(employeeDetails!!.state!!.isNotEmpty()) employeeDetails!!.state else "")
                textInputCountry.setText(if(employeeDetails!!.country!!.isNotEmpty()) employeeDetails!!.country else "")
                textInputUserName.setText(if(employeeDetails!!.userName!!.isNotEmpty()) employeeDetails!!.userName else "")
            }
        }
    }

    private fun postInitViews() {
        binding!!.layoutToolbar.textViewTitle.text = resources.getString(R.string.Employee, if(employeeDetails == null) resources.getString(R.string.lbl_Add) else resources.getString(R.string.lbl_Edit))
    }

    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }
            textInputPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {
                }

                override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                }

                override fun afterTextChanged(s : Editable?) {
                    textInputConfirmPassword.isEnabled = s!!.isNotEmpty()
                }
            })

            textInputConfirmPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {
                }

                override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                }

                override fun afterTextChanged(s : Editable?) {
                    textInputConfirmPassword.setTextColor(ContextCompat.getColor(EmployeesActivity.Instance, if(textInputPassword.text.toString() == s.toString()) R.color.darkGrey else R.color.red))
                }
            })

            textInputPin.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {
                }

                override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                }

                override fun afterTextChanged(s : Editable?) {
                    textInputConfirmPin.isEnabled = s!!.isNotEmpty()
                }
            })

            textInputConfirmPin.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {
                }

                override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                }

                override fun afterTextChanged(s : Editable?) {
                    textInputConfirmPin.setTextColor(ContextCompat.getColor(EmployeesActivity.Instance, if(textInputPin.text.toString() == s.toString()) R.color.darkGrey else R.color.red))
                }
            })

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

            buttonSave.clickWithDebounce {
                binding?.apply {
                    val name = textInputName.text.toString().trim()
                    val userName = textInputUserName.text.toString().trim()
                    val password = textInputConfirmPassword.text.toString().trim()
                    val email = textInputEmailId.text.toString().trim()
                    val mobileNo = textInputMobileNo.text.toString().trim()
                    val address = textInputAddress.text.toString().trim()
                    val city = textInputCity.text.toString().trim()
                    val zip = textInputZip.text.toString().trim()
                    val state = textInputState.text.toString().trim()
                    val country = textInputCountry.text.toString().trim()
                    val pin = textInputConfirmPin.text.toString().trim()
                    val cardNo = textInputCardNumber.text.toString().trim()

                    when {
                        email.isEmpty() && mobileNo.isEmpty() ->
                            manageEmployee(name, password, userName, address, city, state, country, zip, mobileNo, email, pin, cardNo)

                        email.isNotEmpty() && mobileNo.isEmpty() -> {
                            if(Patterns.EMAIL_ADDRESS.matcher(email).matches())
                                manageEmployee(name, password, userName, address, city, state, country, zip, mobileNo, email, pin, cardNo)
                            else EmployeesActivity.Instance.showSweetDialog(EmployeesActivity.Instance, resources.getString(R.string.lbl_EmailFormat))
                        }

                        email.isEmpty() && mobileNo.isNotEmpty() -> {
                            if(mobileNo.length == 10)
                                manageEmployee(name, password, userName, address, city, state, country, zip, mobileNo, email, pin, cardNo)
                            else EmployeesActivity.Instance.showSweetDialog(EmployeesActivity.Instance, resources.getString(R.string.lbl_PhoneNoFormat))
                        }

                        email.isNotEmpty() && mobileNo.isNotEmpty() -> {
                            if(Patterns.EMAIL_ADDRESS.matcher(email).matches() && mobileNo.length == 10)
                                manageEmployee(name, password, userName, address, city, state, country, zip, mobileNo, email, pin, cardNo)
                            else if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) EmployeesActivity.Instance.showSweetDialog(EmployeesActivity.Instance, resources.getString(R.string.lbl_PhoneNoFormat))
                            else if(mobileNo.length == 10) EmployeesActivity.Instance.showSweetDialog(EmployeesActivity.Instance, resources.getString(R.string.lbl_EmailFormat))
                            else EmployeesActivity.Instance.showSweetDialog(EmployeesActivity.Instance, resources.getString(R.string.lbl_PhoneNoFormat) + " or " + resources.getString(R.string.lbl_EmailFormat))
                        }
                    }
                }
            }
        }
    }

    private fun manageEmployee(name : String, password : String, userName : String, address : String, city : String, state : String, country : String, zip : String, mobileNo : String, email : String, pin : String, cardNo : String) {
        if(name.isNotEmpty()) {
            if(binding!!.textInputPassword.text.toString() == password) {
                if(employeeDetails == null) {
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
                        EmployeesActivity.Instance.viewModel.insertEmployee(req = req).observe(EmployeesActivity.Instance) {
                            if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)
                        }
                    } else EmployeesActivity.Instance.showSweetDialog(EmployeesActivity.Instance, resources.getString(R.string.lbl_MissingEmployee))
                } else {
                    val req = Employee(
                        id = employeeDetails!!.id,
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
                    EmployeesActivity.Instance.viewModel.updateEmployee(req = req).observe(EmployeesActivity.Instance) {
                        if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.UPDATE, Employee(name = name, mobileNo = mobileNo, roleName = role.label))
                    }
                }
            } else EmployeesActivity.Instance.showSweetDialog(EmployeesActivity.Instance, resources.getString(R.string.lbl_PasswordValidation))
        } else if(employeeDetails == null) EmployeesActivity.Instance.showSweetDialog(EmployeesActivity.Instance, resources.getString(R.string.lbl_MissingEmployee)) else EmployeesActivity.Instance.showSweetDialog(EmployeesActivity.Instance, resources.getString(R.string.lbl_NameMissing))
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents, employee : Employee? = null)
    }

}