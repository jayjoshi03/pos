package com.varitas.gokulpos.fragmentDialogs

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.varitas.gokulpos.R
import com.varitas.gokulpos.activity.SpecificationActivity
import com.varitas.gokulpos.databinding.FragmentDSpecificationBinding
import com.varitas.gokulpos.request.SpecificationInsertRequest
import com.varitas.gokulpos.request.SpecificationUpdateRequest
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.response.Specification
import com.varitas.gokulpos.utilities.CustomSpinner
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
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
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
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
        if(arguments != null) {
            if(arguments!!.getParcelable<Specification>(Default.SPECIFICATION) != null) {
                specificationDetails = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arguments?.getParcelable(Default.SPECIFICATION, Specification::class.java)!!
                else arguments?.getParcelable(Default.SPECIFICATION)!!
            }
        }
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun loadData() {
        if(specificationDetails != null) {
            binding!!.editTextValue.setText(specificationDetails!!.name)
        }

        SpecificationActivity.Instance.viewModel.getUOM().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                umoSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    uom = CommonDropDown(0, resources.getString(R.string.lbl_SelectUOM))
                    add(uom)
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
        SpecificationActivity.Instance.viewModel.getSpecificationDropDown().observe(this) {
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
                val value = binding!!.editTextValue.text.toString().trim()
                if(value.isNotEmpty() && specificationType.value != 0 && uom.value != 0) {
                    if(specificationDetails == null) {
                        val req = SpecificationInsertRequest(
                            name = value,
                            typeId = specificationType.value,
                            uomId = uom.value,
                            type = specificationType.label,
                            uom = uom.label
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
                            uom = uom.label
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
