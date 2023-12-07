package com.varitas.gokulpos.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.varitas.gokulpos.R
import com.varitas.gokulpos.activity.VendorActivity
import com.varitas.gokulpos.databinding.FragmentDSalespersonBinding
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.response.SalesPerson
import com.varitas.gokulpos.utilities.CustomSpinner
import com.varitas.gokulpos.utilities.Enums
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SalesPersonPopupDialog : BaseDialogFragment() {
    private var mActivity : Activity? = null
    private var binding : FragmentDSalespersonBinding? = null
    private var onButtonClickListener : OnButtonClickListener? = null
    private lateinit var vendorList : ArrayList<CommonDropDown>
    private lateinit var vendorSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var vendor : CommonDropDown

    companion object {
        fun newInstance() : SalesPersonPopupDialog {
            val f = SalesPersonPopupDialog()
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
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?,
    ) : View {
        binding = FragmentDSalespersonBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun loadData() {
        VendorActivity.Instance.viewModel.showProgress.observe(this) {
            VendorActivity.Instance.manageProgress(it)
        }
        VendorActivity.Instance.viewModel.getVendorDropDown().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                vendorSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    vendor = CommonDropDown(0, resources.getString(R.string.lbl_SelectVendor))
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

    private fun postInitViews() {
        binding!!.layoutToolbar.textViewTitle.text = resources.getString(R.string.SalesPerson, resources.getString(R.string.lbl_Add))
    }

    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }
            spinnerVendor.apply {
                adapter = vendorSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {
                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        vendor = spinner?.selectedItem as CommonDropDown
                    }
                })
            }
            buttonSave.clickWithDebounce {
                val name = textInputName.text.toString().trim()
                val number = textInputPhoneNum.text.toString().trim()
                if(name.isNotEmpty() && vendor.value!! > 0) {
                    val req = SalesPerson(
                        name = name,
                        contactNo = number,
                        vendorId = vendor.value!!
                    )
                    VendorActivity.Instance.viewModel.addSalesPerson(req).observe(VendorActivity.Instance) {
                        if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)
                    }
                } else if(name.isEmpty()) VendorActivity.Instance.showSweetDialog(VendorActivity.Instance, resources.getString(R.string.Validation, resources.getString(R.string.hint_Name)))
                else VendorActivity.Instance.showSweetDialog(VendorActivity.Instance, resources.getString(R.string.Validation, resources.getString(R.string.Menu_Vendor)))
            }
        }
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents)
    }
}