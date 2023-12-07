package com.varitas.gokulpos.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.varitas.gokulpos.R
import com.varitas.gokulpos.adapter.DropDownAdapter
import com.varitas.gokulpos.databinding.ActivityAddProductBinding
import com.varitas.gokulpos.request.Attributes
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
import com.varitas.gokulpos.response.Facility
import com.varitas.gokulpos.response.ProductList
import com.varitas.gokulpos.response.ProductResponse
import com.varitas.gokulpos.utilities.CustomSpinner
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Utils
import com.varitas.gokulpos.viewmodel.ProductFeatureViewModel
import com.varitas.gokulpos.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class EditProductActivity : BaseActivity() {

    private lateinit var binding : ActivityAddProductBinding
    private val viewModel : ProductViewModel by viewModels()
    private val viewModelDropDown : ProductFeatureViewModel by viewModels()
    private lateinit var categoryList : ArrayList<CommonDropDown>
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
    private var product : ProductList? = null
    private var productDetail : ProductResponse? = null
    private lateinit var dropDownAdapter : DropDownAdapter

    private lateinit var manageSpecification : ArrayList<ManageSpecification>
    private lateinit var itemStock : ArrayList<ItemStock>
    private lateinit var priceList : ArrayList<ItemPrice>
    private lateinit var soldAlongList : ArrayList<SoldAlong>
    private lateinit var facilities : ArrayList<Facility>

    private lateinit var subCategoryList : ArrayList<CommonDropDown>
    private lateinit var subCategorySpinner : ArrayAdapter<CommonDropDown>
    private lateinit var subCategory : CommonDropDown
    private var parentId = 0

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        product = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(intent.getParcelableExtra(Default.PRODUCT, ProductList::class.java) != null) intent.getParcelableExtra(Default.PRODUCT, ProductList::class.java)
            else null
        } else {
            if(intent.getParcelableExtra<ProductList>(Default.PRODUCT) != null)
                intent.getParcelableExtra(Default.PRODUCT) as ProductList?
            else null
        }
        initData()
        postInitData()
        loadData()
        manageClicks()
    }

    private fun initData() {
        parentId = if(intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_EditProduct)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@EditProductActivity, R.drawable.ic_save))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }

        dropDownAdapter = DropDownAdapter { commonDropDown, id, name, specificationId->
            val ind = manageSpecification.indexOfFirst { it.typeId == id }
            if(ind >= 0) {
                manageSpecification[ind].id = commonDropDown.value!!
                manageSpecification[ind].value = commonDropDown.label!!
            } else manageSpecification.add(
                ManageSpecification(
                    id = commonDropDown.value!!,
                    value = commonDropDown.label!!,
                    typeId = id,
                    typeName = name,
                    specificationId = specificationId
                )
            )
        }

        categoryList = ArrayList()
        categoriesSpinner = ArrayAdapter(this, R.layout.spinner_items, categoryList)
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
        facilities = ArrayList()
        subCategoryList = ArrayList()
        subCategorySpinner = ArrayAdapter(this, R.layout.spinner_items, subCategoryList)
    }

    //region To load data
    private fun loadData() {
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }

        viewModel.getItemDetail(product!!.id!!).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                if(it != null) {
                    productDetail = it
                    binding.layoutBasic.apply {
                        if(it.itemdetails.size > 0) {
                            if(it.itemdetails[0].upcList.size > 0) textInputUPC.setText(if(!TextUtils.isEmpty(it.itemdetails[0].upcList[0].itemUpc)) it.itemdetails[0].upcList[0].itemUpc else "")
                            else textInputUPC.setText("")
                            textInputSKU.setText(it.sku)
                            textInputName.setText(it.name)

                            textInputUnitPrice.setText(Utils.getTwoDecimalValue(if(it.itemdetails[0].price.size > 0) {
                                val ind = it.itemdetails[0].price.indexOfFirst { it.quantity == 1L }
                                if(ind >= 0) {
                                    if(it.itemdetails[0].price[ind].unitPrice!! >= 0.00) it.itemdetails[0].price[ind].unitPrice!! else 0.00
                                } else 0.00
                            } else 0.00))

                            textInputUnitCost.setText(Utils.getTwoDecimalValue(if(it.itemdetails[0].price.size > 0) {
                                val ind = it.itemdetails[0].price.indexOfFirst { it.quantity == 1L }
                                if(ind >= 0) {
                                    if(it.itemdetails[0].price[ind].unitCost!! >= 0.00) it.itemdetails[0].price[ind].unitCost!! else 0.00
                                } else 0.00
                            } else 0.00))

                            textInputMarkup.setText(Utils.getTwoDecimalValue(if(it.itemdetails[0].price.size > 0) {
                                val ind = it.itemdetails[0].price.indexOfFirst { it.quantity == 1L }
                                if(ind >= 0) {
                                    if(it.itemdetails[0].price[ind].markup!! >= 0.00) it.itemdetails[0].price[ind].markup!! else 0.00
                                } else 0.00
                            } else 0.00))

                            textInputMargin.setText(Utils.getTwoDecimalValue(if(it.itemdetails[0].price.size > 0) {
                                val ind = it.itemdetails[0].price.indexOfFirst { it.quantity == 1L }
                                if(ind >= 0) {
                                    if(it.itemdetails[0].price[ind].margin!! >= 0.00) it.itemdetails[0].price[ind].margin!! else 0.00
                                } else 0.00
                            } else 0.00))

                            val dataList = it.itemdetails[0].stock
                            facilities.clear()
                            for(data in dataList) {
                                facilities.add(Facility(id = data.facilityId, name = data.sFacility, quantity = data.quantity!!.toInt()))
                                itemStock.add(ItemStock(id = data.id, facilityId = data.facilityId, quantity = data.quantity!!.toLong()))
                            }

                            for(data in it.itemdetails[0].soldAlong) soldAlongList.add(SoldAlong(data.soldAlongId, data.soldAlongQuantity, data.soldAlongItemId, data.specificationId, data.soldAlongItemName!!))

                            val list = it.itemdetails[0].price
                            for(data in list) {
                                if(data.quantity!! > 1) {
                                    priceList.add(ItemPrice(id = data.id, unitPrice = data.unitPrice, unitCost = data.unitCost, minPrice = data.minPrice, buyDown = data.buyDown, msrp = data.msrp, salesPrice = data.salesPrice, margin = data.margin, markup = data.markup, quantity = data.quantity))
                                }
                            }
                        }

                        viewModelDropDown.getParentCategory().observe(this@EditProductActivity) { cat->
                            CoroutineScope(Dispatchers.Main).launch {
                                categoriesSpinner.apply {
                                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                                    category = CommonDropDown(0, resources.getString(R.string.lbl_SelectCategory))
                                    add(category)
                                    addAll(cat)
                                    val ind = categoryList.indexOfFirst { category-> category.value == it.categoryId }
                                    if(ind >= 0) {
                                        binding.layoutBasic.spinnerCategory.setSelection(ind)
                                        category = categoryList[ind]
                                        manageSubCategory()
                                    }
                                    notifyDataSetChanged()
                                }
                            }
                        }

                        viewModelDropDown.getDepartment().observe(this@EditProductActivity) { depart->
                            CoroutineScope(Dispatchers.Main).launch {
                                departmentSpinner.apply {
                                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                                    department = CommonDropDown(0, resources.getString(R.string.lbl_SelectDepartment))
                                    add(department)
                                    addAll(depart)
                                    val ind = departmentList.indexOfFirst { department-> department.value == it.departmentId }
                                    if(ind >= 0) {
                                        binding.layoutBasic.spinnerDepartment.setSelection(ind)
                                        department = departmentList[ind]
                                    }
                                    notifyDataSetChanged()
                                }
                            }
                        }

                        viewModelDropDown.fetchTaxGroups(Default.TYPE_TAX).observe(this@EditProductActivity) { tax->
                            CoroutineScope(Dispatchers.Main).launch {
                                taxGroupSpinner.apply {
                                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                                    taxGroup = CommonDropDown(label = resources.getString(R.string.lbl_SelectTax), value = 0)
                                    add(taxGroup)
                                    addAll(tax)
                                    val ind = taxGroupList.indexOfFirst { taxGroup-> taxGroup.value == it.taxGroupId }
                                    if(ind >= 0) {
                                        binding.layoutBasic.spinnerTaxGroup.setSelection(ind)
                                        taxGroup = taxGroupList[ind]
                                    }
                                    notifyDataSetChanged()
                                }
                            }
                        }
                        checkBoxPrice.isChecked = if(it.promptForPrice != null) it.promptForPrice!! else false
                        checkBoxQty.isChecked = if(it.promptForQuantity != null) it.promptForQuantity!! else false
                        checkBoxNonPlu.isChecked = if(it.nonPluItem != null) it.nonPluItem!! else false
                    }

                    viewModelDropDown.getBrandDropDown().observe(this@EditProductActivity) { brandItem->
                        CoroutineScope(Dispatchers.Main).launch {
                            brandSpinner.apply {
                                setDropDownViewResource(R.layout.spinner_dropdown_item)
                                brand = CommonDropDown(label = resources.getString(R.string.lbl_SelectBrand), value = 0)
                                add(brand)
                                addAll(brandItem)
                                val ind = brandList.indexOfFirst { brand-> brand.value == it.brandId }
                                if(ind >= 0) {
                                    binding.layoutBasic.spinnerBrand.setSelection(ind)
                                    brand = brandList[ind]
                                }
                                notifyDataSetChanged()
                            }
                        }
                    }

                    viewModelDropDown.getSpecificationType(Default.SPECIFICATION_TYPE).observe(this@EditProductActivity) { dd->
                        for(i in dd.indices) {
                            viewModel.getSizePackDropDown(dd[i].value!!).observe(this@EditProductActivity) { details->
                                details.add(0, CommonDropDown(dd[i].value, dd[i].label))
                                CoroutineScope(Dispatchers.Main).launch {
                                    dropDownAdapter.apply {
                                        if(it.itemdetails.size > 0) {
                                            if(it.itemdetails[0].specification != null) {
                                                if(it.itemdetails[0].specification.size > 0) {
                                                    val indexVal = it.itemdetails[0].specification.indexOfFirst { detail-> detail.typeId == dd[i].value }
                                                    if(indexVal >= 0) {
                                                        val index = details.indexOfFirst { data-> data.value == it.itemdetails[0].specification[indexVal].id }
                                                        if(index >= 0) setList(DynamicDropDown(list = details, id = details[index].value!!, specificationId = it.itemdetails[0].specification[indexVal].specificationId!!))
                                                        else setList(DynamicDropDown(list = details, id = dd[i].value!!))
                                                    } else setList(DynamicDropDown(list = details, id = dd[i].value!!))
                                                } else setList(DynamicDropDown(list = details, id = dd[i].value!!))
                                            } else setList(DynamicDropDown(list = details, id = dd[i].value!!))
                                        } else setList(DynamicDropDown(list = details, id = dd[i].value!!))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } //endregion

    private fun manageSubCategory() {
        subCategorySpinner.apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item)
            clear()
            subCategory = CommonDropDown(0, resources.getString(R.string.lbl_SelectSubCategories))
            add(subCategory)
            viewModelDropDown.fetSubCategories(category.value!!).observe(this@EditProductActivity) {
                addAll(it)
                val index = subCategoryList.indexOfFirst { subCategory-> subCategory.value == productDetail!!.subCategory!! }
                if(index >= 0) {
                    binding.layoutBasic.spinnerSubCategory.setSelection(index)
                    subCategory = subCategoryList[index]
                }
                notifyDataSetChanged()
            }
        }
    }

    private fun postInitData() {
        binding.apply {
            layoutBasic.recyclerViewSizePack.apply {
                adapter = dropDownAdapter
                layoutManager = GridLayoutManager(this@EditProductActivity, 2)
            }
            val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f)
            layoutBasic.textInputLayoutUPC.layoutParams = param

            layoutBasic.textInputUnitPrice.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {
                }

                override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                }

                override fun afterTextChanged(s : Editable?) {
                    setMarginMarkup(unitCost = if(!layoutBasic.textInputUnitCost.text.isNullOrEmpty()) layoutBasic.textInputUnitCost.text.toString().toDouble() else 0.00, unitPrice = if(!TextUtils.isEmpty(s)) s.toString().toDouble() else 0.00, buyDown = if(productDetail!!.itemdetails[0].price.size > 0) {
                        val ind = productDetail!!.itemdetails[0].price.indexOfFirst { it.quantity == 1L }
                        if(ind >= 0) {
                            if(productDetail!!.itemdetails[0].price[ind].buyDown!! >= 0.00) productDetail!!.itemdetails[0].price[ind].buyDown!! else 0.00
                        } else 0.00
                    } else 0.00)
                }
            })

            layoutBasic.textInputUnitCost.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {
                }

                override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                }

                override fun afterTextChanged(s : Editable?) {
                    setMarginMarkup(unitPrice = if(!layoutBasic.textInputUnitPrice.text.isNullOrEmpty()) layoutBasic.textInputUnitPrice.text.toString().toDouble() else 0.00, unitCost = if(!TextUtils.isEmpty(s)) s.toString().toDouble() else 0.00, buyDown = if(productDetail!!.itemdetails[0].price.size > 0) {
                        val ind = productDetail!!.itemdetails[0].price.indexOfFirst { it.quantity == 1L }
                        if(ind >= 0) {
                            if(productDetail!!.itemdetails[0].price[ind].buyDown!! >= 0.00) productDetail!!.itemdetails[0].price[ind].buyDown!! else 0.00
                        } else 0.00
                    } else 0.00)
                }
            })

        }
    }

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewBack.clickWithDebounce {
                val intent = Intent(this@EditProductActivity, ProductListActivity::class.java)
                intent.putExtra(Default.PARENT_ID, parentId)
                openActivity(intent)
            }

            layoutToolbar.imageViewAction.clickWithDebounce {
                val unitPrice = if(!TextUtils.isEmpty(layoutBasic.textInputUnitPrice.text.toString().trim())) layoutBasic.textInputUnitPrice.text.toString().trim().toDouble() else 0.00
                val unitCost = if(!TextUtils.isEmpty(layoutBasic.textInputUnitCost.text.toString().trim())) layoutBasic.textInputUnitCost.text.toString().trim().toDouble() else 0.00
                val margin = if(!TextUtils.isEmpty(layoutBasic.textInputMargin.text.toString().trim())) layoutBasic.textInputMargin.text.toString().trim().toDouble() else 0.00
                val markup = if(!TextUtils.isEmpty(layoutBasic.textInputMarkup.text.toString().trim())) layoutBasic.textInputMarkup.text.toString().trim().toDouble() else 0.00
                if(!TextUtils.isEmpty(layoutBasic.textInputName.text.toString()) && !TextUtils.isEmpty(layoutBasic.textInputUPC.text.toString()) && productDetail!!.type == 1) {
                    if(unitPrice >= unitCost) {

                        //After talking with the API team, we need to deliver the specifications to them in the correct order, for example, size first, then pack.
                        val listSpecification = ArrayList<SpecificationDetails>()
                        val specificationList = manageSpecification
                            .filter { it.value != null && it.typeName != null }
                            .sortedByDescending { it.typeName!! }

                        for(data in specificationList) {
                            if(data.id != data.typeId && data.value!!.trim() != data.typeName!!.trim()) {
                                listSpecification.add(SpecificationDetails(specificationId = data.specificationId, id = data.id, value = data.value))
                            }
                        }

                        val itemAttributes = ArrayList<Attributes>()
                        val details = ArrayList<ItemDetails>()
                        if(productDetail!!.itemdetails.size > 0) {
                            if(productDetail!!.itemdetails[0].price.size > 0) {
                                val ind = productDetail!!.itemdetails[0].price.indexOfFirst { it.quantity == 1L }
                                if(ind >= 0) {
                                    priceList.add(
                                        ItemPrice(
                                            id = productDetail!!.itemdetails[0].price[ind].id,
                                            specification = productDetail!!.itemdetails[0].price[ind].specification,
                                            unitPrice = unitPrice,
                                            unitCost = unitCost,
                                            minPrice = productDetail!!.itemdetails[0].price[ind].minPrice,
                                            buyDown = productDetail!!.itemdetails[0].price[ind].buyDown,
                                            msrp = productDetail!!.itemdetails[0].price[ind].msrp,
                                            salesPrice = productDetail!!.itemdetails[0].price[ind].salesPrice,
                                            margin = margin,
                                            markup = markup,
                                            quantity = 1
                                        )
                                    )
                                }
                            } else {
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
                            }

                            if(productDetail!!.itemdetails[0].attribute.size > 0) {
                                val attribute = productDetail!!.itemdetails[0].attribute[0]
                                itemAttributes.add(
                                    Attributes(
                                        id = attribute.id,
                                        itemId = attribute.itemId,
                                        value = attribute.value,
                                        uom = attribute.uom,
                                        specificationId = attribute.specificationId,
                                        itemStockQuantity = attribute.itemStockQuantity,
                                    )
                                )
                            }

                            val upcList = ArrayList<ItemUPC>()
                            if(!TextUtils.isEmpty(layoutBasic.textInputUPC.text.toString().trim())) upcList.add(ItemUPC(layoutBasic.textInputUPC.text.toString().trim()))

                            val qtyDetail = QuantityDetail(minWarnQty = productDetail!!.itemdetails[0].qtyDetail!!.minWarnQty!!, reOrder = productDetail!!.itemdetails[0].qtyDetail!!.reOrder!!, id = productDetail!!.itemdetails[0].qtyDetail!!.id!!)

                            if(soldAlongList.size > 0) soldAlongList[0].soldalongquantity = productDetail!!.itemdetails[0].soldAlong[0].soldAlongQuantity

                            details.add(
                                ItemDetails(
                                    flag = 2,
                                    soldAlong = soldAlongList,
                                    specification = listSpecification,
                                    attributes = itemAttributes,
                                    price = priceList,
                                    stock = itemStock,
                                    upcList = upcList,
                                    qtyDetail = qtyDetail
                                )
                            )
                        }

                        val req = ProductInsertRequest(
                            id = productDetail!!.id,
                            itemGuid = productDetail!!.itemGuid,
                            name = layoutBasic.textInputName.text.toString().trim(),
                            sku = productDetail!!.sku,
                            model = productDetail!!.model,
                            type = productDetail!!.type,
                            description = productDetail!!.description,
                            departmentId = department.value,
                            brandId = brand.value,
                            categoryId = if(subCategory.value!! > 0) subCategory.value else category.value,
                            vintage = productDetail!!.vintage,
                            unitType = productDetail!!.unitType,
                            taxGroupId = taxGroup.value,
                            isShortcut = productDetail!!.isShortcut,
                            buyCase = productDetail!!.buyCase,
                            nonStockItem = productDetail!!.nonStockItem,
                            promptForPrice = layoutBasic.checkBoxPrice.isChecked,
                            promptForQuantity = layoutBasic.checkBoxQty.isChecked,
                            nonDiscountable = productDetail!!.nonDiscountable,
                            countWithNoDisc = productDetail!!.countWithNoDisc,
                            returnItem = productDetail!!.returnItem,
                            acceptFoodStamp = productDetail!!.acceptFoodStamp,
                            depositItem = productDetail!!.depositItem,
                            acceptWicCheck = productDetail!!.acceptWiccheck,
                            nonRevenue = productDetail!!.nonRevenue,
                            webItem = productDetail!!.webItem,
                            nonCountable = productDetail!!.nonCountable,
                            weightedItem = productDetail!!.weightedItem,
                            nonPluItem = layoutBasic.checkBoxNonPlu.isChecked,
                            itemdetails = details
                        )

                        viewModel.updateProduct(req).observe(this@EditProductActivity) {
                            if(it) {
                                val intent = Intent(this@EditProductActivity, ProductListActivity::class.java)
                                intent.putExtra(Default.PARENT_ID, parentId)
                                openActivity(intent)
                            }
                        }
                    } else showSweetDialog(this@EditProductActivity, resources.getString(R.string.lbl_UnitPriceValidation))
                } else if(productDetail!!.type != 1) showSweetDialog(this@EditProductActivity, resources.getString(R.string.lbl_StandardValidation))
                else showSweetDialog(this@EditProductActivity, resources.getString(R.string.Validation, if(TextUtils.isEmpty(layoutBasic.textInputName.text.toString())) resources.getString(R.string.hint_Name) else resources.getString(R.string.hint_UPC)))
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
                            viewModelDropDown.fetSubCategories(category.value!!).observe(this@EditProductActivity) {
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
        val intent = Intent(this@EditProductActivity, ProductListActivity::class.java)
        intent.putExtra(Default.PARENT_ID, parentId)
        openActivity(intent)
        onBackPressedDispatcher.onBackPressed()
    }

    private fun setMarginMarkup(unitCost : Double, unitPrice : Double, buyDown : Double) {
        binding.layoutBasic.textInputMargin.setText(Utils.getTwoDecimalValue(Utils.calculateMargin(unitPrice, unitCost, buyDown)))
        binding.layoutBasic.textInputMarkup.setText(Utils.getTwoDecimalValue(Utils.calculateMarkUp(unitPrice, unitCost, buyDown)))
    }
}