package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.VendorActivity
import com.varitas.gokulpos.tablet.databinding.FragmentDVendorBinding
import com.varitas.gokulpos.tablet.request.VendorInsertRequest
import com.varitas.gokulpos.tablet.request.VendorUpdateRequest
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.Vendor
import com.varitas.gokulpos.tablet.utilities.CustomSpinner
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VendorPopupDialog : BaseDialogFragment() {

    private var mActivity : Activity? = null
    private var binding : FragmentDVendorBinding? = null
    private var onButtonClickListener : OnButtonClickListener? = null
    private var vendorDetail : Vendor? = null
    private lateinit var taxGroupList : ArrayList<CommonDropDown>
    private lateinit var taxGroupSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var taxGroup : CommonDropDown

    companion object {
        fun newInstance() : VendorPopupDialog {
            val f = VendorPopupDialog()
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
        binding = FragmentDVendorBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val vendorParcelable = arguments?.getParcelable<Vendor>(Default.VENDOR)
            if (vendorParcelable != null) vendorDetail = vendorParcelable
        }
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun loadData() {
        VendorActivity.Instance.viewModel.showProgress.observe(this) {
            VendorActivity.Instance.manageProgress(it)
        }

        VendorActivity.Instance.viewModel.fetchGroupsById(Default.TYPE_TAX).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                taxGroupSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    taxGroup = CommonDropDown(label = resources.getString(R.string.lbl_SelectTax), value = 0)
                    add(taxGroup)
                    addAll(it)
                    if(vendorDetail != null) {
                        val ind = taxGroupList.indexOfFirst { cate-> cate.value == vendorDetail!!.taxGroupId }
                        if(ind >= 0) {
                            binding!!.spinnerTaxGroup.setSelection(ind)
                            taxGroup = CommonDropDown(taxGroupList[ind].value, taxGroupList[ind].label)
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }

        if(vendorDetail != null) {
            binding!!.apply {
                textInputName.setText(if(!TextUtils.isEmpty(vendorDetail!!.name)) vendorDetail!!.name else "")
                textInputCode.setText(if(!TextUtils.isEmpty(vendorDetail!!.code)) vendorDetail!!.code else "")
                textInputCompanyName.setText(if(!TextUtils.isEmpty(vendorDetail!!.companyName)) vendorDetail!!.companyName else "")
                textInputPersonName.setText(if(!TextUtils.isEmpty(vendorDetail!!.personName)) vendorDetail!!.personName else "")
                textInputPhoneNum.setText(if(!TextUtils.isEmpty(vendorDetail!!.phoneNo)) vendorDetail!!.phoneNo else "")
                textInputEmail.setText(if(!TextUtils.isEmpty(vendorDetail!!.email)) vendorDetail!!.email else "")
            }
        }
    }

    private fun initData() {
        taxGroupList = ArrayList()
        taxGroupSpinner = ArrayAdapter(VendorActivity.Instance, R.layout.spinner_items, taxGroupList)
    }

    private fun postInitViews() {
        binding!!.layoutToolbar.textViewTitle.text = resources.getString(R.string.Vendor, if(vendorDetail == null) resources.getString(R.string.lbl_Add) else resources.getString(R.string.lbl_Edit))
    }

    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonSave.clickWithDebounce {
                binding!!.apply {
                    val vendorName = textInputName.text.toString().trim()
                    val code = textInputCode.text.toString().trim()
                    val companyName = textInputCompanyName.text.toString().trim()
                    val personName = textInputPersonName.text.toString().trim()
                    val mail = textInputEmail.text.toString().trim()
                    val number = textInputPhoneNum.text.toString().trim()


                    when {
                        mail.isEmpty() && number.isEmpty() ->
                            manageVendor(vendorName, code, companyName, personName, mail, number)

                        mail.isNotEmpty() && number.isEmpty() -> {
                            if(Patterns.EMAIL_ADDRESS.matcher(mail).matches())
                                manageVendor(vendorName, code, companyName, personName, mail, number)
                            else VendorActivity.Instance.showSweetDialog(VendorActivity.Instance, resources.getString(R.string.lbl_EmailFormat))
                        }

                        mail.isEmpty() && number.isNotEmpty() -> {
                            if(number.length == 10)
                                manageVendor(vendorName, code, companyName, personName, mail, number)
                            else VendorActivity.Instance.showSweetDialog(VendorActivity.Instance, resources.getString(R.string.lbl_PhoneNoFormat))
                        }

                        mail.isNotEmpty() && number.isNotEmpty() -> {
                            if(Patterns.EMAIL_ADDRESS.matcher(mail).matches() && number.length == 10)
                                manageVendor(vendorName, code, companyName, personName, mail, number)
                            else if(Patterns.EMAIL_ADDRESS.matcher(mail).matches()) VendorActivity.Instance.showSweetDialog(VendorActivity.Instance, resources.getString(R.string.lbl_PhoneNoFormat))
                            else if(number.length == 10) VendorActivity.Instance.showSweetDialog(VendorActivity.Instance, resources.getString(R.string.lbl_EmailFormat))
                            else VendorActivity.Instance.showSweetDialog(VendorActivity.Instance, resources.getString(R.string.lbl_PhoneNoFormat) + " or " + resources.getString(R.string.lbl_EmailFormat))
                        }
                    }
                }
            }

            spinnerTaxGroup.apply {
                adapter = taxGroupSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        taxGroup = spinner?.selectedItem as CommonDropDown
                    }
                })
            }
        }
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents, vendor : Vendor? = null, isUpdate : Boolean = false)
    }

    private fun manageVendor(vendorName : String, code : String, companyName : String, personName : String, mail : String, number : String) {
        if(!TextUtils.isEmpty(vendorName)) {
            if(vendorDetail == null) {
                val req = VendorInsertRequest(
                    code = code,
                    companyName = companyName,
                    email = mail,
                    groupId = 0,
                    name = vendorName,
                    personName = personName,
                    phoneNo = number,
                    city = "",
                    state = "",
                    address = "",
                    country = "",
                    payTerm = 0,
                    taxGroupId = taxGroup.value
                )
                VendorActivity.Instance.viewModel.insertVendor(req).observe(VendorActivity.Instance) {
                    if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)
                }

            } else {
                val req = VendorUpdateRequest(
                    address = vendorDetail!!.address,
                    city = vendorDetail!!.city,
                    code = code,
                    companyName = companyName,
                    country = vendorDetail!!.country,
                    email = mail,
                    groupId = vendorDetail!!.groupId,
                    id = vendorDetail!!.id,
                    name = vendorName,
                    payTerm = vendorDetail!!.payTerm,
                    personName = personName,
                    phoneNo = number,
                    state = vendorDetail!!.state,
                    taxGroupId = taxGroup.value
                )

                VendorActivity.Instance.viewModel.updateVendor(req).observe(VendorActivity.Instance) {
                    if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, Vendor(name = vendorName), true)
                }
            }
        } else VendorActivity.Instance.showSweetDialog(VendorActivity.Instance, resources.getString(R.string.lbl_NameMissing))
    }
}