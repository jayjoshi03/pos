package com.varitas.gokulpos.tablet.activity

import android.content.Intent
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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.adapter.AutoCompleteAdapter
import com.varitas.gokulpos.tablet.adapter.DropDownAdapter
import com.varitas.gokulpos.tablet.adapter.ItemPackDetailsAdapter
import com.varitas.gokulpos.tablet.adapter.SoldAlongAdapter
import com.varitas.gokulpos.tablet.adapter.UPCAdapter
import com.varitas.gokulpos.tablet.databinding.ActivityAddProductBinding
import com.varitas.gokulpos.tablet.fragmentDialogs.BarItemPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.DeleteAlertPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.FacilityQuantityPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.ItemPriceListPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.PriceAlertPopupDialog
import com.varitas.gokulpos.tablet.fragmentDialogs.UPCPopupDialog
import com.varitas.gokulpos.tablet.model.ItemDetailsPack
import com.varitas.gokulpos.tablet.request.Attributes
import com.varitas.gokulpos.tablet.request.ItemDetails
import com.varitas.gokulpos.tablet.request.ItemPrice
import com.varitas.gokulpos.tablet.request.ItemStock
import com.varitas.gokulpos.tablet.request.ItemUPC
import com.varitas.gokulpos.tablet.request.ManageSpecification
import com.varitas.gokulpos.tablet.request.ProductInsertRequest
import com.varitas.gokulpos.tablet.request.QuantityDetail
import com.varitas.gokulpos.tablet.request.SoldAlong
import com.varitas.gokulpos.tablet.request.SpecificationDetails
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.DynamicDropDown
import com.varitas.gokulpos.tablet.response.Facility
import com.varitas.gokulpos.tablet.response.FavouriteItems
import com.varitas.gokulpos.tablet.response.ProductList
import com.varitas.gokulpos.tablet.response.ProductResponse
import com.varitas.gokulpos.tablet.utilities.CustomSpinner
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.Utils
import com.varitas.gokulpos.tablet.viewmodel.OrdersViewModel
import com.varitas.gokulpos.tablet.viewmodel.ProductFeatureViewModel
import com.varitas.gokulpos.tablet.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint class EditProductActivity : BaseActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val viewModel: ProductViewModel by viewModels()
    val viewModelDropDown: ProductFeatureViewModel by viewModels()
    private val viewModelOrder: OrdersViewModel by viewModels()
    private lateinit var categoryList: ArrayList<CommonDropDown>
    private lateinit var categoriesSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var category: CommonDropDown
    private lateinit var subCategoryList: ArrayList<CommonDropDown>
    private lateinit var subCategoriesSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var subCategory: CommonDropDown
    private lateinit var departmentList: ArrayList<CommonDropDown>
    private lateinit var departmentSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var department: CommonDropDown
    private lateinit var taxGroupList: ArrayList<CommonDropDown>
    private lateinit var taxGroupSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var taxGroup: CommonDropDown
    private lateinit var itemTypeList: ArrayList<CommonDropDown>
    private lateinit var itemTypeSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var itemType: CommonDropDown
    private lateinit var brandList: ArrayList<CommonDropDown>
    private lateinit var brandSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var brand: CommonDropDown
    private var product: ProductList? = null
    private var productDetail: ProductResponse? = null
    private lateinit var dropDownAdapter: DropDownAdapter
    private lateinit var soldAlongAdapter: SoldAlongAdapter
    private lateinit var upcAdapter: UPCAdapter
    private lateinit var manageSpecification: ArrayList<ManageSpecification>
    private lateinit var itemStock: ArrayList<ItemStock>
    private lateinit var priceList: ArrayList<ItemPrice>
    private lateinit var facilities: ArrayList<Facility>
    private lateinit var suggestionAdapter: AutoCompleteAdapter
    private lateinit var itemPackDetailsAdapter: ItemPackDetailsAdapter
    private lateinit var itemDetailsList: ArrayList<ItemDetails>
    private lateinit var soldAlongItem: FavouriteItems
    private lateinit var uomList: ArrayList<CommonDropDown>
    private lateinit var uomSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var uom: CommonDropDown
    private lateinit var attributeList: ArrayList<CommonDropDown>
    private lateinit var attributeSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var attribute: CommonDropDown
    private lateinit var barItemList: ArrayList<Attributes>
    private lateinit var suggestionBarAdapter: AutoCompleteAdapter
    private lateinit var barItem: FavouriteItems
    private var parentId = 0
    private var isFromOrder = false

    companion object {
        lateinit var Instance: EditProductActivity
    }

    init {
        Instance = this
    }


    override fun onCreate(savedInstanceState: Bundle?) {
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
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_EditProduct)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@EditProductActivity, R.drawable.ic_save))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }

        itemPackDetailsAdapter = ItemPackDetailsAdapter { it, pos, oldPosition, click ->

            when (click) {
                Enums.ClickEvents.VIEW -> {
                    if (it.itemDetails!!.flag == Default.UPDATE) {
                        itemPackDetailsAdapter.apply {
                            binding.apply {
                                layoutBasic.apply {
                                    checkBoxBuyAsCase.isChecked = false
                                    textInputUnitInCase.text!!.clear()
                                    textInputCaseCost.text!!.clear()
                                    textInputCasePrice.text!!.clear()
                                    list[oldPosition].itemDetails!!.apply {
                                        price.clear()
                                        price = ArrayList(priceList)
                                        val index = price.indexOfFirst { it.quantity == 1L }
                                        if (index >= 0) {
                                            price[index].unitPrice = if (!TextUtils.isEmpty(textInputUnitPrice.text.toString().trim())) textInputUnitPrice.text.toString().trim().toDouble() else 0.00
                                            price[index].unitCost = if (!TextUtils.isEmpty(textInputUnitCost.text.toString().trim())) textInputUnitCost.text.toString().trim().toDouble() else 0.00
                                            price[index].minPrice = if (!TextUtils.isEmpty(textInputMinPrice.text.toString().trim())) textInputMinPrice.text.toString().trim().toDouble() else 0.00
                                            price[index].buyDown = if (!TextUtils.isEmpty(textInputBuyDown.text.toString().trim())) textInputBuyDown.text.toString().trim().toDouble() else 0.00
                                            price[index].msrp = if (!TextUtils.isEmpty(textInputMSRP.text.toString().trim())) textInputMSRP.text.toString().trim().toDouble() else 0.00
                                            price[index].salesPrice = if (!TextUtils.isEmpty(textInputSalePrice.text.toString().trim())) textInputSalePrice.text.toString().trim().toDouble() else 0.00
                                            price[index].margin = if (!TextUtils.isEmpty(textInputMargin.text.toString().trim())) textInputMargin.text.toString().trim().toDouble() else 0.00
                                            price[index].markup = if (!TextUtils.isEmpty(textInputMarkup.text.toString().trim())) textInputMarkup.text.toString().trim().toDouble() else 0.00
                                        } else {
                                            price.add(
                                                ItemPrice(
                                                    unitPrice = if (!TextUtils.isEmpty(textInputUnitPrice.text.toString().trim())) textInputUnitPrice.text.toString().trim().toDouble() else 0.00,
                                                    unitCost = if (!TextUtils.isEmpty(textInputUnitCost.text.toString().trim())) textInputUnitCost.text.toString().trim().toDouble() else 0.00,
                                                    minPrice = if (!TextUtils.isEmpty(textInputMinPrice.text.toString().trim())) textInputMinPrice.text.toString().trim().toDouble() else 0.00,
                                                    buyDown = if (!TextUtils.isEmpty(textInputBuyDown.text.toString().trim())) textInputBuyDown.text.toString().trim().toDouble() else 0.00,
                                                    msrp = if (!TextUtils.isEmpty(textInputMSRP.text.toString().trim())) textInputMSRP.text.toString().trim().toDouble() else 0.00,
                                                    salesPrice = if (!TextUtils.isEmpty(textInputSalePrice.text.toString().trim())) textInputSalePrice.text.toString().trim().toDouble() else 0.00,
                                                    margin = if (!TextUtils.isEmpty(textInputMargin.text.toString().trim())) textInputMargin.text.toString().trim().toDouble() else 0.00,
                                                    markup = if (!TextUtils.isEmpty(textInputMarkup.text.toString().trim())) textInputMarkup.text.toString().trim().toDouble() else 0.00,
                                                    quantity = 1
                                                         )
                                                     )
                                        }
                                        qtyDetail = QuantityDetail(if (!TextUtils.isEmpty(textInputMinWarnQty.text.toString())) textInputMinWarnQty.text.toString().toInt() else 0, if (!TextUtils.isEmpty(textInputReOrder.text.toString())) textInputReOrder.text.toString().toInt() else 0)
                                        upcList.clear()
                                        upcList = ArrayList(upcAdapter.list)
                                        soldAlong.clear()
                                        soldAlong = ArrayList(soldAlongAdapter.list)
                                        stock.clear()
                                        stock = ArrayList(itemStock)
                                    }
                                }
                            }
                        }

                        soldAlongAdapter.apply {
                            list.clear()
                            notifyDataSetChanged()
                            setList(it.itemDetails!!.soldAlong)
                        }

                        priceList.apply {
                            clear()
                            addAll(it.itemDetails!!.price)
                        }

                        itemStock.apply {
                            clear()
                            addAll(it.itemDetails!!.stock)
                            if (it.itemDetails!!.stock.isNotEmpty()) {
                                for (data in it.itemDetails!!.stock) {
                                    val ind = facilities.indexOfFirst { it.id == data.facilityId }
                                    if (ind >= 0) facilities[ind].quantity = data.quantity!!.toInt()
                                }
                            } else facilities.forEach { it.quantity = 0 }
                        }

                        upcAdapter.apply {
                            list.clear()
                            notifyDataSetChanged()
                            setList(it.itemDetails!!.upcList)
                        }

                        binding.layoutBasic.apply {
                            manageDisableViews(binding, true)
                            textViewSelectedPack.text = "${it.name}"
                            if (priceList.size > 0) {
                                val ind = priceList.indexOfFirst { it.quantity == 1L }
                                if (ind >= 0) {
                                    textInputUnitPrice.setText(Utils.getTwoDecimalValue(priceList[ind].unitPrice!!))
                                    textInputUnitCost.setText(Utils.getTwoDecimalValue(priceList[ind].unitCost!!))
                                    textInputMinPrice.setText(Utils.getTwoDecimalValue(priceList[ind].minPrice!!))
                                    textInputBuyDown.setText(Utils.getTwoDecimalValue(priceList[ind].buyDown!!))
                                    textInputMSRP.setText(Utils.getTwoDecimalValue(priceList[ind].msrp!!))
                                    textInputSalePrice.setText(Utils.getTwoDecimalValue(priceList[ind].salesPrice!!))
                                    textInputMargin.setText(Utils.getTwoDecimalValue(priceList[ind].margin!!))
                                    textInputMarkup.setText(Utils.getTwoDecimalValue(priceList[ind].markup!!))
                                } else manageItemPrice(binding)
                            } else manageItemPrice(binding)

                            upcAdapter.apply {
                                val ind = list.indexOfFirst { it.isAuto }
                                if (ind < 0) {
                                    buttonAuto.isEnabled = true
                                    buttonAuto.isClickable = true
                                }
                            }

                            textInputMinWarnQty.setText(it.itemDetails!!.qtyDetail!!.minWarnQty.toString())
                            textInputReOrder.setText(it.itemDetails!!.qtyDetail!!.reOrder.toString())
                            val sum = itemStock.sumOf { it.quantity!! }.toString()
                            textInputOnHand.setText(if (sum.length > 1) sum else "0$sum")
                        }
                    } else {
                        binding.layoutBasic.apply {
                            manageDisableViews(binding, false)
                            textViewSelectedPack.text = resources.getString(R.string.DisablePack, it.name)
                        }
                    }
                }
                Enums.ClickEvents.DELETE -> {
                    binding.layoutBasic.apply {
                        manageDisableViews(binding, false)
                        textViewSelectedPack.text = resources.getString(R.string.DisablePack, it.name)
                    }
                }
                else -> {

                }
            }
        }

        soldAlongAdapter = SoldAlongAdapter {
            soldAlongAdapter.apply {
                list.removeAt(it)
                notifyItemRemoved(it)
                notifyItemRangeChanged(it, list.size)
            }
        }

        upcAdapter = UPCAdapter {
            upcAdapter.apply {
                list.removeAt(it)
                notifyItemRemoved(it)
                notifyItemRangeChanged(it, list.size)
                val ind = list.indexOfFirst { it.isAuto }
                if (ind < 0) {
                    binding.layoutBasic.apply {
                        buttonAuto.isEnabled = true
                        buttonAuto.isClickable = true
                    }
                }
            }
        }

        suggestionAdapter = AutoCompleteAdapter(this, android.R.layout.simple_spinner_dropdown_item, ArrayList())
        suggestionBarAdapter = AutoCompleteAdapter(this, android.R.layout.simple_spinner_dropdown_item, ArrayList())

        product = intent.getParcelableExtra(Default.PRODUCT) as ProductList?
        if(product == null) product = ProductList()

        dropDownAdapter = DropDownAdapter { commonDropDown, id, name, specificationId ->
            val ind = manageSpecification.indexOfFirst { it.typeId == id }
            if (ind >= 0) {
                manageSpecification[ind].id = commonDropDown.value!!
                manageSpecification[ind].value = commonDropDown.label!!
            } else manageSpecification.add(ManageSpecification(
                id = commonDropDown.value!!,
                value = commonDropDown.label!!,
                typeId = id,
                typeName = name,
                specificationId = specificationId,
                                                              ))
        }
        //editSpecificationId, commonDropDown.label!!, id, name, specificationId

        itemStock = ArrayList()
        priceList = ArrayList()
        soldAlongItem = FavouriteItems()
        barItem = FavouriteItems()
        facilities = ArrayList()
        barItemList = ArrayList()
        itemDetailsList = ArrayList()
        manageSpecification = ArrayList()
        parentId = if (intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        isFromOrder = if (intent.extras?.getBoolean(Default.ORDER) != null) intent.extras?.getBoolean(Default.ORDER)!! else false
        categoryList = ArrayList()
        category = CommonDropDown(0, resources.getString(R.string.lbl_SelectCategory))
        categoryList.add(category)
        categoriesSpinner = ArrayAdapter(this, R.layout.spinner_items, categoryList)
        subCategoryList = ArrayList()
        subCategory = CommonDropDown(0, resources.getString(R.string.lbl_SelectSubCategories))
        subCategoryList.add(subCategory)
        subCategoriesSpinner = ArrayAdapter(this, R.layout.spinner_items, subCategoryList)
        departmentList = ArrayList()
        department = CommonDropDown(0, resources.getString(R.string.lbl_SelectDepartment))
        departmentList.add(department)
        departmentSpinner = ArrayAdapter(this, R.layout.spinner_items, departmentList)
        taxGroupList = ArrayList()
        taxGroup = CommonDropDown(label = resources.getString(R.string.lbl_SelectTax), value = 0)
        taxGroupList.add(taxGroup)
        taxGroupSpinner = ArrayAdapter(this, R.layout.spinner_items, taxGroupList)
        itemTypeList = ArrayList()
        itemTypeSpinner = ArrayAdapter(this, R.layout.spinner_items, itemTypeList)
        brandList = ArrayList()
        brand = CommonDropDown(label = resources.getString(R.string.lbl_SelectBrand), value = 0)
        brandList.add(brand)
        brandSpinner = ArrayAdapter(this, R.layout.spinner_items, brandList)
        uomList = ArrayList()
        uom = CommonDropDown(label = resources.getString(R.string.lbl_SelectUOM), value = 0)
        uomList.add(uom)
        uomSpinner = ArrayAdapter(this, R.layout.spinner_items, uomList)
        attributeList = ArrayList()
        attribute = CommonDropDown(label = resources.getString(R.string.lbl_SelectAttribute), value = 0)
        attributeList.add(attribute)
        attributeSpinner = ArrayAdapter(this, R.layout.spinner_items, attributeList)
    }

    private fun postInitData() {
        binding.apply {

            layoutBasic.recycleViewSizePack.apply {
                adapter = dropDownAdapter
                layoutManager = GridLayoutManager(this@EditProductActivity, 4)
            }
            val param = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.5f)
            param.marginStart = resources.getDimension(com.intuit.sdp.R.dimen._5sdp).toInt()
            layoutBasic.apply {
                textInputSoldAlongSKU.setAdapter(suggestionAdapter)
                textInputSoldAlongSKU.threshold = 0

                textInputBarItemSKU.setAdapter(suggestionBarAdapter)
                textInputBarItemSKU.threshold = 0

                textInputUnitInCase.isEnabled = false
                textInputCaseCost.isEnabled = false
                textInputCasePrice.isEnabled = false
                buttonPrice.isEnabled = false

                textInputUPC.setOnFocusChangeListener { _, _ ->
                    if (!TextUtils.isEmpty(textInputUPC.text.toString().trim())) {
                        if (textInputUPC.text.toString().trim().length < 3) textInputUPC.error = resources.getString(R.string.lbl_UPCValidation)
                        else textInputUPC.error = null
                    } else textInputUPC.error = null
                }

                recycleViewSoldAlong.apply {
                    adapter = soldAlongAdapter
                    layoutManager = LinearLayoutManager(this@EditProductActivity)
                }

                recycleviewCombinations.apply {
                    adapter = itemPackDetailsAdapter
                    layoutManager = GridLayoutManager(this@EditProductActivity, 4)
                }

                recycleViewUPC.apply {
                    adapter = upcAdapter
                    layoutManager = LinearLayoutManager(this@EditProductActivity)
                }

                textInputLayoutUPC.layoutParams = param
                textInputUnitPrice.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        setMarginMarkup(unitCost = if (!textInputUnitCost.text.isNullOrEmpty()) textInputUnitCost.text.toString().toDouble() else 0.00, unitPrice = if (!TextUtils.isEmpty(s)) s.toString().toDouble() else 0.00, buyDown = if (!textInputBuyDown.text.isNullOrEmpty()) textInputBuyDown.text.toString().toDouble() else 0.00)
                    }
                })

                textInputUnitCost.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        setMarginMarkup(unitPrice = if (!textInputUnitPrice.text.isNullOrEmpty()) textInputUnitPrice.text.toString().toDouble() else 0.00, unitCost = if (!TextUtils.isEmpty(s)) s.toString().toDouble() else 0.00, buyDown = if (!textInputBuyDown.text.isNullOrEmpty()) textInputBuyDown.text.toString().toDouble() else 0.00)
                    }
                })

                textInputBuyDown.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        setMarginMarkup(unitCost = if (!textInputUnitCost.text.isNullOrEmpty()) textInputUnitCost.text.toString().toDouble() else 0.00, buyDown = if (!TextUtils.isEmpty(s)) s.toString().toDouble() else 0.00, unitPrice = if (!textInputUnitPrice.text.isNullOrEmpty()) textInputUnitPrice.text.toString().toDouble() else 0.00)
                    }
                })
            }
        }
    }

    //region To load data
    private fun loadData() {
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }

        viewModel.errorMsg.observe(this) {
            showSweetDialog(this, it)
        }

        viewModelDropDown.getUOM().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                uomSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }

        viewModel.getItemDetail(product!!.id!!).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                if (it != null) {
                    productDetail = it
                    binding.layoutBasic.apply {
                        if (it.itemdetails.size > 0) {
                            if (it.itemdetails[0].upcList.size > 0) textInputUPC.setText(if (!TextUtils.isEmpty(it.itemdetails[0].upcList[0].itemUpc)) it.itemdetails[0].upcList[0].itemUpc else "")
                            else textInputUPC.setText("")
                            textInputSKU.setText(it.sku)
                            textInputName.setText(it.name)

                            textInputUnitPrice.setText(Utils.getTwoDecimalValue(if (it.itemdetails[0].price.size > 0) {
                                val ind = it.itemdetails[0].price.indexOfFirst { it.quantity == 1L }
                                if (ind >= 0) {
                                    if (it.itemdetails[0].price[ind].unitPrice!! >= 0.00) it.itemdetails[0].price[ind].unitPrice!! else 0.00
                                } else 0.00
                            } else 0.00))

                            textInputUnitCost.setText(Utils.getTwoDecimalValue(if (it.itemdetails[0].price.size > 0) {
                                val ind = it.itemdetails[0].price.indexOfFirst { it.quantity == 1L }
                                if (ind >= 0) {
                                    if (it.itemdetails[0].price[ind].unitCost!! >= 0.00) it.itemdetails[0].price[ind].unitCost!! else 0.00
                                } else 0.00
                            } else 0.00))

                            textInputMargin.setText(Utils.getTwoDecimalValue(if (it.itemdetails[0].price.size > 0) {
                                val ind = it.itemdetails[0].price.indexOfFirst { it.quantity == 1L }
                                if (ind >= 0) {
                                    if (it.itemdetails[0].price[ind].margin!! >= 0.00) it.itemdetails[0].price[ind].margin!! else 0.00
                                } else 0.00
                            } else 0.00))

                            textInputMarkup.setText(Utils.getTwoDecimalValue(if (it.itemdetails[0].price.size > 0) {
                                val ind = it.itemdetails[0].price.indexOfFirst { it.quantity == 1L }
                                if (ind >= 0) {
                                    if (it.itemdetails[0].price[ind].markup!! >= 0.00) it.itemdetails[0].price[ind].markup!! else 0.00
                                } else 0.00
                            } else 0.00))

                            textInputMinPrice.setText(Utils.getTwoDecimalValue(if (it.itemdetails[0].price.size > 0) {
                                val ind = it.itemdetails[0].price.indexOfFirst { it.quantity == 1L }
                                if (ind >= 0) {
                                    if (it.itemdetails[0].price[ind].minPrice!! >= 0.00) it.itemdetails[0].price[ind].minPrice!! else 0.00
                                } else 0.00
                            } else 0.00))

                            textInputSalePrice.setText(Utils.getTwoDecimalValue(if (it.itemdetails[0].price.size > 0) {
                                val ind = it.itemdetails[0].price.indexOfFirst { it.quantity == 1L }
                                if (ind >= 0) {
                                    if (it.itemdetails[0].price[ind].salesPrice!! >= 0.00) it.itemdetails[0].price[ind].salesPrice!! else 0.00
                                } else 0.00
                            } else 0.00))

                            textInputMSRP.setText(Utils.getTwoDecimalValue(if (it.itemdetails[0].price.size > 0) {
                                val ind = it.itemdetails[0].price.indexOfFirst { it.quantity == 1L }
                                if (ind >= 0) {
                                    if (it.itemdetails[0].price[ind].msrp!! >= 0.00) it.itemdetails[0].price[ind].msrp!! else 0.00
                                } else 0.00
                            } else 0.00))

                            textInputBuyDown.setText(Utils.getTwoDecimalValue(if (it.itemdetails[0].price.size > 0) {
                                val ind = it.itemdetails[0].price.indexOfFirst { it.quantity == 1L }
                                if (ind >= 0) {
                                    if (it.itemdetails[0].price[ind].buyDown!! >= 0.00) it.itemdetails[0].price[ind].buyDown!! else 0.00
                                } else 0.00
                            } else 0.00))

                            val dataList = it.itemdetails[0].stock
                            facilities.clear()
                            for (data in dataList) {
                                facilities.add(Facility(id = data.facilityId, name = data.sFacility, quantity = data.quantity!!.toInt()))
                                itemStock.add(ItemStock(id = data.id, facilityId = data.facilityId, quantity = data.quantity!!.toLong()))
                            }
                            val sum = facilities.sumOf { it.quantity }.toString()
                            textInputOnHand.setText(if (sum.length > 1) sum else "0$sum")

                            for (data in it.itemdetails[0].attribute) {
                                barItemList.add(Attributes(
                                    id = data.id,
                                    itemId = data.itemId,
                                    value = data.value,
                                    uom = data.uom,
                                    specificationId = data.specificationId,
                                    itemStockQuantity = data.itemStockQuantity,
                                    itemName = if (!TextUtils.isEmpty(data.itemName)) data.itemName!! else ""
                                                          ))
                            }

                            textInputMinWarnQty.setText(if (it.itemdetails[0].qtyDetail!!.minWarnQty.toString().length > 1) it.itemdetails[0].qtyDetail!!.minWarnQty.toString() else "0" + it.itemdetails[0].qtyDetail!!.minWarnQty.toString())
                            textInputReOrder.setText(if (it.itemdetails[0].qtyDetail!!.reOrder.toString().length > 1) it.itemdetails[0].qtyDetail!!.reOrder.toString() else "0" + it.itemdetails[0].qtyDetail!!.reOrder.toString())

                            val soldAlongList = ArrayList<SoldAlong>()
                            for (data in it.itemdetails[0].soldAlong) soldAlongList.add(SoldAlong(data.soldAlongId, data.soldAlongQuantity, data.soldAlongItemId, data.specificationId, data.soldAlongItemName!!))

                            soldAlongAdapter.apply {
                                setList(soldAlongList)
                            }

                            upcAdapter.apply {
                                setList(it.itemdetails[0].upcList)
                            }

                            val list = it.itemdetails[0].price
                            for (data in list) priceList.add(ItemPrice(id = data.id, unitPrice = data.unitPrice, unitCost = data.unitCost, minPrice = data.minPrice, buyDown = data.buyDown, msrp = data.msrp, salesPrice = data.salesPrice, margin = data.margin, markup = data.markup, quantity = data.quantity))
                        }

                        viewModelDropDown.getParentCategory().observe(this@EditProductActivity) { cat ->
                            CoroutineScope(Dispatchers.Main).launch {
                                categoriesSpinner.apply {
                                    setDropDownViewResource(R.layout.spinner_dropdown_item)
//                                    category = CommonDropDown(0, resources.getString(R.string.lbl_SelectCategory))
//                                    add(category)
                                    addAll(cat)
                                    val ind = categoryList.indexOfFirst { category -> category.value == it.categoryId }
                                    if (ind >= 0) {
                                        binding.layoutBasic.spinnerCategory.setSelection(ind)
                                        category = categoryList[ind]
                                        manageSubCategories(category.value!!, it.subCategory!!)
                                    }
                                    notifyDataSetChanged()
                                }
                            }
                        }

                        viewModelDropDown.getDepartment().observe(this@EditProductActivity) { depart->
                            CoroutineScope(Dispatchers.Main).launch {
                                departmentSpinner.apply {
                                    setDropDownViewResource(R.layout.spinner_dropdown_item)
//                                    department = CommonDropDown(0, resources.getString(R.string.lbl_SelectDepartment))
//                                    add(department)
                                    addAll(depart)
                                    val ind = departmentList.indexOfFirst { department -> department.value == it.departmentId }
                                    if (ind >= 0) {
                                        binding.layoutBasic.spinnerDepartment.setSelection(ind)
                                        department = departmentList[ind]
                                    }
                                    notifyDataSetChanged()
                                }
                            }
                        }

                        viewModelDropDown.fetchGroupsById(Default.TYPE_TAX).observe(this@EditProductActivity) { tax ->
                            CoroutineScope(Dispatchers.Main).launch {
                                taxGroupSpinner.apply {
                                    setDropDownViewResource(R.layout.spinner_dropdown_item)
//                                    taxGroup = CommonDropDown(label = resources.getString(R.string.lbl_SelectTax), value = 0)
//                                    add(taxGroup)
                                    addAll(tax)
                                    val ind = taxGroupList.indexOfFirst { taxGroup -> taxGroup.value == it.taxGroupId }
                                    if (ind >= 0) {
                                        binding.layoutBasic.spinnerTaxGroup.setSelection(ind)
                                        taxGroup = taxGroupList[ind]
                                        fetchTax()
                                    }
                                    notifyDataSetChanged()
                                }
                            }
                        }


                        itemTypeSpinner.apply {
                            setDropDownViewResource(R.layout.spinner_dropdown_item)
                            itemType = CommonDropDown(Default.STANDARD, resources.getString(R.string.lbl_Standard))
                            add(itemType)
                            add(CommonDropDown(Default.MULTI_PACK, resources.getString(R.string.lbl_MultiPack)))
                            add(CommonDropDown(Default.LOT_MATRIX, resources.getString(R.string.lbl_LotMatrix)))
                            add(CommonDropDown(Default.BAR, resources.getString(R.string.lbl_BarItem)))
                            val ind = itemTypeList.indexOfFirst { type -> type.value!! == it.type }
                            if (ind >= 0) {
                                try {
                                    spinnerItemType.setSelection(ind)
                                    itemType = itemTypeList[ind]
                                    when (itemType.value) {
                                        Default.MULTI_PACK -> {
                                            linearLayoutLotMatrix.visibility = View.GONE
                                            imageViewAddPack.visibility = View.VISIBLE
                                            packCombinations(it)
                                        }
                                        Default.LOT_MATRIX -> {
                                            imageViewAddPack.visibility = View.VISIBLE
                                            linearLayoutLotMatrix.visibility = View.VISIBLE
                                            linearLayoutBar.visibility = View.GONE
                                            packCombinations(it)

                                            viewModelDropDown.getSpecificationType(Default.ATTRIBUTE_TYPE).observe(this@EditProductActivity) { dd ->
                                                attributeSpinner.apply {
                                                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                                                    clear()
                                                    attribute = CommonDropDown(0, resources.getString(R.string.lbl_SelectAttribute))
                                                    add(attribute)
                                                    addAll(dd)
                                                    notifyDataSetChanged()
                                                    manageAttributeDropDown()
                                                }
                                            }
                                        }
                                        Default.BAR -> {
                                            linearLayoutLotMatrix.visibility = View.GONE
                                            imageViewAddPack.visibility = View.INVISIBLE
                                            itemPackDetailsAdapter.apply {
                                                list.clear()
                                                setList(list)
                                            }
                                            textViewSelectedPack.visibility = View.GONE
                                            linearLayoutBar.visibility = View.VISIBLE
                                            //resetSpecification()
                                            manageDisableViews(binding, true)
                                        }
                                        Default.STANDARD -> {
                                            linearLayoutLotMatrix.visibility = View.GONE
                                            linearLayoutBar.visibility = View.GONE
                                            imageViewAddPack.visibility = View.INVISIBLE
                                            itemPackDetailsAdapter.apply {
                                                list.clear()
                                                setList(list)
                                            }
                                            textViewSelectedPack.visibility = View.GONE
                                            //resetSpecification()
                                            manageDisableViews(binding, true)
                                        }
                                    }
                                } catch (ex: Exception) {
                                    Utils.printAndWriteException(ex)
                                }
                            }
                            notifyDataSetChanged()
                        }

                        checkBoxPrice.isChecked = if (it.promptForPrice != null) it.promptForPrice!! else false
                        managePrice(checkBoxPrice.isChecked)
                        checkBoxQty.isChecked = if (it.promptForQuantity != null) it.promptForQuantity!! else false
                        checkBoxNonPlu.isChecked = if (it.nonPluItem != null) it.nonPluItem!! else false
                        checkBoxBuyAsCase.isChecked = if (it.buyCase != null) it.buyCase!! else false
                        checkBoxInActive.isChecked = if (it.status != null) it.status == Default.INACTIVE else false
                        //manageQuantity(checkBoxQty.isChecked)

                        textInputUnitInCase.isEnabled = checkBoxBuyAsCase.isChecked
                        textInputCaseCost.isEnabled = checkBoxBuyAsCase.isChecked
                        textInputCasePrice.isEnabled = checkBoxBuyAsCase.isChecked
                        buttonPrice.isEnabled = checkBoxBuyAsCase.isChecked

                        checkBoxNonStock.isChecked = if (it.nonStockItem != null) it.nonStockItem!! else false
                        checkBoxNonDiscount.isChecked = if (it.nonDiscountable != null) it.nonDiscountable!! else false
                        checkBoxWeightItem.isChecked = if (it.weightedItem != null) it.weightedItem!! else false
                        checkBoxWICCheck.isChecked = if (it.acceptWiccheck != null) it.acceptWiccheck!! else false
                        checkBoxNonRevenue.isChecked = if (it.nonRevenue != null) it.nonRevenue!! else false
                        checkBoxDepositItem.isChecked = if (it.depositItem != null) it.depositItem!! else false
                        checkBoxWebItem.isChecked = if (it.webItem != null) it.webItem!! else false
                        checkBoxCountWithNoDisc.isChecked = if (it.countWithNoDisc != null) it.countWithNoDisc!! else false
                        checkBoxNonCountable.isChecked = if (it.nonCountable != null) it.nonCountable!! else false
                        checkBoxFoodStamp.isChecked = if (it.acceptFoodStamp != null) it.acceptFoodStamp!! else false
                        checkBoxReturnItem.isChecked = if (it.returnItem != null) it.returnItem!! else false
                    }


                    if (itemType.value != Default.LOT_MATRIX) {
                        viewModelDropDown.getSpecificationType(Default.SPECIFICATION_TYPE).observe(this@EditProductActivity) { dd ->
                            for (i in dd.indices) setupMatrix(dd[i], it)
                        }
                    }

                    viewModelDropDown.getBrandDropDown().observe(this@EditProductActivity) { brandItem ->
                        CoroutineScope(Dispatchers.Main).launch {
                            brandSpinner.apply {
                                setDropDownViewResource(R.layout.spinner_dropdown_item)
                                addAll(brandItem)
                                val ind = brandList.indexOfFirst { brand -> brand.value == it.brandId }
                                if (ind >= 0) {
                                    binding.layoutBasic.spinnerBrand.setSelection(ind)
                                    brand = brandList[ind]
                                }
                                notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }


    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewBack.clickWithDebounce {
                val intent = Intent(this@EditProductActivity, ProductListActivity::class.java)
                intent.putExtra(Default.PARENT_ID, parentId)
                intent.putExtra(Default.ORDER, isFromOrder)
                openActivity(intent)
            }

            layoutToolbar.imageViewAction.clickWithDebounce {
                layoutBasic.apply {
                    if (!TextUtils.isEmpty(textInputName.text.toString().trim())) {
                        val quantity = QuantityDetail(if (!TextUtils.isEmpty(textInputMinWarnQty.text.toString())) textInputMinWarnQty.text.toString().toInt() else 0, if (!TextUtils.isEmpty(textInputReOrder.text.toString())) textInputReOrder.text.toString().toInt() else 0)
                        when (itemType.value) {
                            Default.STANDARD -> {
                                barItemList.clear()
                                if(productDetail != null){
                                    if(productDetail!!.itemdetails.size > 0) quantity.id = productDetail!!.itemdetails[0].qtyDetail!!.id
                                }
                                updateStandardBar(quantity)
                            }
                            Default.BAR -> {
                                if(productDetail != null){
                                    if(productDetail!!.itemdetails.size > 0) quantity.id = productDetail!!.itemdetails[0].qtyDetail!!.id
                                }
                                updateStandardBar(quantity)
                            }
                            Default.MULTI_PACK -> updateMultiPackMatrix(quantity)
                            Default.LOT_MATRIX -> updateMultiPackMatrix(quantity)
                        }
                    } else showSweetDialog(this@EditProductActivity, resources.getString(R.string.lbl_NameMissing))
                }
            }

            layoutBasic.apply {

                textInputSoldAlongSKU.setOnItemClickListener { parent, _, position, _ ->
                    val details = parent.getItemAtPosition(position) as FavouriteItems
                    soldAlongItem = details
                    textInputSoldAlongName.setText(details.name!!)
                    textInputSoldAlongQty.setText("1")
                    textInputSoldAlongSKU.text.clear()
                }

                textInputBarItemSKU.setOnItemClickListener { parent, _, position, _ ->
                    val details = parent.getItemAtPosition(position) as FavouriteItems
                    barItem = details
                    textInputBarItemName.setText(details.name!!)
                    textInputBarItemQty.setText("1")
                    textInputBarItemSKU.text.clear()
                }

                checkBoxBuyAsCase.setOnCheckedChangeListener { _, isChecked ->
                    textInputUnitInCase.isEnabled = isChecked
                    textInputCaseCost.isEnabled = isChecked
                    textInputCasePrice.isEnabled = isChecked
                    buttonPrice.isEnabled = isChecked
                }

                buttonPrice.clickWithDebounce {
                    val casePrice = if (!TextUtils.isEmpty(textInputCasePrice.text.toString().trim())) textInputCasePrice.text.toString().toDouble() else 0.00
                    val caseCost = if (!TextUtils.isEmpty(textInputCaseCost.text.toString().trim())) textInputCaseCost.text.toString().toDouble() else 0.00
                    val unitInCase = if (!TextUtils.isEmpty(textInputUnitInCase.text.toString().trim())) textInputUnitInCase.text.toString().toLong() else 0

                    if (casePrice > 0.00 && caseCost > 0.00 && unitInCase > 0.00 && checkBoxBuyAsCase.isChecked) {
                        val price = ItemPrice(id = 0, unitPrice = casePrice,
                            unitCost = caseCost,
                            margin = Utils.calculateMargin(casePrice, caseCost, 0.00),
                            markup = Utils.calculateMarkUp(casePrice, caseCost, 0.00),
                            quantity = unitInCase)

                        priceList.add(price)
                        textInputCasePrice.text!!.clear()
                        textInputCaseCost.text!!.clear()
                        textInputUnitInCase.text!!.clear()
                        checkBoxBuyAsCase.isChecked = false
                    }
                }

                buttonAddSoldAlong.clickWithDebounce {
                    if (soldAlongItem.id != null) {
                        soldAlongAdapter.apply {
                            val index = list.indexOfFirst { it.soldAlongItemId == soldAlongItem.id }
                            if (index >= 0)
                                list[index].soldalongquantity = list[index].soldalongquantity?.plus(if (!TextUtils.isEmpty(textInputSoldAlongQty.text.toString())) textInputSoldAlongQty.text.toString().toInt() else 0)
                            else addSoldAlong(SoldAlong(soldalongquantity = if (!TextUtils.isEmpty(textInputSoldAlongQty.text.toString())) textInputSoldAlongQty.text.toString().toInt() else 0, soldAlongItemId = soldAlongItem.id, name = textInputSoldAlongName.text.toString().trim(), specificationId = soldAlongItem.specificationId!!))
                        }
                        soldAlongItem = FavouriteItems()
                        textInputSoldAlongQty.text!!.clear()
                        textInputSoldAlongName.text!!.clear()
                    }
                }

                buttonAddBarItem.clickWithDebounce {
                    if (barItem.id != null && uom.value!! > 0) {
                        barItemList.add(Attributes(
                            id = 0,
                            itemId = barItem.id,
                            value = if (!TextUtils.isEmpty(textInputBarItemQty.text.toString())) textInputBarItemQty.text.toString().toInt() else 0,
                            uom = uom.label!!,
                            specificationId = barItem.specificationId,
                            itemStockQuantity = 0,
                            itemName = if (!TextUtils.isEmpty(textInputBarItemName.text.toString())) textInputBarItemName.text.toString().trim() else ""
                                                  ))
                        barItem = FavouriteItems()
                        spinnerUOM.setSelection(0)
                        uomSpinner.notifyDataSetChanged()
                        textInputBarItemQty.text!!.clear()
                        textInputBarItemName.text!!.clear()
                    } else showSweetDialog(this@EditProductActivity, resources.getString(R.string.Validation, resources.getString(R.string.hint_UOMType)))
                }

                buttonViewBarItem.clickWithDebounce {
                    manageBarItem()
                }

                buttonAuto.clickWithDebounce {
                    upcAdapter.apply {
                        addUPC(ItemUPC(Utils.getAutoGeneratedUPCBarcode(), true))
                    }
                    buttonAuto.isEnabled = false
                    buttonAuto.isClickable = false
                }

                buttonAdd.clickWithDebounce {
                    addUPC()
                }

                textInputSoldAlongSKU.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (!s.isNullOrEmpty()) fetchSuggestions(s.toString())
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })

                textInputBarItemSKU.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (!s.isNullOrEmpty()) fetchSuggestions(s.toString(), true)
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })

                spinnerDepartment.apply {
                    adapter = departmentSpinner
                    var previousSelectedItem: CommonDropDown? = null
                    setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                        override fun onSpinnerOpened(spinner: Spinner?) {
                            previousSelectedItem = spinner?.selectedItem as? CommonDropDown
                        }

                        override fun onSpinnerClosed(spinner: Spinner?) {
                            department = spinner?.selectedItem as CommonDropDown
                            if (department != previousSelectedItem) {
                                if (department.value!! > 0) {
                                    viewModelDropDown.getDepartmentDetails(department.value!!).observe(this@EditProductActivity) {
                                        if (it != null) {
                                            binding.apply {
                                                layoutBasic.apply {
                                                    checkBoxFoodStamp.isChecked = if (it.isFoodStamp != null) it.isFoodStamp!! else false
                                                    checkBoxPrice.isChecked = if (it.showInOpenPrice != null) it.showInOpenPrice!! else false
                                                    managePrice(checkBoxPrice.isChecked)
                                                    checkBoxNonRevenue.isChecked = if (it.isNonRevenue != null) it.isNonRevenue!! else false
                                                    checkBoxNonDiscount.isChecked = if (it.nonDiscountable != null) it.nonDiscountable!! else false
                                                    checkBoxNonCountable.isChecked = if (it.nonCountable != null) it.nonCountable!! else false
                                                    checkBoxWeightItem.isChecked = if (it.weightItemFlag != null) it.weightItemFlag!! else false
                                                    checkBoxWICCheck.isChecked = if (it.allowWicCheck != null) it.allowWicCheck!! else false
                                                    checkBoxWebItem.isChecked = if (it.webItemFlag != null) it.webItemFlag!! else false
                                                    var ind = taxGroupList.indexOfFirst { taxGroup -> taxGroup.value == it.taxGroupId }
                                                    if (ind >= 0) {
                                                        spinnerTaxGroup.setSelection(ind)
                                                        taxGroup = taxGroupList[ind]
                                                        taxGroupSpinner.notifyDataSetChanged()
                                                    }
                                                    ind = brandList.indexOfFirst { brand -> brand.value == it.brandId }
                                                    if (ind >= 0) {
                                                        spinnerBrand.setSelection(ind)
                                                        taxGroup = taxGroupList[ind]
                                                        brandSpinner.notifyDataSetChanged()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            previousSelectedItem = null
                        }
                    })
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
                            previousSelectedItem = null
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

                spinnerUOM.apply {
                    adapter = uomSpinner
                    setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                        override fun onSpinnerOpened(spinner: Spinner?) {

                        }

                        override fun onSpinnerClosed(spinner: Spinner?) {
                            uom = spinner?.selectedItem as CommonDropDown
                        }
                    })
                }

                spinnerTaxGroup.apply {
                    adapter = taxGroupSpinner
                    var previousSelectedItem: CommonDropDown? = null
                    setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                        override fun onSpinnerOpened(spinner: Spinner?) {
                            previousSelectedItem = spinner?.selectedItem as? CommonDropDown
                        }

                        override fun onSpinnerClosed(spinner: Spinner?) {
                            taxGroup = spinner?.selectedItem as CommonDropDown
                            if (taxGroup != previousSelectedItem) {
                                if (taxGroup.value!! > 0) fetchTax()
                                else textViewTax.text = ""
                            }
                            previousSelectedItem = null
                        }
                    })
                }

                spinnerItemType.apply {
                    adapter = itemTypeSpinner
                    var previousSelectedItem: CommonDropDown? = null
                    setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                        override fun onSpinnerOpened(spinner: Spinner?) {
                            previousSelectedItem = spinner?.selectedItem as? CommonDropDown
                        }

                        override fun onSpinnerClosed(spinner: Spinner?) {
                            itemType = spinner?.selectedItem as CommonDropDown
                            if (itemType != previousSelectedItem) {
                                val ft = supportFragmentManager.beginTransaction()
                                val dialogFragment = DeleteAlertPopupDialog(resources.getString(R.string.ChangeItemTypePermission, previousSelectedItem!!.label, itemType.label), 0, changeItemType = true)
                                val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(DeleteAlertPopupDialog::class.java.name)
                                if (prevFragment != null) return
                                dialogFragment.isCancelable = false
                                dialogFragment.show(ft, DeleteAlertPopupDialog::class.java.name)
                                dialogFragment.setListener(object : DeleteAlertPopupDialog.OnButtonClickListener {
                                    override fun onButtonClickListener(typeButton: Enums.ClickEvents) {
                                        when (typeButton) {
                                            Enums.ClickEvents.DELETE -> {
                                                when (itemType.value) {
                                                    Default.MULTI_PACK -> {
                                                        imageViewAddPack.visibility = View.VISIBLE
                                                        linearLayoutBar.visibility = View.GONE
                                                        linearLayoutLotMatrix.visibility = View.GONE
                                                        resetSpecification()
                                                        packCombinations(productDetail!!)
                                                    }
                                                    Default.LOT_MATRIX -> {
                                                        imageViewAddPack.visibility = View.VISIBLE
                                                        linearLayoutLotMatrix.visibility = View.VISIBLE
                                                        linearLayoutBar.visibility = View.GONE
                                                        packCombinations(productDetail!!)
                                                        viewModelDropDown.getSpecificationType(Default.ATTRIBUTE_TYPE).observe(this@EditProductActivity) {
                                                            attributeSpinner.apply {
                                                                setDropDownViewResource(R.layout.spinner_dropdown_item)
                                                                clear()
                                                                attribute = CommonDropDown(0, resources.getString(R.string.lbl_SelectAttribute))
                                                                add(attribute)
                                                                addAll(it)
                                                                notifyDataSetChanged()
                                                                dropDownAdapter.apply {
                                                                    list.clear()
                                                                    notifyDataSetChanged()
                                                                }
                                                                manageAttributeDropDown()
                                                            }
                                                        }
                                                    }
                                                    Default.BAR -> {
                                                        linearLayoutLotMatrix.visibility = View.GONE
                                                        imageViewAddPack.visibility = View.INVISIBLE
                                                        itemPackDetailsAdapter.apply {
                                                            list.clear()
                                                            setList(list)
                                                        }
                                                        textViewSelectedPack.visibility = View.GONE
                                                        linearLayoutBar.visibility = View.VISIBLE
                                                        resetSpecification()
                                                        manageDisableViews(binding, true)
                                                    }
                                                    Default.STANDARD -> {
                                                        linearLayoutLotMatrix.visibility = View.GONE
                                                        linearLayoutBar.visibility = View.GONE
                                                        imageViewAddPack.visibility = View.INVISIBLE
                                                        itemPackDetailsAdapter.apply {
                                                            list.clear()
                                                            setList(list)
                                                        }
                                                        textViewSelectedPack.visibility = View.GONE
                                                        resetSpecification()
                                                        manageDisableViews(binding, true)
                                                    }
                                                }
                                            }
                                            Enums.ClickEvents.CANCEL -> {
                                                val ind = itemTypeList.indexOf(previousSelectedItem)
                                                if (ind >= 0) {
                                                    itemType = itemTypeList[ind]
                                                    spinnerItemType.setSelection(0)
                                                    itemTypeSpinner.notifyDataSetChanged()
                                                }
                                            }
                                            else -> {}
                                        }
                                        previousSelectedItem = null
                                    }
                                })
                            }
                        }
                    })
                }

                spinnerBrand.apply {
                    adapter = brandSpinner
                    var previousSelectedItem: CommonDropDown? = null
                    setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                        override fun onSpinnerOpened(spinner: Spinner?) {
                            previousSelectedItem = spinner?.selectedItem as? CommonDropDown
                        }

                        override fun onSpinnerClosed(spinner: Spinner?) {
                            brand = spinner?.selectedItem as CommonDropDown
                            if (brand != previousSelectedItem) {
                                if (brand.value!! > 0) {
                                    viewModelDropDown.getBrandDetails(brand.value!!).observe(this@EditProductActivity) {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            if (it != null) {
                                                when {
                                                    it.rjrt == null || it.pmUsa == null -> brandValidation(resources.getString(R.string.BrandValidation, resources.getString(R.string.lbl_DepartmentCategory)))
                                                    category.value!! > 0 && department.value!! > 0 -> {}
                                                    category.value!! > 0 -> brandValidation(resources.getString(R.string.BrandValidation, resources.getString(R.string.Menu_Department)))
                                                    department.value!! > 0 -> brandValidation(resources.getString(R.string.BrandValidation, resources.getString(R.string.Menu_Category)))
                                                    else -> brandValidation(resources.getString(R.string.BrandValidation, resources.getString(R.string.lbl_DepartmentCategory)))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    })
                }

                spinnerAttributes.apply {
                    adapter = attributeSpinner
                    setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                        override fun onSpinnerOpened(spinner: Spinner?) {

                        }

                        override fun onSpinnerClosed(spinner: Spinner?) {
                            attribute = spinner?.selectedItem as CommonDropDown
                        }
                    })
                }

                buttonEnter.clickWithDebounce {
                    if (!checkBoxQty.isChecked) manageStocks()
                    else return@clickWithDebounce
                }

                buttonStockView.clickWithDebounce {
                    if (facilities.size > 0) viewStocks(facilities, true)
                    else return@clickWithDebounce
                }

                buttonPriceView.clickWithDebounce {
                    if (priceList.size > 0) viewPrices(priceList)
                    else return@clickWithDebounce
                }

                buttonAddAttribute.clickWithDebounce {
                    if (attribute.value!! > 0) {
                        var index = -1
                        if (dropDownAdapter.list.isNotEmpty()) index = dropDownAdapter.list.indexOfFirst { it.list[0].value == attribute.value }
                        if (index < 0) {
                            viewModel.getSizePackDropDown(attribute.value!!).observe(this@EditProductActivity) { details ->
                                details.add(0, CommonDropDown(attribute.value, attribute.label))
                                CoroutineScope(Dispatchers.Main).launch {
                                    dropDownAdapter.apply {
                                        setList(DynamicDropDown(details))
                                    }
                                    spinnerAttributes.setSelection(0)
                                    attributeSpinner.notifyDataSetChanged()
                                }
                            }
                        }
                    } else showSweetDialog(this@EditProductActivity, resources.getString(R.string.Validation, resources.getString(R.string.lbl_SelectAttribute)))
                }

                checkBoxPrice.setOnCheckedChangeListener { _, isChecked ->
                    managePrice(isChecked)
                }

                checkBoxQty.setOnCheckedChangeListener { _, isChecked ->
                    manageQuantity(isChecked)
                }

                imageViewAddPack.clickWithDebounce {
                    if (manageSpecification.size > 0) {
                        itemPackDetailsAdapter.apply {
                            val specificationName = manageSpecification.joinToString(separator = ", ") { "${it.value}" }
                            val ind = list.indexOfFirst { it.name == specificationName }
                            if (ind < 0) {
                                val listSpecification = ArrayList<SpecificationDetails>()
                                for (data in manageSpecification) {
                                    if (data.id != data.typeId && data.value!!.trim() != data.typeName!!.trim())
                                        listSpecification.add(SpecificationDetails(id = data.id, value = data.value))
                                }

                                val qtyDetail: QuantityDetail
                                val itemPriceList: ArrayList<ItemPrice>
                                val stocks: ArrayList<ItemStock>
                                val upc: ArrayList<ItemUPC>
                                val along: ArrayList<SoldAlong>

                                if (list.isNotEmpty()) {
                                    qtyDetail = QuantityDetail(0, 0)
                                    itemPriceList = ArrayList()
                                    itemPriceList.add(ItemPrice(unitPrice = 0.00, unitCost = 0.00, minPrice = 0.00, buyDown = 0.00, msrp = 0.00, salesPrice = 0.00, margin = 0.00, markup = 0.00, quantity = 1))
                                    stocks = ArrayList()
                                    upc = ArrayList()
                                    along = ArrayList()
                                } else {
                                    priceList.add(ItemPrice(unitPrice = if (!TextUtils.isEmpty(textInputUnitPrice.text.toString().trim())) textInputUnitPrice.text.toString().trim().toDouble() else 0.00, unitCost = if (!TextUtils.isEmpty(textInputUnitCost.text.toString().trim())) textInputUnitCost.text.toString().trim().toDouble() else 0.00, minPrice = if (!TextUtils.isEmpty(textInputMinPrice.text.toString().trim())) textInputMinPrice.text.toString().trim().toDouble() else 0.00, buyDown = if (!TextUtils.isEmpty(textInputBuyDown.text.toString().trim())) textInputBuyDown.text.toString().trim().toDouble() else 0.00, msrp = if (!TextUtils.isEmpty(textInputMSRP.text.toString().trim())) textInputMSRP.text.toString().trim().toDouble() else 0.00, salesPrice = if (!TextUtils.isEmpty(textInputSalePrice.text.toString().trim())) textInputSalePrice.text.toString().trim().toDouble() else 0.00, margin = if (!TextUtils.isEmpty(textInputMargin.text.toString().trim())) textInputMargin.text.toString().trim().toDouble() else 0.00, markup = if (!TextUtils.isEmpty(textInputMarkup.text.toString().trim())) textInputMarkup.text.toString().trim().toDouble() else 0.00, quantity = 1))

                                    qtyDetail = QuantityDetail(if (!TextUtils.isEmpty(textInputMinWarnQty.text.toString())) textInputMinWarnQty.text.toString().toInt() else 0, if (!TextUtils.isEmpty(textInputReOrder.text.toString())) textInputReOrder.text.toString().toInt() else 0)

                                    itemPriceList = ArrayList(priceList)
                                    stocks = ArrayList(itemStock)
                                    upc = ArrayList(upcAdapter.list)
                                    along = ArrayList(soldAlongAdapter.list)
                                }
                                val details = ItemDetails(specification = listSpecification, attributes = ArrayList(), price = itemPriceList, stock = stocks, upcList = upc, soldAlong = along, qtyDetail = qtyDetail)
                                managePacks(ItemDetailsPack(specificationName, details))
                            }
                        }
                    } else return@clickWithDebounce
                }
            }
        }
    } //endregion

    //region To manage validation for brand
    private fun brandValidation(msg: String) {
        brand = CommonDropDown(label = resources.getString(R.string.lbl_SelectBrand), value = 0)
        binding.layoutBasic.spinnerBrand.setSelection(0)
        showSweetDialog(this@EditProductActivity, msg)
    }
    //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        val intent = Intent(this, ProductListActivity::class.java)
        intent.putExtra(Default.PARENT_ID, parentId)
        intent.putExtra(Default.ORDER, isFromOrder)
        openActivity(intent)
        onBackPressedDispatcher.onBackPressed()
    }

    private fun setMarginMarkup(unitCost: Double, unitPrice: Double, buyDown: Double) {
        binding.apply {
            layoutBasic.apply {
                textInputMargin.setText(Utils.getTwoDecimalValue(Utils.calculateMargin(unitPrice, unitCost, buyDown)))
                textInputMarkup.setText(Utils.getTwoDecimalValue(Utils.calculateMarkUp(unitPrice, unitCost, buyDown)))
            }
        }
    }

    //region To manage stocks
    private fun manageStocks() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = FacilityQuantityPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(FacilityQuantityPopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putBoolean(Default.EDIT, true)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, FacilityQuantityPopupDialog::class.java.name)
        dialogFragment.setListener(object : FacilityQuantityPopupDialog.OnButtonClickListener {

            override fun onButtonClickListener(typeButton: Enums.ClickEvents, list: ArrayList<Facility>) {
                when (typeButton) {
                    Enums.ClickEvents.CANCEL -> {
                        if (dialogFragment.isVisible) dialogFragment.dismiss()
                    }

                    Enums.ClickEvents.UPDATE -> {
                        if (dialogFragment.isVisible) dialogFragment.dismiss()
                        if (list.size > 0) {
                            binding.apply {
                                CoroutineScope(Dispatchers.Main).launch {
                                    val sum = list.sumOf { it.quantity }.toString()
                                    layoutBasic.textInputOnHand.setText(if (sum.length > 1) sum else "0$sum")
                                    facilities.clear()
                                    facilities.addAll(list)
                                    for (data in list) {
                                        val ind = itemStock.indexOfFirst { it.facilityId == data.id }
                                        if (ind >= 0) itemStock[ind].quantity = data.quantity.toLong()
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                    }
                }
            }
        })
    }
    //endregion

    //region To fetch suggestions
    private fun fetchSuggestions(str: String, isFromBar: Boolean = false) {
        viewModelOrder.fetchAutoCompleteItems(str).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                if (isFromBar) {
                    suggestionBarAdapter.apply {
                        clear()
                        addAll(it)
                        notifyDataSetChanged()
                        binding.layoutBasic.textInputBarItemSKU.showDropDown()
                    }
                } else {
                    suggestionAdapter.apply {
                        clear()
                        addAll(it)
                        notifyDataSetChanged()
                        binding.layoutBasic.textInputSoldAlongSKU.showDropDown()
                    }
                }
            }
        }
    }
    //endregion

    //region To view prices
    private fun viewPrices(prices: ArrayList<ItemPrice>) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = ItemPriceListPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(ItemPriceListPopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putSerializable(Default.PRICE, prices)
        bundle.putBoolean(Default.EDIT, true)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, ItemPriceListPopupDialog::class.java.name)
        dialogFragment.setListener(object : ItemPriceListPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, list: ArrayList<ItemPrice>) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                if (list.size > 0) priceList = ArrayList(list)
            }
        })
    }
    //endregion


    //region To update product
    private fun updateValidationProduct() {
        binding.apply {
            layoutBasic.apply {
                var priceValidation = false
                for (data in itemDetailsList) {
                    val index = data.price.indexOfFirst { it.quantity == 1L }
                    if (index >= 0) priceValidation = data.price[index].unitPrice == 0.00 && data.price[index].unitCost == 0.00
                    if (priceValidation) break
                }

                if (priceValidation) priceConfirmation()
                else {
                    for (data in itemDetailsList) {
                        val index = data.price.indexOfFirst { it.quantity == 1L }
                        if (index >= 0) priceValidation = data.price[index].unitPrice!! > data.price[index].unitCost!!
                        if (!priceValidation) break
                    }
                    updateProduct(priceValidation)
                }
            }
        }
    }

    private fun priceConfirmation() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = PriceAlertPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(PriceAlertPopupDialog::class.java.name)
        if (prevFragment != null) return
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, PriceAlertPopupDialog::class.java.name)
        dialogFragment.setListener(object : PriceAlertPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                when (typeButton) {
                    Enums.ClickEvents.SAVE -> updateProduct(true)
                    else -> {}
                }
            }
        })
    }

    private fun updateProduct(priceValidation: Boolean) {
        binding.apply {
            layoutBasic.apply {
                if (priceValidation) {
                    val req = ProductInsertRequest(id = productDetail!!.id, itemGuid = productDetail!!.itemGuid, name = textInputName.text.toString().trim(), sku = productDetail!!.sku, model = productDetail!!.model, type = itemType.value, description = productDetail!!.description, departmentId = department.value, brandId = brand.value, categoryId = if (subCategory.value == 0) category.value else subCategory.value, vintage = productDetail!!.vintage, unitType = productDetail!!.unitType, taxGroupId = taxGroup.value, isShortcut = productDetail!!.isShortcut, buyCase = checkBoxBuyAsCase.isChecked, nonStockItem = checkBoxNonStock.isChecked,
                        promptForPrice = checkBoxPrice.isChecked, promptForQuantity = checkBoxQty.isChecked, status = if(checkBoxInActive.isChecked) Default.INACTIVE else Default.ACTIVE,
                        nonDiscountable = checkBoxNonDiscount.isChecked, countWithNoDisc = checkBoxCountWithNoDisc.isChecked, returnItem = checkBoxReturnItem.isChecked, acceptFoodStamp = checkBoxFoodStamp.isChecked,
                        depositItem = checkBoxDepositItem.isChecked, acceptWiccheck = checkBoxWICCheck.isChecked,
                        nonRevenue = checkBoxNonRevenue.isChecked, webItem = checkBoxWebItem.isChecked,
                        nonCountable = checkBoxNonCountable.isChecked, weightedItem = checkBoxWeightItem.isChecked, nonPluItem = checkBoxNonPlu.isChecked,
                        itemdetails = itemDetailsList)
                    viewModel.updateProduct(req).observe(this@EditProductActivity) {
                        if (it) {
                            val intent = Intent(this@EditProductActivity, ProductListActivity::class.java)
                            intent.putExtra(Default.PARENT_ID, parentId)
                            intent.putExtra(Default.ORDER, isFromOrder)
                            openActivity(intent)
                        }
                    }
                } else showSweetDialog(this@EditProductActivity, resources.getString(R.string.lbl_UnitPriceValidation))
            }
        }
    }
    //endregion

    //region To manage prompt price/quantity check box
    private fun managePrice(isChecked: Boolean) {
        binding.layoutBasic.apply {
            textInputUnitCost.isEnabled = !isChecked
            textInputUnitPrice.isEnabled = !isChecked
            textInputSalePrice.isEnabled = !isChecked
            textInputMSRP.isEnabled = !isChecked
            textInputBuyDown.isEnabled = !isChecked
            textInputMinPrice.isEnabled = !isChecked
        }
    }

    private fun manageQuantity(isChecked: Boolean) {
        binding.layoutBasic.apply {
            textInputOnHand.isEnabled = !isChecked
            textInputUnitInCase.isEnabled = !isChecked
            textInputCaseCost.isEnabled = !isChecked
            textInputCasePrice.isEnabled = !isChecked
        }
    }
    //endregion

    //region To fetch tax/es from taxGroup
    private fun fetchTax() {
        viewModelDropDown.fetchTaxesFromGroup(taxGroup.value!!).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                if (it.isNotEmpty()) {
                    val taxList = it.joinToString(separator = ", ") { tax -> "${tax.className} (${Utils.getTwoDecimalValue(tax.rateValue!!)}%)" }
                    binding.apply {
                        layoutBasic.apply {
                            textViewTax.apply {
                                text = resources.getString(R.string.Menu_Tax) + taxList
                            }
                        }
                    }
                }
            }
        }
    }
    //endregion

    //region To add UPC manually
    private fun addUPC() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = UPCPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(UPCPopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putBoolean(Default.EDIT, true)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, UPCPopupDialog::class.java.name)
        dialogFragment.setListener(object : UPCPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, upc: String) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                when (typeButton) {
                    Enums.ClickEvents.SAVE -> {
                        upcAdapter.apply {
                            addUPC(ItemUPC(upc, false))
                        }
                    }
                    else -> {}
                }
            }
        })
    }
    //endregion

    //region To manage packs
    private fun managePacks(itemDetailsPack: ItemDetailsPack) {
        itemPackDetailsAdapter.apply {
            if (list.isEmpty()) {
                binding.layoutBasic.textViewSelectedPack.apply {
                    visibility = View.VISIBLE
                    text = "${itemDetailsPack.name}"
                }
            }
            addData(itemDetailsPack)
        }
    }
    //endregion

    //region To manage bar item
    private fun manageBarItem() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = BarItemPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(BarItemPopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putParcelableArrayList(Default.BAR_ITEM, barItemList)
        bundle.putBoolean(Default.EDIT, true)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, BarItemPopupDialog::class.java.name)
        dialogFragment.setListener(object : BarItemPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, items: ArrayList<Attributes>) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                barItemList = ArrayList(items)
            }
        })
    }
    //endregion

    //region To manage standard and bar
    private fun updateStandardBar(quantity: QuantityDetail) {
        itemDetailsList.clear()

        //After talking with the API team, we need to deliver the specifications to them in the correct order, for example, size first, then pack.
        val listSpecification = ArrayList<SpecificationDetails>()
        val specificationList = manageSpecification
            .filter { it.value != null && it.typeName != null }
            .sortedByDescending { it.typeName!! }

        for (data in specificationList) {
            if (data.id != data.typeId && data.value!!.trim() != data.typeName!!.trim()) {
                listSpecification.add(SpecificationDetails(specificationId = data.specificationId, id = data.id, value = data.value))
            }
        }

        binding.apply {
            layoutBasic.apply {
                if (productDetail!!.itemdetails.size > 0) {
                    if (productDetail!!.itemdetails[0].price.size > 0) {
                        val ind = productDetail!!.itemdetails[0].price.indexOfFirst { it.quantity == 1L }
                        priceList.clear()
                        priceList.add(ItemPrice(id = if (ind >= 0) productDetail!!.itemdetails[0].price[ind].id else 0, unitPrice = if (!TextUtils.isEmpty(textInputUnitPrice.text.toString().trim())) textInputUnitPrice.text.toString().trim().toDouble() else 0.00, unitCost = if (!TextUtils.isEmpty(textInputUnitCost.text.toString().trim())) textInputUnitCost.text.toString().trim().toDouble() else 0.00, minPrice = if (!TextUtils.isEmpty(textInputMinPrice.text.toString().trim())) textInputMinPrice.text.toString().toDouble() else 0.00, buyDown = if (!TextUtils.isEmpty(textInputBuyDown.text.toString().trim())) textInputBuyDown.text.toString().toDouble() else 0.00, msrp = if (!TextUtils.isEmpty(textInputMSRP.text.toString().trim())) textInputMSRP.text.toString().toDouble() else 0.00, salesPrice = if (!TextUtils.isEmpty(textInputSalePrice.text.toString().trim())) textInputSalePrice.text.toString().toDouble() else 0.00, margin = if (!TextUtils.isEmpty(textInputMargin.text.toString().trim())) textInputMargin.text.toString().toDouble() else 0.00, markup = if (!TextUtils.isEmpty(textInputMarkup.text.toString().trim())) textInputMarkup.text.toString().toDouble() else 0.00, quantity = 1))
                    }
                }
            }
        }

        itemDetailsList.add(ItemDetails(flag = 2, specification = listSpecification, attributes = barItemList, price = priceList, stock = itemStock, upcList = upcAdapter.list, soldAlong = soldAlongAdapter.list, qtyDetail = quantity))

        if (upcAdapter.list.size > 0) updateValidationProduct()
        else showSweetDialog(this@EditProductActivity, resources.getString(R.string.lbl_UPCMissing))
    }
    //endregion

    //region To fetch specification or attributes
    private fun fetchSpecificationOrAttributes(typeId: Int) {
        //Size Pack Dropdown
        viewModelDropDown.getSpecificationType(typeId).observe(this) {
            for (i in it.indices) {
                viewModel.getSizePackDropDown(it[i].value!!).observe(this) { details ->
                    details.add(0, CommonDropDown(it[i].value, it[i].label))
                    CoroutineScope(Dispatchers.Main).launch {
                        dropDownAdapter.apply {
                            setList(DynamicDropDown(details, id = it[i].value!!))
                        }
                    }
                }
            }
        }
    }
    //endregion

    //region To manage update Multi-pack or Matrix
    private fun updateMultiPackMatrix(quantity: QuantityDetail) {
        barItemList.clear()
        itemDetailsList.clear()
        binding.apply {
            layoutBasic.apply {
                itemPackDetailsAdapter.apply {
                    if (list.size > 0) {
                        for (data in list) {
                            if (textViewSelectedPack.text.toString().trim() == data.name) {
                                quantity.id = data.itemDetails!!.qtyDetail!!.id

                                val index = priceList.indexOfFirst { it.quantity == 1L }
                                if (index >= 0) {
                                    priceList[index].unitPrice = if (!TextUtils.isEmpty(textInputUnitPrice.text.toString().trim())) textInputUnitPrice.text.toString().trim().toDouble() else 0.00
                                    priceList[index].unitCost = if (!TextUtils.isEmpty(textInputUnitCost.text.toString().trim())) textInputUnitCost.text.toString().trim().toDouble() else 0.00
                                    priceList[index].minPrice = if (!TextUtils.isEmpty(textInputMinPrice.text.toString().trim())) textInputMinPrice.text.toString().trim().toDouble() else 0.00
                                    priceList[index].buyDown = if (!TextUtils.isEmpty(textInputBuyDown.text.toString().trim())) textInputBuyDown.text.toString().trim().toDouble() else 0.00
                                    priceList[index].msrp = if (!TextUtils.isEmpty(textInputMSRP.text.toString().trim())) textInputMSRP.text.toString().trim().toDouble() else 0.00
                                    priceList[index].salesPrice = if (!TextUtils.isEmpty(textInputSalePrice.text.toString().trim())) textInputSalePrice.text.toString().trim().toDouble() else 0.00
                                    priceList[index].margin = if (!TextUtils.isEmpty(textInputMargin.text.toString().trim())) textInputMargin.text.toString().trim().toDouble() else 0.00
                                    priceList[index].markup = if (!TextUtils.isEmpty(textInputMarkup.text.toString().trim())) textInputMarkup.text.toString().trim().toDouble() else 0.00
                                } else {
                                    priceList.clear()
                                    priceList.add(ItemPrice(unitPrice = if (!TextUtils.isEmpty(textInputUnitPrice.text.toString().trim())) textInputUnitPrice.text.toString().trim().toDouble() else 0.00, unitCost = if (!TextUtils.isEmpty(textInputUnitCost.text.toString().trim())) textInputUnitCost.text.toString().trim().toDouble() else 0.00, minPrice = if (!TextUtils.isEmpty(textInputMinPrice.text.toString().trim())) textInputMinPrice.text.toString().trim().toDouble() else 0.00, buyDown = if (!TextUtils.isEmpty(textInputBuyDown.text.toString().trim())) textInputBuyDown.text.toString().trim().toDouble() else 0.00, msrp = if (!TextUtils.isEmpty(textInputMSRP.text.toString().trim())) textInputMSRP.text.toString().trim().toDouble() else 0.00, salesPrice = if (!TextUtils.isEmpty(textInputSalePrice.text.toString().trim())) textInputSalePrice.text.toString().trim().toDouble() else 0.00, margin = if (!TextUtils.isEmpty(textInputMargin.text.toString().trim())) textInputMargin.text.toString().trim().toDouble() else 0.00, markup = if (!TextUtils.isEmpty(textInputMarkup.text.toString().trim())) textInputMarkup.text.toString().trim().toDouble() else 0.00, quantity = 1))
                                }

                                data.itemDetails!!.apply {
                                    soldAlong = ArrayList(soldAlongAdapter.list)
                                    price = ArrayList(priceList)
                                    stock = ArrayList(itemStock)
                                    upcList = ArrayList(upcAdapter.list)
                                    qtyDetail = quantity
                                }
                            }
                            itemDetailsList.add(data.itemDetails!!)
                        }
                        val index = itemDetailsList.indexOfFirst { it.upcList.isEmpty() }
                        if (index >= 0) showSweetDialog(this@EditProductActivity, resources.getString(R.string.lbl_UPCMissing))
                        else updateValidationProduct()
                    } else showSweetDialog(this@EditProductActivity, resources.getString(R.string.ItemTypeValidation, resources.getString(R.string.lbl_LotMatrix), resources.getString(R.string.lbl_MultiPack)))
                }
            }
        }
    }
    //endregion

    //region To reset Specification
    private fun resetSpecification(typeId: Int = Default.SPECIFICATION_TYPE) {
        dropDownAdapter.apply {
            list.clear()
            notifyDataSetChanged()
        }
        fetchSpecificationOrAttributes(typeId)
    }
    //endregion

    //region To manage Sub Categories
    private fun manageSubCategories(id: Int, subCatId: Int = 0) {
        viewModelDropDown.getSubCategory(id).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                subCategoriesSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    clear()
                    subCategory = CommonDropDown(0, resources.getString(R.string.lbl_SelectSubCategories))
                    add(subCategory)
                    addAll(it)
                    val ind = subCategoryList.indexOfFirst { category -> category.value == subCatId }
                    if (ind >= 0) {
                        binding.layoutBasic.spinnerSubCategory.setSelection(ind)
                        subCategory = subCategoryList[ind]
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }
    //endregion

    //region To setup matrix and packs
    private fun setupMatrix(dropDown: CommonDropDown, productResponse: ProductResponse) {
        viewModel.getSizePackDropDown(dropDown.value!!).observe(this@EditProductActivity) { details ->
            details.add(0, CommonDropDown(dropDown.value, dropDown.label))
            CoroutineScope(Dispatchers.Main).launch {
                dropDownAdapter.apply {
                    if (productResponse.itemdetails.size > 0) {
                        if (productResponse.itemdetails[0].specification != null) {
                            if (productResponse.itemdetails[0].specification.size > 0) {
                                val indexVal = productResponse.itemdetails[0].specification.indexOfFirst { dd -> dd.typeId == dropDown.value }
                                if (indexVal >= 0) {
                                    val index = details.indexOfFirst { data -> data.value == productResponse.itemdetails[0].specification[indexVal].id }
                                    if (index >= 0) setList(DynamicDropDown(list = details, id = details[index].value!!, specificationId = productResponse.itemdetails[0].specification[indexVal].specificationId!!))
                                    else setList(DynamicDropDown(list = details, id = dropDown.value!!))
                                } else setList(DynamicDropDown(list = details, id = dropDown.value!!))
                            } else setList(DynamicDropDown(list = details, id = dropDown.value!!))
                        } else setList(DynamicDropDown(list = details, id = dropDown.value!!))
                    } else setList(DynamicDropDown(list = details, id = dropDown.value!!))
                }
            }
        }
    }
    //endregion

    //region To manage pack combinations
    private fun packCombinations(productResponse: ProductResponse) {
        itemPackDetailsAdapter.apply {
            val details = ArrayList<ItemDetailsPack>()
            for (data in productResponse.itemdetails) {

                val specification = ArrayList<SpecificationDetails>()
                for (j in data.specification) {
                    specification.add(SpecificationDetails(
                        specificationId = j.specificationId,
                        id = j.id,
                        value = j.value,
                                                          ))
                }

                val attributes = ArrayList<Attributes>()
                for (j in data.attribute) {
                    attributes.add(Attributes(
                        id = j.id,
                        itemId = j.itemId,
                        value = j.value,
                        uom = j.uom,
                        specificationId = j.specificationId,
                        itemStockQuantity = j.itemStockQuantity,
                                             ))
                }

                val price = ArrayList<ItemPrice>()
                for (j in data.price) {
                    price.add(ItemPrice(
                        id = j.id,
                        specification = j.specification,
                        unitCost = j.unitCost,
                        unitPrice = j.unitPrice,
                        minPrice = j.minPrice,
                        buyDown = j.buyDown,
                        msrp = j.msrp,
                        salesPrice = j.salesPrice,
                        margin = j.margin,
                        markup = j.markup,
                        quantity = j.quantity,
                                       ))
                }

                val stocks = ArrayList<ItemStock>()
                for (j in data.stock) {
                    stocks.add(ItemStock(
                        facilityId = j.facilityId,
                        id = j.id,
                        quantity = j.quantity,
                                        ))
                }

                val soldAlong = ArrayList<SoldAlong>()
                for (j in data.soldAlong) {
                    soldAlong.add(SoldAlong(
                        soldalongId = j.soldAlongId,
                        soldalongquantity = j.soldAlongQuantity,
                        soldAlongItemId = j.soldAlongItemId,
                        specificationId = j.specificationId,
                        name = j.soldAlongItemName!!
                                           ))
                }


                val itemData = ItemDetails(
                    flag = Default.UPDATE,
                    specification = specification,
                    attributes = attributes,
                    price = price,
                    stock = stocks,
                    soldAlong = soldAlong,
                    upcList = data.upcList,
                    qtyDetail = data.qtyDetail
                                          )
                details.add(ItemDetailsPack(name = data.specification.joinToString(separator = ", ") { "${it.value}" }, itemData))
            }
            setList(details)
            if (list.isNotEmpty()) {
                binding.apply {
                    layoutBasic.apply {
                        textViewSelectedPack.apply {
                            visibility = View.VISIBLE
                            text = "${list[0].name}"
                        }
                    }
                }
            }
        }
    }
    //endregion

    private fun manageAttributeDropDown() {
        val dropDown = ArrayList<CommonDropDown>()
        for (data in attributeList) {
            for (details in productDetail!!.itemdetails) {
                for (j in details.specification) {
                    if(data.value!! > 0) {
                        if (j.typeId == data.value!!) {
                            if (dropDown.isNotEmpty()) {
                                val index = dropDown.indexOfFirst { it.value == data.value }
                                if (index < 0) {
                                    dropDown.add(data)
                                    setupMatrix(data, productDetail!!)
                                }
                            } else {
                                dropDown.add(data)
                                setupMatrix(data, productDetail!!)
                            }
                        }
                    }
                }
            }
        }
    }
}