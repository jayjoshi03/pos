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
import com.varitas.gokulpos.activity.DepartmentActivity
import com.varitas.gokulpos.databinding.FragmentDDepartmentBinding
import com.varitas.gokulpos.request.DepartmentInsertUpdate
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.response.Department
import com.varitas.gokulpos.utilities.CustomSpinner
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import com.varitas.gokulpos.utilities.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DepartmentPopupDialog : BaseDialogFragment() {

    private var mActivity : Activity? = null
    private var onButtonClickListener : OnButtonClickListener? = null
    private var binding : FragmentDDepartmentBinding? = null
    private lateinit var taxGroupList : ArrayList<CommonDropDown>
    private lateinit var taxGroupSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var taxGroup : CommonDropDown
    private lateinit var brandList : ArrayList<CommonDropDown>
    private lateinit var brandSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var brand : CommonDropDown
    private var departmentDetail : DepartmentInsertUpdate? = null

    @Deprecated("Deprecated in Java")
    override fun onAttach(activity : Activity) {
        super.onAttach(activity)
        if(context is Activity) mActivity = activity
    }

    companion object {
        fun newInstance() : DepartmentPopupDialog {
            val f = DepartmentPopupDialog()
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View {
        binding = FragmentDDepartmentBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            if(arguments!!.getParcelable<DepartmentInsertUpdate>(Default.DEPARTMENT) != null) {
                departmentDetail = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arguments?.getParcelable(Default.DEPARTMENT, DepartmentInsertUpdate::class.java)!!
                else arguments?.getParcelable(Default.DEPARTMENT)!!
            }
        }
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun initData() {
        taxGroupList = ArrayList()
        taxGroupSpinner = ArrayAdapter(DepartmentActivity.Instance, R.layout.spinner_items, taxGroupList)
        brandList = ArrayList()
        brandSpinner = ArrayAdapter(DepartmentActivity.Instance, R.layout.spinner_items, brandList)
    }

    private fun loadData() {
        DepartmentActivity.Instance.viewModel.fetchTaxGroups(Default.TYPE_TAX).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                taxGroupSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    taxGroup = CommonDropDown(label = resources.getString(R.string.lbl_SelectTax), value = 0)
                    add(taxGroup)
                    addAll(it)
                    if(departmentDetail != null) {
                        val ind = taxGroupList.indexOfFirst { cate-> cate.value == departmentDetail!!.taxGroupId }
                        if(ind >= 0) {
                            binding!!.spinnerTax.setSelection(ind)
                            taxGroup = CommonDropDown(taxGroupList[ind].value, taxGroupList[ind].label)
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }

        DepartmentActivity.Instance.viewModel.getBrandDropDown().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                brandSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    brand = CommonDropDown(0, resources.getString(R.string.lbl_SelectBrand))
                    add(brand)
                    addAll(it)
                    if(departmentDetail != null) {
                        val ind = brandList.indexOfFirst { brand-> brand.value == departmentDetail!!.brandId }
                        if(ind >= 0) {
                            binding!!.spinnerBrand.setSelection(ind)
                            brand = CommonDropDown(brandList[ind].value, brandList[ind].label)
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }

        if(departmentDetail != null) {
            binding!!.apply {
                textInputName.setText(departmentDetail!!.name)
                textInputCode.setText(departmentDetail!!.code)
                if(departmentDetail!!.priceRatioValue != null) textInputPercentage.setText(Utils.getTwoDecimalValue(departmentDetail!!.priceRatioValue!!))
                checkBoxIsFoodStamp.isChecked = departmentDetail!!.allowFoodStamp!!
                checkBoxMinAge.isChecked = departmentDetail!!.ageVerification!!
                checkBoxShowInOpenPrice.isChecked = departmentDetail!!.showInOpenPrice!!
                checkBoxIsTaxable.isChecked = departmentDetail!!.isTaxable!!
                radioMargin.isChecked = departmentDetail!!.priceRatioType == Default.MARGIN
                radioMarkup.isChecked = departmentDetail!!.priceRatioType == Default.MARKUP
                checkBoxNonStock.isChecked = if(departmentDetail!!.nonStock != null) departmentDetail!!.nonStock!! else false
                checkBoxWebItem.isChecked = if(departmentDetail!!.webItemFlag != null) departmentDetail!!.webItemFlag!! else false
                checkBoxWICCheck.isChecked = if(departmentDetail!!.allowWicCheck != null) departmentDetail!!.allowWicCheck!! else false
                checkBoxNonDiscount.isChecked = if(departmentDetail!!.nonDiscountable != null) departmentDetail!!.nonDiscountable!! else false
                checkBoxNonCountable.isChecked = if(departmentDetail!!.nonCountable != null) departmentDetail!!.nonCountable!! else false
                checkBoxWeightItem.isChecked = if(departmentDetail!!.weightItemFlag != null) departmentDetail!!.weightItemFlag!! else false
                checkBoxFoodStamp.isChecked = if(departmentDetail!!.isFoodStamp != null) departmentDetail!!.isFoodStamp!! else false
                checkBoxNonRevenue.isChecked = if(departmentDetail!!.isNonRevenue != null) departmentDetail!!.isNonRevenue!! else false
                checkBoxAddBrand.isChecked = if(departmentDetail!!.allowInBrand != null) departmentDetail!!.allowInBrand!! else false
                if(checkBoxAddBrand.isChecked) {
                    linearLayoutBrand.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun postInitViews() {
        binding!!.layoutToolbar.textViewTitle.text = resources.getString(R.string.Department, if(departmentDetail == null) resources.getString(R.string.lbl_Add) else resources.getString(R.string.lbl_Edit))
    }

    private fun manageClicks() {
        binding!!.apply {

            checkBoxAddBrand.setOnCheckedChangeListener { _, isChecked->
                linearLayoutBrand.visibility = if(isChecked) View.VISIBLE else View.GONE
            }

            checkBoxIsTaxable.setOnCheckedChangeListener { _, isChecked->
                linearLayoutTax.visibility = if(isChecked) View.VISIBLE else View.GONE
            }

            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL, null)
            }
            buttonSave.clickWithDebounce {
                //region To insert/update department

                if(checkBoxAddBrand.isChecked) {
                    if(brand.value!! == 0) {
                        DepartmentActivity.Instance.showSweetDialog(DepartmentActivity.Instance, resources.getString(R.string.lbl_SelectBrandValid))
                        return@clickWithDebounce
                    }
                }

                if(checkBoxIsTaxable.isChecked) {
                    if(taxGroup.value!! == 0) {
                        DepartmentActivity.Instance.showSweetDialog(DepartmentActivity.Instance, resources.getString(R.string.lbl_SelectGroupValid))
                        return@clickWithDebounce
                    }
                }

                try {
                    val deptName = textInputName.text.toString().trim()
                    val priceVal = if(!TextUtils.isEmpty(textInputPercentage.text.toString().trim())) textInputPercentage.text.toString().trim().toDouble() else 0.00
                    if(!TextUtils.isEmpty(deptName)) {
                        if(priceVal <= 100.00) {
                            if(departmentDetail == null) {
                                val req = DepartmentInsertUpdate(
                                    name = deptName,
                                    code = textInputCode.text.toString().trim(),
                                    allowFoodStamp = checkBoxIsFoodStamp.isChecked,
                                    isTaxable = checkBoxIsTaxable.isChecked,
                                    taxGroupId = taxGroup.value,
                                    showInOpenPrice = checkBoxShowInOpenPrice.isChecked,
                                    priceRatioType = if(radioMargin.isChecked) Default.MARGIN else Default.MARKUP,
                                    priceRatioValue = priceVal,
                                    ageVerification = checkBoxMinAge.isChecked,
                                    pictureId = 0,
                                    nonStock = checkBoxNonStock.isChecked,
                                    webItemFlag = checkBoxWebItem.isChecked,
                                    allowWicCheck = checkBoxWICCheck.isChecked,
                                    nonDiscountable = checkBoxNonDiscount.isChecked,
                                    nonCountable = checkBoxNonCountable.isChecked,
                                    weightItemFlag = checkBoxWeightItem.isChecked,
                                    isFoodStamp = checkBoxFoodStamp.isChecked,
                                    isNonRevenue = checkBoxNonRevenue.isChecked,
                                    allowInBrand = checkBoxAddBrand.isChecked,
                                    brandId = brand.value
                                )
                                DepartmentActivity.Instance.viewModel.insertDepartment(req = req).observe(this@DepartmentPopupDialog) {
                                    if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, Department(name = textInputName.text.toString()))
                                }
                            } else {
                                val req = DepartmentInsertUpdate(
                                    id = departmentDetail!!.id,
                                    name = deptName,
                                    code = textInputCode.text.toString().trim(),
                                    ageVerification = checkBoxMinAge.isChecked,
                                    allowFoodStamp = checkBoxIsFoodStamp.isChecked,
                                    isTaxable = checkBoxIsTaxable.isChecked,
                                    pictureId = 0,
                                    priceRatioType = if(radioMargin.isChecked) Default.MARGIN else Default.MARKUP,
                                    priceRatioValue = priceVal,
                                    showInOpenPrice = checkBoxShowInOpenPrice.isChecked,
                                    taxGroupId = taxGroup.value,
                                    displayOnWeb = departmentDetail!!.displayOnWeb,
                                    departmentId = departmentDetail!!.id,
                                    nonStock = checkBoxNonStock.isChecked,
                                    webItemFlag = checkBoxWebItem.isChecked,
                                    allowWicCheck = checkBoxWICCheck.isChecked,
                                    nonDiscountable = checkBoxNonDiscount.isChecked,
                                    nonCountable = checkBoxNonCountable.isChecked,
                                    weightItemFlag = checkBoxWeightItem.isChecked,
                                    isFoodStamp = checkBoxFoodStamp.isChecked,
                                    isNonRevenue = checkBoxNonRevenue.isChecked,
                                    allowInBrand = checkBoxAddBrand.isChecked,
                                    brandId = brand.value
                                )

                                DepartmentActivity.Instance.viewModel.updateDepartment(req = req).observe(this@DepartmentPopupDialog) {
                                    if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, Department(name = textInputName.text.toString()), true)
                                }
                            }
                        } else DepartmentActivity.Instance.showSweetDialog(DepartmentActivity.Instance, resources.getString(R.string.lbl_RateValidation))
                    } else DepartmentActivity.Instance.showSweetDialog(DepartmentActivity.Instance, resources.getString(R.string.lbl_NameMissing))
                } catch(e : Exception) {
                    Utils.printAndWriteException(e)
                } //endregion
            }

            spinnerTax.apply {
                adapter = taxGroupSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        taxGroup = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            spinnerBrand.apply {
                adapter = brandSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        brand = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

        }
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents, department : Department? = null, isUpdate : Boolean = false)
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}