package com.varitas.gokulpos.fragmentDialogs

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.varitas.gokulpos.R
import com.varitas.gokulpos.activity.CategoryActivity
import com.varitas.gokulpos.databinding.FragmentDCategoryBinding
import com.varitas.gokulpos.request.CategoryInsertUpdate
import com.varitas.gokulpos.response.CategoryDetails
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.utilities.CustomSpinner
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryPopupDialog : BaseDialogFragment() {

    private var mActivity : Activity? = null
    private var binding : FragmentDCategoryBinding? = null
    private var onButtonClickListener : OnButtonClickListener? = null
    private lateinit var parentCategories : ArrayList<CommonDropDown>
    private lateinit var parentCategoriesSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var parentCategory : CommonDropDown
    private lateinit var departmentList : ArrayList<CommonDropDown>
    private lateinit var departmentSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var department : CommonDropDown
    private lateinit var brandList : ArrayList<CommonDropDown>
    private lateinit var brandSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var brand : CommonDropDown
    private var categoryDetail : CategoryInsertUpdate? = null

    companion object {
        fun newInstance() : CategoryPopupDialog {
            val f = CategoryPopupDialog()
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
        binding = FragmentDCategoryBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            if(arguments!!.getParcelable<CategoryInsertUpdate>(Default.CATEGORY) != null) {
                categoryDetail = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arguments?.getParcelable(Default.CATEGORY, CategoryInsertUpdate::class.java)!!
                else arguments?.getParcelable(Default.CATEGORY)!!
            }
        }
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun loadData() {
        if(categoryDetail != null) {
            binding?.apply {
                editTextCategory.setText(categoryDetail!!.name)
                checkBoxNonStock.isChecked = if(categoryDetail!!.nonStock != null) categoryDetail!!.nonStock!! else false
                checkBoxWebItem.isChecked = if(categoryDetail!!.webItemFlag != null) categoryDetail!!.webItemFlag!! else false
                checkBoxWICCheck.isChecked = if(categoryDetail!!.allowWicCheck != null) categoryDetail!!.allowWicCheck!! else false
                checkBoxNonDiscount.isChecked = if(categoryDetail!!.nonDiscountable != null) categoryDetail!!.nonDiscountable!! else false
                checkBoxNonCountable.isChecked = if(categoryDetail!!.nonCountable != null) categoryDetail!!.nonCountable!! else false
                checkBoxWeightItem.isChecked = if(categoryDetail!!.weightItemFlag != null) categoryDetail!!.weightItemFlag!! else false
                checkBoxFoodStamp.isChecked = if(categoryDetail!!.isFoodStamp != null) categoryDetail!!.isFoodStamp!! else false
                checkBoxNonRevenue.isChecked = if(categoryDetail!!.isNonRevenue != null) categoryDetail!!.isNonRevenue!! else false
                checkBoxAddBrand.isChecked = if(categoryDetail!!.allowInBrand != null) categoryDetail!!.allowInBrand!! else false
                if(checkBoxAddBrand.isChecked) {
                    linearLayoutBrand.visibility = View.VISIBLE
                }
            }
        }

        CategoryActivity.Instance.viewModel.getParentCategory().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                parentCategoriesSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    parentCategory = CommonDropDown(0, resources.getString(R.string.lbl_SelectParentCategory))
                    add(parentCategory)
                    addAll(it)
                    if(categoryDetail != null) {
                        val ind = parentCategories.indexOfFirst { category-> category.value == categoryDetail!!.parentCategory }
                        if(ind >= 0) {
                            binding!!.spinnerParentCategory.setSelection(ind)
                            parentCategory = parentCategories[ind]
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }

        CategoryActivity.Instance.viewModel.getDepartment().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                departmentSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    department = CommonDropDown(0, resources.getString(R.string.lbl_SelectDepartment))
                    add(department)
                    addAll(it)
                    if(categoryDetail != null) {
                        val ind = departmentList.indexOfFirst { department-> department.value == categoryDetail!!.departmentId }
                        if(ind >= 0) {
                            binding!!.spinnerDepartment.setSelection(ind)
                            department = departmentList[ind]
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }

        CategoryActivity.Instance.viewModel.getBrandDropDown().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                brandSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    brand = CommonDropDown(0, resources.getString(R.string.lbl_SelectBrand))
                    add(brand)
                    addAll(it)
                    if(categoryDetail != null) {
                        val ind = brandList.indexOfFirst { brand-> brand.value == categoryDetail!!.brandId }
                        if(ind >= 0) {
                            binding!!.spinnerBrand.setSelection(ind)
                            brand = brandList[ind]
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun initData() {
        parentCategories = ArrayList()
        parentCategoriesSpinner = ArrayAdapter(CategoryActivity.Instance, R.layout.spinner_items, parentCategories)
        departmentList = ArrayList()
        departmentSpinner = ArrayAdapter(CategoryActivity.Instance, R.layout.spinner_items, departmentList)
        brandList = ArrayList()
        brandSpinner = ArrayAdapter(CategoryActivity.Instance, R.layout.spinner_items, brandList)
    }

    private fun postInitViews() {
        binding!!.layoutToolbar.textViewTitle.text = resources.getString(R.string.Category, if(categoryDetail != null) resources.getString(R.string.lbl_Edit) else resources.getString(R.string.lbl_Add))
    }

    private fun manageClicks() {
        binding!!.apply {

            checkBoxAddBrand.setOnCheckedChangeListener { _, isChecked->
                linearLayoutBrand.visibility = if(isChecked) View.VISIBLE else View.GONE
            }

            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL, null, categoryDetail != null)
            }

            spinnerParentCategory.apply {
                adapter = parentCategoriesSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        parentCategory = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            spinnerDepartment.apply {
                adapter = departmentSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        department = spinner?.selectedItem as CommonDropDown
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

            buttonSave.clickWithDebounce { // add Category
                val categoryName = editTextCategory.text.toString().trim()

                if(checkBoxAddBrand.isChecked) {
                    if(brand.value!! == 0) {
                        CategoryActivity.Instance.showSweetDialog(CategoryActivity.Instance, resources.getString(R.string.lbl_SelectBrandValid))
                        return@clickWithDebounce
                    }
                }
                if(categoryName.isNotEmpty()) {
                    if(categoryDetail == null) {
                        val req = CategoryInsertUpdate(
                            name = categoryName,
                            parentCategory = parentCategory.value,
                            departmentId = department.value,
                            allowOnWeb = false,
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
                        CategoryActivity.Instance.viewModel.insertCategories(req).observe(CategoryActivity.Instance) {
                            if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)
                        }
                    } else {
                        val req = CategoryInsertUpdate(
                            id = categoryDetail!!.id,
                            name = categoryName,
                            parentCategory = parentCategory.value,
                            departmentId = department.value,
                            allowOnWeb = false,
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
                        CategoryActivity.Instance.viewModel.updateCategory(req).observe(CategoryActivity.Instance) {
                            if(it) {
                                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, CategoryDetails(name = categoryName), true)
                            }
                        }
                    }
                } else CategoryActivity.Instance.showSweetDialog(CategoryActivity.Instance, resources.getString(R.string.lbl_NameMissing))
            }
        }
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents, category : CategoryDetails? = null, isUpdate : Boolean = false)
    }

}