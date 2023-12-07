package com.varitas.gokulpos.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.varitas.gokulpos.R
import com.varitas.gokulpos.adapter.DropDownAdapter
import com.varitas.gokulpos.databinding.ActivityAddProductBinding
import com.varitas.gokulpos.request.ItemDetails
import com.varitas.gokulpos.request.ItemPrice
import com.varitas.gokulpos.request.ItemStock
import com.varitas.gokulpos.request.ItemUPC
import com.varitas.gokulpos.request.ManageSpecification
import com.varitas.gokulpos.request.ProductInsertRequest
import com.varitas.gokulpos.request.QuantityDetail
import com.varitas.gokulpos.request.SoldAlong
import com.varitas.gokulpos.request.SpecificationDetails
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.response.DynamicDropDown
import com.varitas.gokulpos.response.ScanBarcode
import com.varitas.gokulpos.utilities.CustomSpinner
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Utils
import com.varitas.gokulpos.viewmodel.ProductFeatureViewModel
import com.varitas.gokulpos.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class AddProductActivity : BaseActivity() {

    private lateinit var binding : ActivityAddProductBinding
    private val viewModel : ProductViewModel by viewModels()
    private val viewModelDropDown : ProductFeatureViewModel by viewModels()
    private lateinit var categoriesList : ArrayList<CommonDropDown>
    private lateinit var categoriesSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var category : CommonDropDown
    private lateinit var departmentList : ArrayList<CommonDropDown>
    private lateinit var departmentSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var department : CommonDropDown
    private lateinit var taxGroupList : ArrayList<CommonDropDown>
    private lateinit var taxGroupSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var taxGroup : CommonDropDown
    private lateinit var brandList : ArrayList<CommonDropDown>
    private lateinit var brandSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var brand : CommonDropDown
    private var scannedBarcode : ScanBarcode? = null
    private lateinit var dropDownAdapter : DropDownAdapter

    private lateinit var manageSpecification : ArrayList<ManageSpecification>
    private lateinit var itemStock : ArrayList<ItemStock>
    private lateinit var priceList : ArrayList<ItemPrice>
    private lateinit var soldAlongList : ArrayList<SoldAlong>

    private lateinit var subCategoryList : ArrayList<CommonDropDown>
    private lateinit var subCategorySpinner : ArrayAdapter<CommonDropDown>
    private lateinit var subCategory : CommonDropDown
    private var parentId = 0

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initData()
        postInitData()
        loadData()
        manageClicks()
    }


    private fun initData() {
        parentId = if(intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_AddProduct)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@AddProductActivity, R.drawable.ic_save))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }

        dropDownAdapter = DropDownAdapter { commonDropDown, id, name, specificationId ->
            val ind = manageSpecification.indexOfFirst { it.typeId == id }
            if (ind >= 0) {
                manageSpecification[ind].id = commonDropDown.value!!
                manageSpecification[ind].value = commonDropDown.label!!
                manageSpecification[ind].typeName = name
                manageSpecification[ind].specificationId = specificationId
            } else manageSpecification.add(ManageSpecification(commonDropDown.value!!, commonDropDown.label!!, id, name, specificationId))
        }

        scannedBarcode = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(intent.getParcelableExtra(Default.PRODUCT, ScanBarcode::class.java) != null)
                intent.getParcelableExtra(Default.PRODUCT, ScanBarcode::class.java)
            else null
        } else {
            if(intent.getParcelableExtra<ScanBarcode>(Default.PRODUCT) != null)
                intent.getParcelableExtra<ScanBarcode>(Default.PRODUCT)!!
            else null
        }

        categoriesList = ArrayList()
        categoriesSpinner = ArrayAdapter(this, R.layout.spinner_items, categoriesList)
        departmentList = ArrayList()
        departmentSpinner = ArrayAdapter(this, R.layout.spinner_items, departmentList)
        taxGroupList = ArrayList()
        taxGroupSpinner = ArrayAdapter(this, R.layout.spinner_items, taxGroupList)
        brandList = ArrayList()
        brandSpinner = ArrayAdapter(this, R.layout.spinner_items, brandList)
        manageSpecification = ArrayList()
        priceList = ArrayList()
        itemStock = ArrayList()
        soldAlongList = ArrayList()
        subCategoryList = ArrayList()
        subCategorySpinner = ArrayAdapter(this, R.layout.spinner_items, subCategoryList)
    }

    private fun postInitData() {
        binding.apply {
            layoutBasic.recyclerViewSizePack.apply {
                adapter = dropDownAdapter
                layoutManager = GridLayoutManager(this@AddProductActivity, 2)
            }
            layoutBasic.imageViewScan.visibility = View.VISIBLE

            layoutBasic.textInputUnitPrice.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {
                }

                override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                }

                override fun afterTextChanged(s : Editable?) {
                    setMarginMarkup(unitCost = if(!layoutBasic.textInputUnitCost.text.isNullOrEmpty()) layoutBasic.textInputUnitCost.text.toString().toDouble() else 0.00, unitPrice = if(!TextUtils.isEmpty(s)) s.toString().toDouble() else 0.00)
                }
            })

            layoutBasic.textInputUnitCost.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {
                }

                override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                }

                override fun afterTextChanged(s : Editable?) {
                    setMarginMarkup(unitPrice = if(!layoutBasic.textInputUnitPrice.text.isNullOrEmpty()) layoutBasic.textInputUnitPrice.text.toString().toDouble() else 0.00, unitCost = if(!TextUtils.isEmpty(s)) s.toString().toDouble() else 0.00)
                }
            })
        }
    }

    //region To load data
    private fun loadData() {
        if(scannedBarcode != null) {
            binding.apply {
                layoutBasic.textInputUPC.setText(scannedBarcode!!.upc)
                layoutBasic.textInputName.setText(scannedBarcode!!.name)
            }
        }
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }

        viewModel.errorMsg.observe(this) {
            showSweetDialog(this, it)
        }

        viewModelDropDown.getDepartment().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                departmentSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    department = CommonDropDown(0, resources.getString(R.string.lbl_SelectDepartment))
                    add(department)
                    addAll(it)
                    if(scannedBarcode != null) {
                        val ind = departmentList.indexOfFirst { dept-> dept.value == scannedBarcode!!.departmentId }
                        if(ind >= 0) binding.layoutBasic.spinnerDepartment.setSelection(ind)
                    }
                    notifyDataSetChanged()
                }
            }
        }

        viewModelDropDown.getParentCategory().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                categoriesSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    category = CommonDropDown(0, resources.getString(R.string.lbl_SelectCategory))
                    add(category)
                    addAll(it)
                    if(scannedBarcode != null) {
                        val ind = categoriesList.indexOfFirst { cate-> cate.value == scannedBarcode!!.categoryId }
                        if(ind >= 0) binding.layoutBasic.spinnerCategory.setSelection(ind)
                    }
                    notifyDataSetChanged()
                    subCategorySpinner.apply {
                        setDropDownViewResource(R.layout.spinner_dropdown_item)
                        clear()
                        subCategory = CommonDropDown(0, resources.getString(R.string.lbl_SelectSubCategories))
                        add(subCategory)
                        notifyDataSetChanged()
                    }
                }
            }
        }

        viewModelDropDown.fetchTaxGroups(Default.TYPE_TAX).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                taxGroupSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    taxGroup = CommonDropDown(label = resources.getString(R.string.lbl_SelectTax), value = 0)
                    add(taxGroup)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }

        viewModelDropDown.fetchFacility().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                for(data in it) itemStock.add(ItemStock(facilityId = data.id, quantity = data.quantity.toLong()))
            }
        }

        viewModelDropDown.getBrandDropDown().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                brandSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    brand = CommonDropDown(label = resources.getString(R.string.lbl_SelectBrand), value = 0)
                    add(brand)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }

        viewModelDropDown.getSpecificationType(Default.SPECIFICATION_TYPE).observe(this) {
            for(i in it.indices) {
                viewModel.getSizePackDropDown(it[i].value!!).observe(this) { dropDownList->
                    dropDownList.add(0, CommonDropDown(label = it[i].label, value = it[i].value))
                    CoroutineScope(Dispatchers.Main).launch {
                        dropDownAdapter.apply {
                            setList(DynamicDropDown(dropDownList))
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    } //endregion

    private fun setMarginMarkup(unitCost : Double, unitPrice : Double, buyDown : Double = 0.00) {
        binding.apply {
            layoutBasic.textInputMargin.setText(Utils.getTwoDecimalValue(Utils.calculateMargin(unitPrice, unitCost, buyDown)))
            layoutBasic.textInputMarkup.setText(Utils.getTwoDecimalValue(Utils.calculateMarkUp(unitPrice, unitCost, buyDown)))
        }
    }

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewBack.clickWithDebounce {
                val intent = Intent(this@AddProductActivity, ProductListActivity::class.java)
                intent.putExtra(Default.PARENT_ID, parentId)
                openActivity(intent)
            }

            layoutToolbar.imageViewAction.clickWithDebounce {
                val unitPrice = if(!TextUtils.isEmpty(layoutBasic.textInputUnitPrice.text.toString().trim())) layoutBasic.textInputUnitPrice.text.toString().trim().toDouble() else 0.00
                val unitCost = if(!TextUtils.isEmpty(layoutBasic.textInputUnitCost.text.toString().trim())) layoutBasic.textInputUnitCost.text.toString().trim().toDouble() else 0.00
                val margin = if(!TextUtils.isEmpty(layoutBasic.textInputMargin.text.toString().trim())) layoutBasic.textInputMargin.text.toString().trim().toDouble() else 0.00
                val markup = if(!TextUtils.isEmpty(layoutBasic.textInputMarkup.text.toString().trim())) layoutBasic.textInputMarkup.text.toString().trim().toDouble() else 0.00
                if(!TextUtils.isEmpty(layoutBasic.textInputName.text.toString()) && !TextUtils.isEmpty(layoutBasic.textInputUPC.text.toString())) {
                    if(unitPrice >= unitCost) {

                        //After talking with the API team, we need to deliver the specifications to them in the correct order, for example, size first, then pack.
                        val listSpecification = ArrayList<SpecificationDetails>()
                        val specificationList = manageSpecification
                            .filter { it.value != null && it.typeName != null }
                            .sortedByDescending { it.typeName!! }

                        for (data in specificationList) {
                            if (data.id != data.typeId && data.value!!.trim() != data.typeName!!.trim()) {
                                val specificationDetails = SpecificationDetails(id = data.id, value = data.value!!.trim())
                                listSpecification.add(specificationDetails)
                            }
                        }

                        priceList.clear()
                        priceList.add(
                            ItemPrice(
                                unitPrice = unitPrice, unitCost = unitCost,
                                minPrice = 0.00,
                                buyDown = 0.00,
                                msrp = 0.00,
                                salesPrice = 0.00,
                                margin = margin,
                                markup = markup,
                                quantity = 1
                            )
                        )

                        val upcList = ArrayList<ItemUPC>()
                        if(!TextUtils.isEmpty(layoutBasic.textInputUPC.text.toString().trim())) upcList.add(ItemUPC(layoutBasic.textInputUPC.text.toString().trim()))

                        val details = ArrayList<ItemDetails>()
                        details.add(
                            ItemDetails(
                                specification = listSpecification,
                                attributes = ArrayList(),
                                price = priceList,
                                stock = itemStock,
                                upcList = upcList,
                                soldAlong = soldAlongList,
                                qtyDetail = QuantityDetail()
                            )
                        )
                        val req = ProductInsertRequest(
                            name = layoutBasic.textInputName.text.toString().trim(),
                            type = 1,
                            departmentId = department.value,
                            brandId = brand.value,
                            categoryId = if(subCategory.value!! > 0) subCategory.value else category.value,
                            taxGroupId = taxGroup.value,
                            promptForPrice = layoutBasic.checkBoxPrice.isChecked,
                            promptForQuantity = layoutBasic.checkBoxQty.isChecked,
                            nonPluItem = layoutBasic.checkBoxNonPlu.isChecked,
                            itemdetails = details
                        )
                        viewModel.insertProduct(req).observe(this@AddProductActivity) {
                            if(it) {
                                val intent = Intent(this@AddProductActivity, ProductListActivity::class.java)
                                intent.putExtra(Default.PARENT_ID, parentId)
                                openActivity(intent)
                            }
                        }
                    } else showSweetDialog(this@AddProductActivity, resources.getString(R.string.lbl_UnitPriceValidation))
                } else if(TextUtils.isEmpty(layoutBasic.textInputName.text.toString())) showSweetDialog(this@AddProductActivity, resources.getString(R.string.lbl_NameMissing))
                else showSweetDialog(this@AddProductActivity, resources.getString(R.string.lbl_UPCMissing))
            }

            layoutBasic.imageViewScan.clickWithDebounce {
                val intent = Intent(this@AddProductActivity, BarcodeActivity::class.java)
                intent.putExtra(Default.PARENT_ID, parentId)
                openActivity(intent)
            }

            layoutBasic.spinnerDepartment.apply {
                adapter = departmentSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        department = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            layoutBasic.spinnerCategory.apply {
                adapter = categoriesSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        category = spinner?.selectedItem as CommonDropDown
                        if(category.value!! > 0) {
                            viewModelDropDown.fetSubCategories(category.value!!).observe(this@AddProductActivity) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    subCategorySpinner.apply {
                                        setDropDownViewResource(R.layout.spinner_dropdown_item)
                                        clear()
                                        subCategory = CommonDropDown(0, resources.getString(R.string.lbl_SelectSubCategories))
                                        add(subCategory)
                                        addAll(it)
                                        notifyDataSetChanged()
                                    }
                                }
                            }
                        } else {
                            subCategorySpinner.apply {
                                setDropDownViewResource(R.layout.spinner_dropdown_item)
                                clear()
                                subCategory = CommonDropDown(0, resources.getString(R.string.lbl_SelectSubCategories))
                                add(subCategory)
                                notifyDataSetChanged()
                            }
                        }
                    }
                })
            }

            layoutBasic.spinnerTaxGroup.apply {
                adapter = taxGroupSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        taxGroup = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            layoutBasic.spinnerBrand.apply {
                adapter = brandSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        brand = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            layoutBasic.spinnerSubCategory.apply {
                adapter = subCategorySpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        subCategory = spinner?.selectedItem as CommonDropDown
                    }
                })
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        val intent = Intent(this@AddProductActivity, ProductListActivity::class.java)
        intent.putExtra(Default.PARENT_ID, parentId)
        openActivity(intent)
        onBackPressedDispatcher.onBackPressed()
    }

    //region To set spinner data
    private fun setSpinnerData(spinner : Spinner, ind : Int) {
        spinner.setSelection(if(ind >= 0) ind else 0)
    } //endregion
}