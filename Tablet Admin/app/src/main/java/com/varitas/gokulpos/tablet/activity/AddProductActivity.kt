package com.varitas.gokulpos.tablet.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
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
import com.varitas.gokulpos.tablet.response.ScanBarcode
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

@AndroidEntryPoint class AddProductActivity : BaseActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val viewModel: ProductViewModel by viewModels()
    val viewModelFeature: ProductFeatureViewModel by viewModels()
    private val viewModelOrder: OrdersViewModel by viewModels()
    private val viewModelDropDown: ProductFeatureViewModel by viewModels()
    private lateinit var categoriesList: ArrayList<CommonDropDown>
    private lateinit var categoriesSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var category: CommonDropDown
    private lateinit var subCategoriesList: ArrayList<CommonDropDown>
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
    private lateinit var uomList: ArrayList<CommonDropDown>
    private lateinit var uomSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var uom: CommonDropDown
    private lateinit var attributeList: ArrayList<CommonDropDown>
    private lateinit var attributeSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var attribute: CommonDropDown
    private var scannedBarcode: ScanBarcode? = null
    private lateinit var dropDownAdapter: DropDownAdapter
    private lateinit var soldAlongAdapter: SoldAlongAdapter
    private lateinit var itemPackDetailsAdapter: ItemPackDetailsAdapter
    private lateinit var upcAdapter: UPCAdapter
    private lateinit var manageSpecification: ArrayList<ManageSpecification>
    private lateinit var itemStock: ArrayList<ItemStock>
    private lateinit var priceList: ArrayList<ItemPrice>
    private lateinit var facilities: ArrayList<Facility>
    private lateinit var itemDetailsList: ArrayList<ItemDetails>
    private lateinit var barItemList: ArrayList<Attributes>
    private lateinit var suggestionAdapter: AutoCompleteAdapter
    private lateinit var suggestionBarAdapter: AutoCompleteAdapter
    private lateinit var soldAlongItem: FavouriteItems
    private lateinit var barItem: FavouriteItems
    private var parentId = 0
    private var isFromOrder = false

    companion object {
        lateinit var Instance: AddProductActivity
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
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_AddProduct)
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@AddProductActivity, R.drawable.ic_save))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }

        itemPackDetailsAdapter = ItemPackDetailsAdapter { it, _, oldPosition, click ->

            when (click) {
                Enums.ClickEvents.VIEW -> {
                    if (it.itemDetails!!.flag == null) {
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
                else -> {}
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

        dropDownAdapter = DropDownAdapter { commonDropDown, id, name, specificationId ->
            val ind = manageSpecification.indexOfFirst { it.typeId == id }
            if (ind >= 0) {
                manageSpecification[ind].id = commonDropDown.value!!
                manageSpecification[ind].value = commonDropDown.label!!
                manageSpecification[ind].typeName = name
                manageSpecification[ind].specificationId = specificationId
            } else manageSpecification.add(ManageSpecification(commonDropDown.value!!, commonDropDown.label!!, id, name, specificationId))
        }

        scannedBarcode = intent.getParcelableExtra(Default.PRODUCT)
        parentId = if (intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        isFromOrder = if (intent.extras?.getBoolean(Default.ORDER) != null) intent.extras?.getBoolean(Default.ORDER)!! else false
        soldAlongItem = FavouriteItems()
        barItem = FavouriteItems()
        manageSpecification = ArrayList()
        barItemList = ArrayList()
        facilities = ArrayList()
        itemStock = ArrayList()
        priceList = ArrayList()
        itemDetailsList = ArrayList()
        categoriesList = ArrayList()
        category = CommonDropDown(0, resources.getString(R.string.lbl_SelectCategory))
        categoriesList.add(category)
        categoriesSpinner = ArrayAdapter(this, R.layout.spinner_items, categoriesList)
        subCategoriesList = ArrayList()
        subCategory = CommonDropDown(0, resources.getString(R.string.lbl_SelectSubCategories))
        subCategoriesList.add(subCategory)
        subCategoriesSpinner = ArrayAdapter(this, R.layout.spinner_items, subCategoriesList)
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
            layoutBasic.apply {
                textInputSoldAlongSKU.setAdapter(suggestionAdapter)
                textInputSoldAlongSKU.threshold = 0

                textInputBarItemSKU.setAdapter(suggestionBarAdapter)
                textInputBarItemSKU.threshold = 0

                textInputUnitInCase.isEnabled = false
                textInputCaseCost.isEnabled = false
                textInputCasePrice.isEnabled = false
                buttonPrice.isEnabled = false

                recycleViewSizePack.apply {
                    adapter = dropDownAdapter
                    layoutManager = GridLayoutManager(this@AddProductActivity, 4)
                }

                recycleviewCombinations.apply {
                    adapter = itemPackDetailsAdapter
                    layoutManager = GridLayoutManager(this@AddProductActivity, 4)
                }

                recycleViewSoldAlong.apply {
                    adapter = soldAlongAdapter
                    layoutManager = LinearLayoutManager(this@AddProductActivity)
                }

                recycleViewUPC.apply {
                    adapter = upcAdapter
                    layoutManager = LinearLayoutManager(this@AddProductActivity)
                }

                imageViewScan.visibility = View.VISIBLE

                textInputUPC.setOnFocusChangeListener { _, _ ->
                    if (!TextUtils.isEmpty(textInputUPC.text.toString().trim())) {
                        if (textInputUPC.text.toString().trim().length < 3) textInputUPC.error = resources.getString(R.string.lbl_UPCValidation)
                        else textInputUPC.error = null
                    } else textInputUPC.error = null
                }

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

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewBack.clickWithDebounce {
                val intent = Intent(this@AddProductActivity, ProductListActivity::class.java)
                intent.putExtra(Default.PARENT_ID, parentId)
                intent.putExtra(Default.ORDER, isFromOrder)
                openActivity(intent)
            }

            layoutToolbar.imageViewAction.clickWithDebounce {
                layoutBasic.apply {
                    val quantity = QuantityDetail(if (!TextUtils.isEmpty(textInputMinWarnQty.text.toString())) textInputMinWarnQty.text.toString().toInt() else 0, if (!TextUtils.isEmpty(textInputReOrder.text.toString())) textInputReOrder.text.toString().toInt() else 0)
                    if (!TextUtils.isEmpty(textInputName.text.toString().trim())) {
                        when (itemType.value) {
                            Default.STANDARD -> {
                                barItemList.clear()
                                insertStandardBar(quantity)
                            }
                            Default.BAR -> insertStandardBar(quantity)
                            Default.MULTI_PACK -> insertMultiPackMatrix(quantity)
                            Default.LOT_MATRIX -> insertMultiPackMatrix(quantity)
                        }
                    } else showSweetDialog(this@AddProductActivity, resources.getString(R.string.lbl_NameMissing))
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
                    if (details.isSize!!) {
                        barItem = details
                        textInputBarItemName.setText(details.name!!)
                        textInputBarItemQty.setText("1")
                        textInputBarItemSKU.text.clear()
                    } else showSweetDialog(this@AddProductActivity, "Selected item doesn't have a size specification. Please select size.")
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
                        val price = ItemPrice(unitPrice = casePrice,
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

                imageViewScan.clickWithDebounce {
                    val intent = Intent(this@AddProductActivity, BarcodeActivity::class.java)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }

                buttonAddSoldAlong.clickWithDebounce {
                    if (soldAlongItem.id != null) {
                        soldAlongAdapter.apply {
                            val index = list.indexOfFirst { it.soldAlongItemId == soldAlongItem.id }
                            if (index >= 0) {
                                list[index].soldalongquantity = list[index].soldalongquantity?.plus(if (!TextUtils.isEmpty(textInputSoldAlongQty.text.toString())) textInputSoldAlongQty.text.toString().toInt() else 0)
                                notifyItemChanged(index)
                            } else addSoldAlong(SoldAlong(soldalongquantity = if (!TextUtils.isEmpty(textInputSoldAlongQty.text.toString())) textInputSoldAlongQty.text.toString().toInt() else 0, soldAlongItemId = soldAlongItem.id, name = textInputSoldAlongName.text.toString().trim(), specificationId = soldAlongItem.specificationId!!))
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
                    } else showSweetDialog(this@AddProductActivity, resources.getString(R.string.Validation, resources.getString(R.string.hint_UOMType)))
                }

                buttonViewBarItem.clickWithDebounce {
                    manageBarItem()
                }

                buttonAuto.clickWithDebounce {
                    upcAdapter.apply {
                        textInputUPC.setText(Utils.getAutoGeneratedUPCBarcode())
                        addUPC(ItemUPC(textInputUPC.text.toString().trim(), true))
                    }
                    buttonAuto.isEnabled = false
                    buttonAuto.isClickable = false
                }

                buttonAdd.clickWithDebounce {
                    addUPC()
                }

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
                                    viewModelFeature.getDepartmentDetails(department.value!!).observe(this@AddProductActivity) {
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
                                                    }
                                                    Default.LOT_MATRIX -> {
                                                        imageViewAddPack.visibility = View.VISIBLE
                                                        linearLayoutBar.visibility = View.GONE
                                                        linearLayoutLotMatrix.visibility = View.VISIBLE
                                                        //resetSpecification(Default.ATTRIBUTE_TYPE)
                                                        dropDownAdapter.apply {
                                                            list.clear()
                                                            notifyDataSetChanged()
                                                        }
                                                        viewModelDropDown.getSpecificationType(Default.ATTRIBUTE_TYPE).observe(this@AddProductActivity) {
                                                            attributeSpinner.apply {
                                                                setDropDownViewResource(R.layout.spinner_dropdown_item)
                                                                clear()
                                                                attribute = CommonDropDown(0, resources.getString(R.string.lbl_SelectAttribute))
                                                                add(attribute)
                                                                addAll(it)
                                                                notifyDataSetChanged()
                                                            }
                                                        }
                                                    }
                                                    Default.BAR -> {
                                                        linearLayoutLotMatrix.visibility = View.GONE
                                                        textViewSelectedPack.visibility = View.GONE
                                                        imageViewAddPack.visibility = View.INVISIBLE
                                                        itemPackDetailsAdapter.apply {
                                                            list.clear()
                                                            setList(list)
                                                        }
                                                        linearLayoutBar.visibility = View.VISIBLE
                                                        resetSpecification()
                                                        manageDisableViews(binding, true)
                                                    }
                                                    Default.STANDARD -> {
                                                        linearLayoutLotMatrix.visibility = View.GONE
                                                        linearLayoutBar.visibility = View.GONE
                                                        textViewSelectedPack.visibility = View.GONE
                                                        imageViewAddPack.visibility = View.INVISIBLE
                                                        itemPackDetailsAdapter.apply {
                                                            list.clear()
                                                            setList(list)
                                                        }
                                                        resetSpecification()
                                                        manageDisableViews(binding, true)
                                                    }
                                                }
                                            }
                                            Enums.ClickEvents.CANCEL -> {
                                                val ind = itemTypeList.indexOf(previousSelectedItem)
                                                if(ind >= 0) {
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
                                    viewModelFeature.getBrandDetails(brand.value!!).observe(this@AddProductActivity) {
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
                    viewStocks(facilities)
                }

                buttonPriceView.clickWithDebounce {
                    if (priceList.size > 0) viewPrices(priceList)
                    else return@clickWithDebounce
                }

                checkBoxPrice.setOnCheckedChangeListener { _, isChecked ->
                    managePrice(isChecked)
                }

                checkBoxQty.setOnCheckedChangeListener { _, isChecked ->
                    manageQuantity(isChecked)
                }

                buttonAddAttribute.clickWithDebounce {
                    if (attribute.value!! > 0) {
                        var index = -1
                        if (dropDownAdapter.list.isNotEmpty()) index = dropDownAdapter.list.indexOfFirst { it.list[0].value == attribute.value }
                        if (index < 0) {
                            viewModel.getSizePackDropDown(attribute.value!!).observe(this@AddProductActivity) { details ->
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
                    } else showSweetDialog(this@AddProductActivity, resources.getString(R.string.Validation, resources.getString(R.string.lbl_SelectAttribute)))
                }

                imageViewAddPack.clickWithDebounce {
                    if (manageSpecification.size > 0) {
                        itemPackDetailsAdapter.apply {
                            val specificationName = manageSpecification.joinToString(separator = ", ") { "${it.value}" }
                            val ind = list.indexOfFirst { it.name == specificationName }
                            if (ind < 0) {
                                val listSpecification = ArrayList<SpecificationDetails>()
                                for (data in manageSpecification) {
                                    if (data.id != data.typeId && data.value!!.trim() != data.typeName!!.trim()) listSpecification.add(SpecificationDetails(id = data.id, value = data.value))
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
        showSweetDialog(this@AddProductActivity, msg)
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

    //region To manage stocks
    private fun manageStocks() {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = FacilityQuantityPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(FacilityQuantityPopupDialog::class.java.name)
        if (prevFragment != null) return
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
                                    itemStock.clear()
                                    for (data in list) itemStock.add(ItemStock(facilityId = data.id, quantity = data.quantity.toLong()))
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

    //region To load data
    private fun loadData() {
        if (scannedBarcode != null) {
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
//                    department = CommonDropDown(0, resources.getString(R.string.lbl_SelectDepartment))
//                    add(department)
                    addAll(it)
                    if (scannedBarcode != null) {
                        val ind = departmentList.indexOfFirst { dept -> dept.value == scannedBarcode!!.departmentId }
                        if (ind >= 0) binding.layoutBasic.spinnerDepartment.setSelection(ind)
                    }
                    notifyDataSetChanged()
                }
            }
        }

        viewModelFeature.fetchFacility().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                facilities.clear()
                facilities.addAll(it)
                for (data in it) itemStock.add(ItemStock(facilityId = data.id, quantity = data.quantity.toLong()))
            }
        }

        viewModelDropDown.getParentCategory().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                categoriesSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
//                    category = CommonDropDown(0, resources.getString(R.string.lbl_SelectCategory))
//                    add(category)
                    addAll(it)
                    if (scannedBarcode != null) {
                        val ind = categoriesList.indexOfFirst { cate -> cate.value == scannedBarcode!!.categoryId }
                        if (ind >= 0) binding.layoutBasic.spinnerCategory.setSelection(ind)
                        if (scannedBarcode!!.categoryId != null) manageSubCategories(scannedBarcode!!.categoryId!!)
                    }
                    notifyDataSetChanged()
                }
            }
        }

        viewModelDropDown.fetchGroupsById(Default.TYPE_TAX).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                taxGroupSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
//                    taxGroup = CommonDropDown(label = resources.getString(R.string.lbl_SelectTax), value = 0)
//                    add(taxGroup)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }

        itemTypeSpinner.apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item)
            itemType = CommonDropDown(Default.STANDARD, resources.getString(R.string.lbl_Standard))
            add(itemType)
            add(CommonDropDown(Default.MULTI_PACK, resources.getString(R.string.lbl_MultiPack)))
            add(CommonDropDown(Default.BAR, resources.getString(R.string.lbl_BarItem)))
            add(CommonDropDown(Default.LOT_MATRIX, resources.getString(R.string.lbl_LotMatrix)))
            notifyDataSetChanged()
        }

        fetchSpecificationOrAttributes(Default.SPECIFICATION_TYPE)

        viewModelDropDown.getBrandDropDown().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                brandSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
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
    } //endregion

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

    private fun insertValidationProduct() {
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
                        if (priceValidation) break
                    }
                    insertProduct(priceValidation)
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
                    Enums.ClickEvents.SAVE -> insertProduct(true)
                    else -> {}
                }
            }
        })
    }

    private fun insertProduct(priceValidation: Boolean) {
        binding.apply {
            layoutBasic.apply {
                if (priceValidation) {
                    val req = ProductInsertRequest(name = textInputName.text.toString().trim(), type = itemType.value, departmentId = department.value, brandId = brand.value, categoryId = if (subCategory.value == 0) category.value else subCategory.value, taxGroupId = taxGroup.value, promptForPrice = checkBoxPrice.isChecked, promptForQuantity = checkBoxQty.isChecked, nonPluItem = checkBoxNonPlu.isChecked,
                        buyCase = checkBoxBuyAsCase.isChecked, status = if(checkBoxInActive.isChecked) Default.INACTIVE else Default.ACTIVE,
                        nonDiscountable = checkBoxNonDiscount.isChecked, countWithNoDisc = checkBoxCountWithNoDisc.isChecked,
                        returnItem = checkBoxReturnItem.isChecked, acceptFoodStamp = checkBoxFoodStamp.isChecked,
                        depositItem = checkBoxDepositItem.isChecked, acceptWiccheck = checkBoxWICCheck.isChecked,
                        nonRevenue = checkBoxNonRevenue.isChecked, webItem = checkBoxWebItem.isChecked,
                        nonCountable = checkBoxNonCountable.isChecked, weightedItem = checkBoxWeightItem.isChecked,
                        nonStockItem = checkBoxNonStock.isChecked, itemdetails = itemDetailsList)

                    viewModel.insertProduct(req).observe(this@AddProductActivity) {
                        if (it) {
                            val intent = Intent(this@AddProductActivity, ProductListActivity::class.java)
                            intent.putExtra(Default.PARENT_ID, parentId)
                            intent.putExtra(Default.ORDER, isFromOrder)
                            openActivity(intent)
                        }
                    }
                } else showSweetDialog(this@AddProductActivity, resources.getString(R.string.lbl_UnitPriceValidation))
            }
        }
    }

    //region To view prices
    private fun viewPrices(prices: ArrayList<ItemPrice>) {
        val ft = supportFragmentManager.beginTransaction()
        val dialogFragment = ItemPriceListPopupDialog.newInstance()
        val prevFragment: Fragment? = supportFragmentManager.findFragmentByTag(ItemPriceListPopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putSerializable(Default.PRICE, prices)
        bundle.putBoolean(Default.EDIT, false)
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
            buttonEnter.isEnabled = !isChecked
        }
    }
    //endregion

    //region To fetch tax/es from taxGroup
    private fun fetchTax() {
        viewModelFeature.fetchTaxesFromGroup(taxGroup.value!!).observe(this) {
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
        bundle.putBoolean(Default.EDIT, false)
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
        bundle.putBoolean(Default.EDIT, false)
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

    //region To manage standard and bar item
    private fun insertStandardBar(quantity: QuantityDetail) {
        itemDetailsList.clear()

        //After talking with the API team, we need to deliver the specifications to them in the correct order, for example, size first, then pack.

        val listSpecification = ArrayList<SpecificationDetails>()
        val specificationList = manageSpecification.filter { it.value != null && it.typeName != null }.sortedByDescending { it.typeName!! }

        for (data in specificationList) {
            if (data.id != data.typeId && data.value!!.trim() != data.typeName!!.trim()) {
                val specificationDetails = SpecificationDetails(id = data.id, value = data.value!!.trim())
                listSpecification.add(specificationDetails)
            }
        }

        binding.apply {
            layoutBasic.apply {
                priceList.clear()
                priceList.add(ItemPrice(unitPrice = if (!TextUtils.isEmpty(textInputUnitPrice.text.toString().trim())) textInputUnitPrice.text.toString().trim().toDouble() else 0.00, unitCost = if (!TextUtils.isEmpty(textInputUnitCost.text.toString().trim())) textInputUnitCost.text.toString().trim().toDouble() else 0.00, minPrice = if (!TextUtils.isEmpty(textInputMinPrice.text.toString().trim())) textInputMinPrice.text.toString().trim().toDouble() else 0.00, buyDown = if (!TextUtils.isEmpty(textInputBuyDown.text.toString().trim())) textInputBuyDown.text.toString().trim().toDouble() else 0.00, msrp = if (!TextUtils.isEmpty(textInputMSRP.text.toString().trim())) textInputMSRP.text.toString().trim().toDouble() else 0.00, salesPrice = if (!TextUtils.isEmpty(textInputSalePrice.text.toString().trim())) textInputSalePrice.text.toString().trim().toDouble() else 0.00, margin = if (!TextUtils.isEmpty(textInputMargin.text.toString().trim())) textInputMargin.text.toString().trim().toDouble() else 0.00, markup = if (!TextUtils.isEmpty(textInputMarkup.text.toString().trim())) textInputMarkup.text.toString().trim().toDouble() else 0.00, quantity = 1))
            }
        }
        itemDetailsList.add(ItemDetails(specification = listSpecification, attributes = barItemList, price = priceList, stock = itemStock, upcList = upcAdapter.list, soldAlong = soldAlongAdapter.list, qtyDetail = quantity))

        if (upcAdapter.list.size > 0) insertValidationProduct()
        else showSweetDialog(this@AddProductActivity, resources.getString(R.string.lbl_UPCMissing))
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
                            setList(DynamicDropDown(details))
                        }
                    }
                }
            }
        }
    }
    //endregion

    //region To insert multi-pack or bar
    private fun insertMultiPackMatrix(quantity: QuantityDetail) {
        barItemList.clear()
        itemDetailsList.clear()
        itemPackDetailsAdapter.apply {
            binding.apply {
                layoutBasic.apply {
                    if (list.size > 0) {
                        for (data in list) {
                            if (textViewSelectedPack.text.toString().trim() == data.name!!.trim()) {
                                val index = data.itemDetails!!.price.indexOfFirst { it.quantity == 1L }
                                if (index >= 0) {
                                    data.itemDetails!!.price[index].unitPrice = if (!TextUtils.isEmpty(textInputUnitPrice.text.toString().trim())) textInputUnitPrice.text.toString().trim().toDouble() else 0.00
                                    data.itemDetails!!.price[index].unitCost = if (!TextUtils.isEmpty(textInputUnitCost.text.toString().trim())) textInputUnitCost.text.toString().trim().toDouble() else 0.00
                                    data.itemDetails!!.price[index].minPrice = if (!TextUtils.isEmpty(textInputMinPrice.text.toString().trim())) textInputMinPrice.text.toString().trim().toDouble() else 0.00
                                    data.itemDetails!!.price[index].buyDown = if (!TextUtils.isEmpty(textInputBuyDown.text.toString().trim())) textInputBuyDown.text.toString().trim().toDouble() else 0.00
                                    data.itemDetails!!.price[index].msrp = if (!TextUtils.isEmpty(textInputMSRP.text.toString().trim())) textInputMSRP.text.toString().trim().toDouble() else 0.00
                                    data.itemDetails!!.price[index].salesPrice = if (!TextUtils.isEmpty(textInputSalePrice.text.toString().trim())) textInputSalePrice.text.toString().trim().toDouble() else 0.00
                                    data.itemDetails!!.price[index].margin = if (!TextUtils.isEmpty(textInputMargin.text.toString().trim())) textInputMargin.text.toString().trim().toDouble() else 0.00
                                    data.itemDetails!!.price[index].markup = if (!TextUtils.isEmpty(textInputMarkup.text.toString().trim())) textInputMarkup.text.toString().trim().toDouble() else 0.00
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
                        if (index >= 0) showSweetDialog(this@AddProductActivity, resources.getString(R.string.lbl_UPCMissing))
                        else insertValidationProduct()
                    } else showSweetDialog(this@AddProductActivity, resources.getString(R.string.ItemTypeValidation, resources.getString(R.string.lbl_LotMatrix), resources.getString(R.string.lbl_MultiPack)))
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
    private fun manageSubCategories(id: Int) {
        viewModelDropDown.getSubCategory(id).observe(this) {
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