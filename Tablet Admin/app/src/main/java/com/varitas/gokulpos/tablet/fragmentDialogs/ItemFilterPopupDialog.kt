package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.GridLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.ProductListActivity
import com.varitas.gokulpos.tablet.adapter.DropDownAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDFilterBinding
import com.varitas.gokulpos.tablet.model.ItemGroupDetail
import com.varitas.gokulpos.tablet.request.ManageSpecification
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.DynamicDropDown
import com.varitas.gokulpos.tablet.utilities.CustomSpinner
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemFilterPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDFilterBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private lateinit var categoriesList: ArrayList<CommonDropDown>
    private lateinit var categoriesSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var category: CommonDropDown
    private lateinit var departmentList: ArrayList<CommonDropDown>
    private lateinit var departmentSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var department: CommonDropDown
    private lateinit var brandList: ArrayList<CommonDropDown>
    private lateinit var brandSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var brand: CommonDropDown
    private lateinit var subCategoriesList: ArrayList<CommonDropDown>
    private lateinit var subCategoriesSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var subCategory: CommonDropDown
    private lateinit var itemTypeList: ArrayList<CommonDropDown>
    private lateinit var itemTypeSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var itemType: CommonDropDown
    private lateinit var itemGroupList: ArrayList<ItemGroupDetail>
    private lateinit var itemGroupSpinner: ArrayAdapter<ItemGroupDetail>
    private lateinit var itemGroup: ItemGroupDetail
    private lateinit var dropDownAdapter: DropDownAdapter
    private lateinit var manageSpecification: ArrayList<ManageSpecification>

    companion object {
        fun newInstance(): ItemFilterPopupDialog {
            val f = ItemFilterPopupDialog()
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
        binding = FragmentDFilterBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun initData() {
        categoriesList = ArrayList()
        category = CommonDropDown(0, resources.getString(R.string.lbl_SelectCategory))
        categoriesList.add(category)
        categoriesSpinner = ArrayAdapter(ProductListActivity.Instance, R.layout.spinner_items, categoriesList)

        departmentList = ArrayList()
        department = CommonDropDown(0, resources.getString(R.string.lbl_SelectDepartment))
        departmentList.add(department)
        departmentSpinner = ArrayAdapter(ProductListActivity.Instance, R.layout.spinner_items, departmentList)

        subCategoriesList = ArrayList()
        subCategory = CommonDropDown(0, resources.getString(R.string.lbl_SelectSubCategories))
        subCategoriesList.add(subCategory)
        subCategoriesSpinner = ArrayAdapter(ProductListActivity.Instance, R.layout.spinner_items, subCategoriesList)

        brandList = ArrayList()
        brand = CommonDropDown(0, resources.getString(R.string.lbl_SelectBrand))
        brandList.add(brand)
        brandSpinner = ArrayAdapter(ProductListActivity.Instance, R.layout.spinner_items, brandList)

        itemGroupList = ArrayList()
        itemGroup = ItemGroupDetail(0, resources.getString(R.string.lbl_SelectItemGroup))
        itemGroupList.add(itemGroup)
        itemGroupSpinner = ArrayAdapter(ProductListActivity.Instance, R.layout.spinner_items, itemGroupList)

        itemTypeList = ArrayList()
        itemTypeSpinner = ArrayAdapter(ProductListActivity.Instance, R.layout.spinner_items, itemTypeList)

        manageSpecification = ArrayList()

        dropDownAdapter = DropDownAdapter { commonDropDown, id, name, specificationId ->
            val ind = manageSpecification.indexOfFirst { it.typeId == id }
            if (ind >= 0) {
                manageSpecification[ind].id = commonDropDown.value!!
                manageSpecification[ind].value = commonDropDown.label!!
                manageSpecification[ind].typeName = name
                manageSpecification[ind].specificationId = specificationId
            } else manageSpecification.add(ManageSpecification(commonDropDown.value!!, commonDropDown.label!!, id, name, specificationId))
        }
    }

    //region To load data
    private fun loadData() {

        ProductListActivity.Instance.viewModelFeature.showProgress.observe(this) {
            ProductListActivity.Instance.manageProgress(it)
        }

        ProductListActivity.Instance.viewModelFeature.getDepartment().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                departmentSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    clear()
                    department = CommonDropDown(0, resources.getString(R.string.lbl_SelectDepartment))
                    add(department)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }

        ProductListActivity.Instance.viewModelFeature.getBrandDropDown().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                brandSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    clear()
                    brand = CommonDropDown(0, resources.getString(R.string.lbl_SelectBrand))
                    add(brand)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }

        ProductListActivity.Instance.viewModelFeature.getParentCategory().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                categoriesSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    clear()
                    category = CommonDropDown(0, resources.getString(R.string.lbl_SelectCategory))
                    add(category)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }

        ProductListActivity.Instance.viewModelFeature.fetchItemGroups(Default.TYPE_ITEM).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                itemGroupSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    clear()
                    itemGroup = ItemGroupDetail(0, resources.getString(R.string.lbl_SelectItemGroup))
                    add(itemGroup)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }

        itemTypeSpinner.apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item)
            itemType = CommonDropDown(Default.STANDARD, resources.getString(R.string.lbl_SelectItemType))
            add(itemType)
            add(CommonDropDown(Default.MULTI_PACK, resources.getString(R.string.lbl_MultiPack)))
            add(CommonDropDown(Default.STANDARD, resources.getString(R.string.lbl_Standard)))
            add(CommonDropDown(Default.BAR, resources.getString(R.string.lbl_BarItem)))
            add(CommonDropDown(Default.LOT_MATRIX, resources.getString(R.string.lbl_LotMatrix)))
            notifyDataSetChanged()
        }

        ProductListActivity.Instance.viewModelFeature.getSpecificationType(Default.SPECIFICATION_TYPE).observe(this) {
            for (i in it.indices) {
                ProductListActivity.Instance.viewModel.getSizePackDropDown(it[i].value!!).observe(this) { details ->
                    details.add(0, CommonDropDown(it[i].value, it[i].label))
                    CoroutineScope(Dispatchers.Main).launch {
                        dropDownAdapter.apply {
                            setList(DynamicDropDown(details))
                        }
                    }
                }
            }
        }
    }
    //endregion

    //region To post init views
    private fun postInitViews() {
        binding!!.apply {
            layoutToolbar.textViewTitle.text = resources.getText(R.string.lbl_Filter)

            recycleViewSizePack.apply {
                adapter = dropDownAdapter
                layoutManager = GridLayoutManager(ProductListActivity.Instance, 3)
            }
        }
    }
    //endregion

    //region To manage clicks
    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonClose.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonSave.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, department.value!!, if (subCategory.value!! > 0) subCategory.value!! else category.value!!, brand.value!!, itemType.value!!)
            }

            spinnerCategory.apply {
                adapter = categoriesSpinner
                var previousSelectedItem: CommonDropDown? = null
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner: Spinner?) {
                        previousSelectedItem = spinner?.selectedItem as? CommonDropDown
                    }

                    override fun onSpinnerClosed(spinner: Spinner?) {
                        category = spinner?.selectedItem as CommonDropDown
                        if (category != previousSelectedItem) manageSubCategories(category.value!!)
                    }
                })
            }

            spinnerSubCategory.apply {
                adapter = subCategoriesSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner: Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner: Spinner?) {
                        subCategory = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            spinnerDepartment.apply {
                adapter = departmentSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner: Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner: Spinner?) {
                        department = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            spinnerBrand.apply {
                adapter = brandSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner: Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner: Spinner?) {
                        brand = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            spinnerItemType.apply {
                adapter = itemTypeSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner: Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner: Spinner?) {
                        itemType = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            spinnerItemGroup.apply {
                adapter = itemGroupSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner: Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner: Spinner?) {
                        itemGroup = spinner?.selectedItem as ItemGroupDetail
                    }
                })
            }
        }
    }
    //endregion

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, departmentId: Int = 0, categoryId: Int = 0, brandId: Int = 0, itemTypeId: Int = 0)
    }

    //region To manage Sub Categories
    private fun manageSubCategories(id: Int) {
        ProductListActivity.Instance.viewModelFeature.getSubCategory(id).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                subCategoriesSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    clear()
                    subCategory = CommonDropDown(0, resources.getString(R.string.lbl_SelectSubCategories))
                    add(subCategory)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }
    }
    //endregion
}