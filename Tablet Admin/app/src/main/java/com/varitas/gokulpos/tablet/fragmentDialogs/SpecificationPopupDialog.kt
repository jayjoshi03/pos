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
import com.varitas.gokulpos.tablet.activity.SpecificationActivity
import com.varitas.gokulpos.tablet.databinding.FragmentDSpecificationBinding
import com.varitas.gokulpos.tablet.request.SpecificationInsertRequest
import com.varitas.gokulpos.tablet.request.SpecificationUpdateRequest
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.Specification
import com.varitas.gokulpos.tablet.utilities.CustomSpinner
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpecificationPopupDialog : BaseDialogFragment() {

    private var mActivity : Activity? = null
    private var binding : FragmentDSpecificationBinding? = null
    private var onButtonClickListener : OnButtonClickListener? = null
    private lateinit var uomList : ArrayList<CommonDropDown>
    private lateinit var umoSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var uom : CommonDropDown
    private lateinit var specificationTypeList : ArrayList<CommonDropDown>
    private lateinit var specificationTypeSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var specificationType : CommonDropDown
    private var specificationDetails : Specification? = null

    companion object {
        fun newInstance() : SpecificationPopupDialog {
            val f = SpecificationPopupDialog()
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
        binding = FragmentDSpecificationBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val specificationParcelable = arguments?.getParcelable<Specification>(Default.SPECIFICATION)
            if (specificationParcelable != null) specificationDetails = specificationParcelable
        }
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun loadData() {
        if(specificationDetails != null) {
            binding!!.apply {
                editTextValue.setText(specificationDetails!!.name)
                editTextNoOfUnit.setText(if (specificationDetails!!.noOfUnit != null) specificationDetails!!.noOfUnit.toString() else "")
                editTextTaxFactor.setText(if (specificationDetails!!.taxFactor != null) Utils.getTwoDecimalValue(specificationDetails!!.taxFactor!!) else "")
                editTextUnitPriceFactor.setText(if (specificationDetails!!.unitPriceFactor != null) Utils.getTwoDecimalValue(specificationDetails!!.unitPriceFactor!!) else "")
                editTextUnitInCase.setText(if (specificationDetails!!.unitIn != null) specificationDetails!!.unitIn.toString() else "")
                editTextUnitPriceUOM.setText(if (specificationDetails!!.unitPriceUom != null) specificationDetails!!.unitPriceUom.toString() else "")
            }
        }

        SpecificationActivity.Instance.viewModel.getUOM().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                umoSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    addAll(it)
                    if(specificationDetails != null) {
                        val ind = uomList.indexOfFirst { uom-> uom.value == specificationDetails!!.uomId!! }
                        if(ind >= 0) {
                            binding!!.spinnerUOM.setSelection(ind)
                            uom = CommonDropDown(uomList[ind].value, uomList[ind].label)
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }

        getSpecificationType()
    }

    private fun getSpecificationType() {
        SpecificationActivity.Instance.viewModel.getSpecificationType(Default.ATTRIBUTE_TYPE).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                specificationTypeSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    clear()
                    specificationType = CommonDropDown(0, resources.getString(R.string.lbl_SelectSpecificationType))
                    add(specificationType)
                    addAll(it)
                    if(specificationDetails != null) {
                        val ind = specificationTypeList.indexOfFirst { specificationType-> specificationType.value == specificationDetails!!.typeId!! }
                        if(ind >= 0) {
                            binding!!.spinnerSpecificationType.setSelection(ind)
                            specificationType = CommonDropDown(specificationTypeList[ind].value, specificationTypeList[ind].label)
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun initData() {
        uomList = ArrayList()
        uom = CommonDropDown(0, resources.getString(R.string.lbl_SelectUOM))
        uomList.add(uom)
        umoSpinner = ArrayAdapter(SpecificationActivity.Instance, R.layout.spinner_items, uomList)
        specificationTypeList = ArrayList()
        specificationTypeSpinner = ArrayAdapter(SpecificationActivity.Instance, R.layout.spinner_items, specificationTypeList)
    }

    private fun postInitViews() {
        binding!!.layoutToolbar.textViewTitle.text = resources.getString(R.string.Specification, if(specificationDetails != null) resources.getString(R.string.lbl_Edit) else resources.getString(R.string.lbl_Add))
    }


    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL, null, specificationDetails != null)
            }

            spinnerUOM.apply {
                adapter = umoSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        uom = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            spinnerSpecificationType.apply {
                adapter = specificationTypeSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        specificationType = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            buttonSave.clickWithDebounce { // add Specification
                val value = editTextValue.text.toString().trim()
                val noOfUnit = editTextNoOfUnit.text.toString().trim()
                val taxFactor = editTextTaxFactor.text.toString().trim()
                val unitPriceFactor = editTextUnitPriceFactor.text.toString().trim()
                val unitPriceUOM = editTextUnitPriceUOM.text.toString().trim()
                val unitIn = editTextUnitInCase.text.toString().trim()
                if(value.isNotEmpty() && specificationType.value != 0 && uom.value != 0) {
                    if(specificationDetails == null) {
                        val req = SpecificationInsertRequest(
                            name = value,
                            typeId = specificationType.value,
                            uomId = uom.value,
                            type = specificationType.label,
                            uom = uom.label,
                            noOfUnit = if (!TextUtils.isEmpty(noOfUnit)) noOfUnit.toInt() else 0,
                            taxFactor = if (!TextUtils.isEmpty(taxFactor)) noOfUnit.toDouble() else 0.00,
                            unitIn = if (!TextUtils.isEmpty(unitIn)) unitIn.toInt() else 0,
                            unitPriceFactor = if (!TextUtils.isEmpty(unitPriceFactor)) unitPriceFactor.toDouble() else 0.00,
                            unitPriceUom = if (!TextUtils.isEmpty(unitPriceUOM)) unitPriceUOM.toInt() else 0,
                                                            )
                        SpecificationActivity.Instance.viewModel.insertSpecification(req).observe(SpecificationActivity.Instance) {
                            if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)
                        }
                    } else {
                        val req = SpecificationUpdateRequest(
                            id = specificationDetails!!.id,
                            name = value,
                            typeId = specificationType.value,
                            uomId = uom.value,
                            type = specificationType.label,
                            uom = uom.label,
                            noOfUnit = if (!TextUtils.isEmpty(noOfUnit)) noOfUnit.toInt() else 0,
                            taxFactor = if (!TextUtils.isEmpty(taxFactor)) noOfUnit.toDouble() else 0.00,
                            unitIn = if (!TextUtils.isEmpty(unitIn)) unitIn.toInt() else 0,
                            unitPriceFactor = if (!TextUtils.isEmpty(unitPriceFactor)) unitPriceFactor.toDouble() else 0.00,
                            unitPriceUom = if (!TextUtils.isEmpty(unitPriceUOM)) unitPriceUOM.toInt() else 0,
                                                            )
                        SpecificationActivity.Instance.viewModel.updateSpecification(req).observe(SpecificationActivity.Instance) {
                            if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, Specification(name = value, type = specificationType.label, typeId = specificationType.value, uom = uom.label, uomId = uom.value), true)
                        }
                    }
                } else SpecificationActivity.Instance.showSweetDialog(SpecificationActivity.Instance, resources.getString(R.string.Validation, if(TextUtils.isEmpty(value)) resources.getString(R.string.hint_Value) else if(specificationType.value == 0) resources.getString(R.string.lbl_Type) else resources.getString(R.string.Menu_UOM)))
            }

        }
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents, specification : Specification? = null, isUpdate : Boolean = false)
    }
}
