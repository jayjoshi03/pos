package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.VendorActivity
import com.varitas.gokulpos.tablet.databinding.FragmentDAddsalePersonBinding
import com.varitas.gokulpos.tablet.request.AddSalePerson
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.Vendor
import com.varitas.gokulpos.tablet.utilities.CustomSpinner
import com.varitas.gokulpos.tablet.utilities.Enums
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddSalePersonPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDAddsalePersonBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private lateinit var vendorList: ArrayList<CommonDropDown>
    private lateinit var vendorSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var vendor: CommonDropDown

    companion object {
        fun newInstance(): AddSalePersonPopupDialog {
            val f = AddSalePersonPopupDialog()
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (context is Activity) mActivity = activity
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(1500, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
                             ): View {
        binding = FragmentDAddsalePersonBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        loadData()
        manageClicks()
    }

    private fun loadData() {
        VendorActivity.Instance.viewModel.showProgress.observe(this) {
            VendorActivity.Instance.manageProgress(it)
        }

        VendorActivity.Instance.viewModel.fetchVendorDropDown().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                vendorSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    vendor = CommonDropDown(label = resources.getString(R.string.lbl_SelectVendor), value = 0)
                    add(vendor)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun initData() {
        vendorList = ArrayList()
        vendorSpinner = ArrayAdapter(VendorActivity.Instance, R.layout.spinner_items, vendorList)
    }

    private fun manageClicks() {
        binding!!.apply {
            buttonClose.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonSave.clickWithDebounce {
                binding!!.apply {
                    val name = textInputName.text.toString().trim()
                    val phoneNum = textInputPhoneNum.text.toString().trim()
                    if (vendor.value!! > 0) {
                        if (!TextUtils.isEmpty(name)) {
                            if (!TextUtils.isEmpty(phoneNum)) {
                                val req = AddSalePerson(id = 0, name = name, contactNo = phoneNum, vendorId = vendor.value)
                                VendorActivity.Instance.viewModel.insertSalePerson(req).observe(this@AddSalePersonPopupDialog) {
                                    if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)
                                }
                            } else VendorActivity.Instance.showSweetDialog(VendorActivity.Instance, resources.getString(R.string.Validation, resources.getString(R.string.lbl_PhoneNumber)))
                        } else VendorActivity.Instance.showSweetDialog(VendorActivity.Instance, resources.getString(R.string.Validation, resources.getString(R.string.hint_Name)))
                    } else VendorActivity.Instance.showSweetDialog(VendorActivity.Instance, resources.getString(R.string.Validation, resources.getString(R.string.lbl_SelectVendor)))
                }
            }

            spinnerVendor.apply {
                adapter = vendorSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner: Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner: Spinner?) {
                        vendor = spinner?.selectedItem as CommonDropDown
                    }
                })
            }
        }
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, vendor: Vendor? = null, isUpdate: Boolean = false)
    }
}