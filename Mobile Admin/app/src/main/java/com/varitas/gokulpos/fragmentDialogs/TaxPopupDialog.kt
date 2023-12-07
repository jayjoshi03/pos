package com.varitas.gokulpos.fragmentDialogs

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.varitas.gokulpos.R
import com.varitas.gokulpos.activity.TaxActivity
import com.varitas.gokulpos.databinding.FragmentDTaxBinding
import com.varitas.gokulpos.request.TaxInsertRequest
import com.varitas.gokulpos.request.TaxUpdateRequest
import com.varitas.gokulpos.response.Tax
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.utilities.Utils

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
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
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
        if(arguments != null) {
            if(arguments!!.getParcelable<Tax>(Default.TAX) != null) {
                taxDetail = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arguments?.getParcelable(Default.TAX, Tax::class.java)!!
                else arguments?.getParcelable(Default.TAX)!!
            }
        }
        initData()
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
                checkBoxBySize.isChecked = if(taxDetail!!.bySize != null) taxDetail!!.bySize!! else false
            }
        }
    }

    private fun initData() {

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
                val rateValue = if(!TextUtils.isEmpty(textInputRate.text.toString().trim())) textInputRate.text.toString().trim().toDouble() else 0.00
                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(rateValue.toString())) {
                    if(rateValue <= 100.00) {
                        if(taxDetail == null) {
                            val req = TaxInsertRequest(className = name, rateValue = rateValue, bySize = checkBoxBySize.isChecked)
                            TaxActivity.Instance.viewModel.insertTax(req).observe(TaxActivity.Instance) {
                                if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)
                            }
                        } else {
                            val req = TaxUpdateRequest(className = name, rateValue = rateValue, id = taxDetail!!.id, bySize = checkBoxBySize.isChecked)
                            TaxActivity.Instance.viewModel.updateTax(req).observe(TaxActivity.Instance) {
                                if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, Tax(className = name, rateValue = rateValue), true)
                            }
                        }
                    } else TaxActivity.Instance.showSweetDialog(TaxActivity.Instance, resources.getString(R.string.lbl_RateValidation))
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