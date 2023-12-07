package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.TaxActivity
import com.varitas.gokulpos.tablet.databinding.FragmentDTaxBinding
import com.varitas.gokulpos.tablet.request.TaxInsertRequest
import com.varitas.gokulpos.tablet.request.TaxUpdateRequest
import com.varitas.gokulpos.tablet.response.Tax
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.Utils

class TaxPopupDialog : BaseDialogFragment() {

    private var mActivity : Activity? = null
    private var binding : FragmentDTaxBinding? = null
    private var onButtonClickListener : OnButtonClickListener? = null
    private var taxDetail : Tax? = null

    companion object {
        fun newInstance() : TaxPopupDialog {
            val f = TaxPopupDialog()
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
        binding = FragmentDTaxBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val taxParcelable = arguments?.getParcelable<Tax>(Default.TAX)
            if (taxParcelable != null) taxDetail = taxParcelable
        }
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun loadData() {
        TaxActivity.Instance.viewModel.showProgress.observe(this) {
            TaxActivity.Instance.manageProgress(it)
        }

        if(taxDetail != null) {
            binding!!.apply {
                textInputClassName.setText(taxDetail!!.className!!)
                textInputRate.setText(Utils.getTwoDecimalValue(taxDetail!!.rateValue!!))
                checkBoxBySize.isChecked = taxDetail!!.bySize
            }
        }
    }

    private fun postInitViews() {
        binding!!.layoutToolbar.textViewTitle.text = resources.getString(R.string.Tax, if(taxDetail == null) resources.getString(R.string.lbl_Add) else resources.getString(R.string.lbl_Edit))
    }

    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonSave.clickWithDebounce {
                val name = textInputClassName.text.toString().trim()
                val rateValue = textInputRate.text.toString().trim()
                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(rateValue)) {
                    if(taxDetail == null) {
                        val req = TaxInsertRequest(className = name, rateValue = rateValue.toDouble(), bySize = checkBoxBySize.isChecked)
                        TaxActivity.Instance.viewModel.insertTax(req).observe(TaxActivity.Instance) {
                            if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)
                        }
                    } else {
                        val req = TaxUpdateRequest(className = name, rateValue = rateValue.toDouble(), id = taxDetail!!.id, bySize = checkBoxBySize.isChecked)
                        TaxActivity.Instance.viewModel.updateTax(req).observe(TaxActivity.Instance) {
                            if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, Tax(className = name, rateValue = rateValue.toDouble()), true)
                        }
                    }
                } else TaxActivity.Instance.showSweetDialog(TaxActivity.Instance, resources.getString(R.string.Validation, if(TextUtils.isEmpty(name)) resources.getString(R.string.hint_Name) else resources.getString(R.string.lbl_Rate)))
            }
        }
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents, tax : Tax? = null, isUpdate : Boolean = false)
    }
}