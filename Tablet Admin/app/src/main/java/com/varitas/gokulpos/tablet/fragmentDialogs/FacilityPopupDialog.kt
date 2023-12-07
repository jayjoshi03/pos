package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.FacilityActivity
import com.varitas.gokulpos.tablet.databinding.FragmentDFacilityBinding
import com.varitas.gokulpos.tablet.request.FacilityInsertRequest
import com.varitas.gokulpos.tablet.request.FacilityUpdateRequest
import com.varitas.gokulpos.tablet.response.Facility
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums

class FacilityPopupDialog : BaseDialogFragment() {

    private var mActivity : Activity? = null
    private var binding : FragmentDFacilityBinding? = null
    private var onButtonClickListener : OnButtonClickListener? = null
    private var facilityDetail : Facility? = null

    companion object {
        fun newInstance() : FacilityPopupDialog {
            val f = FacilityPopupDialog()
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
        binding = FragmentDFacilityBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val facilityParcelable = arguments?.getParcelable<Facility>(Default.FACILITY)
            if (facilityParcelable != null) facilityDetail = facilityParcelable
        }
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun initData() {

    }

    private fun loadData() {
        FacilityActivity.Instance.viewModel.showProgress.observe(this) {
            FacilityActivity.Instance.manageProgress(it)
        }

        if(facilityDetail != null) {
            binding!!.apply {
                textInputName.setText(facilityDetail!!.name)
                textInputContactName.setText(facilityDetail!!.contactName)
                textInputContactNumber.setText(facilityDetail!!.contactNumber)
                textInputAddress.setText(facilityDetail!!.address)
                textInputCity.setText(facilityDetail!!.city)
                textInputState.setText(facilityDetail!!.state)
                textInputCountry.setText(facilityDetail!!.country)
                textInputZip.setText(facilityDetail!!.zip)
                checkBoxIsDefault.isChecked = if(facilityDetail!!.isDefault != null) facilityDetail!!.isDefault!! else false
            }
        }
    }

    private fun postInitViews() {
        binding!!.layoutToolbar.textViewTitle.text = resources.getString(R.string.Facility, if(facilityDetail == null) resources.getString(R.string.lbl_Add) else resources.getString(R.string.lbl_Edit))
    }

    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL, null, facilityDetail != null)
            }

            buttonSave.clickWithDebounce {
                val name = binding!!.textInputName.text.toString().trim()
                val contactName = binding!!.textInputContactName.text.toString().trim()
                val contactNumber = binding!!.textInputContactNumber.text.toString().trim()
                val address = binding!!.textInputAddress.text.toString().trim()
                val city = binding!!.textInputCity.text.toString().trim()
                val state = binding!!.textInputState.text.toString().trim()
                val country = binding!!.textInputCountry.text.toString().trim()
                val zip = binding!!.textInputZip.text.toString().trim()

                if(contactNumber.isNotEmpty()) {
                    if(contactNumber.length == 10)
                        manageFacility(name, contactName, address, city, state, country, zip, contactNumber)
                    else FacilityActivity.Instance.showSweetDialog(FacilityActivity.Instance, resources.getString(R.string.lbl_PhoneNoFormat))
                } else manageFacility(name, contactName, address, city, state, country, zip, contactNumber)
            }
        }
    }

    private fun FragmentDFacilityBinding.manageFacility(name : String, contactName : String, address : String, city : String, state : String, country : String, zip : String, contactNumber : String) {
        if(name.isNotEmpty()) {
            if(facilityDetail == null) {
                val req = FacilityInsertRequest(
                    name = name,
                    contactName = contactName,
                    address = address,
                    city = city,
                    state = state,
                    country = country,
                    zip = zip,
                    contactNumber = contactNumber, isDefault = checkBoxIsDefault.isChecked
                )
                FacilityActivity.Instance.viewModel.insertFacility(req).observe(FacilityActivity.Instance) {
                    if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)
                }
            } else {
                val req = FacilityUpdateRequest(
                    id = facilityDetail!!.id,
                    name = name,
                    contactName = contactName,
                    address = address,
                    city = city,
                    state = state,
                    country = country,
                    zip = zip,
                    contactNumber = contactNumber, isDefault = checkBoxIsDefault.isChecked
                )
                FacilityActivity.Instance.viewModel.updateFacility(req).observe(FacilityActivity.Instance) {
                    if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, Facility(name = name), true)
                }
            }
        } else FacilityActivity.Instance.showSweetDialog(FacilityActivity.Instance, resources.getString(R.string.lbl_NameMissing))
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents, facility : Facility? = null, isUpdate : Boolean = false)
    }
}