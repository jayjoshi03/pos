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
import androidx.recyclerview.widget.GridLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.OrderActivity
import com.varitas.gokulpos.tablet.adapter.DropDownAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDQuickAddBinding
import com.varitas.gokulpos.tablet.request.ItemDetails
import com.varitas.gokulpos.tablet.request.ItemPrice
import com.varitas.gokulpos.tablet.request.ItemUPC
import com.varitas.gokulpos.tablet.request.ManageSpecification
import com.varitas.gokulpos.tablet.request.ProductInsertRequest
import com.varitas.gokulpos.tablet.request.QuantityDetail
import com.varitas.gokulpos.tablet.request.SpecificationDetails
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.DynamicDropDown
import com.varitas.gokulpos.tablet.utilities.CustomSpinner
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuickAddPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDQuickAddBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private lateinit var categoriesList: ArrayList<CommonDropDown>
    private lateinit var categoriesSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var category: CommonDropDown
    private lateinit var departmentList: ArrayList<CommonDropDown>
    private lateinit var departmentSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var department: CommonDropDown
    private lateinit var taxGroupList: ArrayList<CommonDropDown>
    private lateinit var taxGroupSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var taxGroup: CommonDropDown
    private lateinit var dropDownAdapter: DropDownAdapter
    private lateinit var manageSpecification: ArrayList<ManageSpecification>

    companion object {
        fun newInstance(): QuickAddPopupDialog {
            val f = QuickAddPopupDialog()
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
        dialog?.window?.setLayout(resources.getInteger(R.integer.fragmentHeight), WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
                             ): View {
        binding = FragmentDQuickAddBinding.inflate(LayoutInflater.from(context))
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
        categoriesSpinner = ArrayAdapter(OrderActivity.Instance, R.layout.spinner_items, categoriesList)
        departmentList = ArrayList()
        departmentSpinner = ArrayAdapter(OrderActivity.Instance, R.layout.spinner_items, departmentList)
        taxGroupList = ArrayList()
        taxGroupSpinner = ArrayAdapter(OrderActivity.Instance, R.layout.spinner_items, taxGroupList)
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

        OrderActivity.Instance.productViewModel.showProgress.observe(this) {
            OrderActivity.Instance.manageProgress(it)
        }

        OrderActivity.Instance.featureViewModel.getDepartment().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                departmentSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    department = CommonDropDown(0, resources.getString(R.string.lbl_SelectDepartment))
                    add(department)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }

        OrderActivity.Instance.featureViewModel.getParentCategory().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                categoriesSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    category = CommonDropDown(0, resources.getString(R.string.lbl_SelectCategory))
                    add(category)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }

        OrderActivity.Instance.featureViewModel.fetchGroupsById(Default.TYPE_TAX).observe(this) {
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

        //Size Pack Dropdown
        OrderActivity.Instance.featureViewModel.getSpecificationType(Default.SPECIFICATION_TYPE).observe(this) {
            for (i in it.indices) {
                OrderActivity.Instance.productViewModel.getSizePackDropDown(it[i].value!!).observe(this) { details ->
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
            layoutToolbar.textViewTitle.text = resources.getText(R.string.Menu_QuickAdd)

            recycleViewSizePack.apply {
                adapter = dropDownAdapter
                layoutManager = GridLayoutManager(OrderActivity.Instance, 3)
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
                if (!TextUtils.isEmpty(textInputName.text.toString().trim())) {
                    if (textInputUPC.text.toString().trim().length in 4..14) insertProduct()
                    else OrderActivity.Instance.showSweetDialog(OrderActivity.Instance, resources.getString(R.string.lbl_UPCValidation))
                } else OrderActivity.Instance.showSweetDialog(OrderActivity.Instance, resources.getString(R.string.lbl_NameMissing))
            }

            spinnerCategory.apply {
                adapter = categoriesSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner: Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner: Spinner?) {
                        category = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            spinnerTaxGroup.apply {
                adapter = taxGroupSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner: Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner: Spinner?) {
                        taxGroup = spinner?.selectedItem as CommonDropDown
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
        }
    }

    private fun insertProduct() {
        binding!!.apply {
            val unitPrice = if (!TextUtils.isEmpty(textInputUnitPrice.text.toString().trim())) textInputUnitPrice.text.toString().trim().toDouble() else 0.00
            val unitCost = if (!TextUtils.isEmpty(textInputUnitCost.text.toString().trim())) textInputUnitCost.text.toString().trim().toDouble() else 0.00
            if (unitPrice >= unitCost) {
                val listSpecification = ArrayList<SpecificationDetails>()
                for (data in manageSpecification) listSpecification.add(SpecificationDetails(id = data.id, value = data.value))

                val priceList = ArrayList<ItemPrice>()
                priceList.add(ItemPrice(
                    unitPrice = unitPrice,
                    unitCost = unitCost,
                    margin = Utils.calculateMargin(unitPrice, unitCost, 0.00),
                    markup = Utils.calculateMarkUp(unitPrice, unitCost, 0.00),
                    quantity = 1
                                       ))

                val upcList = ArrayList<ItemUPC>()
                if (!TextUtils.isEmpty(textInputUPC.text.toString().trim())) upcList.add(ItemUPC(textInputUPC.text.toString().trim()))

                val details = ArrayList<ItemDetails>()
                details.add(ItemDetails(
                    specification = listSpecification,
                    attributes = ArrayList(),
                    price = priceList,
                    stock = ArrayList(),
                    upcList = upcList,
                    soldAlong = ArrayList(),
                    qtyDetail = QuantityDetail()
                                       ))

                val req = ProductInsertRequest(
                    name = textInputName.text.toString().trim(),
                    type = 1,
                    departmentId = department.value,
                    categoryId = category.value,
                    taxGroupId = taxGroup.value,
                    promptForPrice = checkBoxPrice.isChecked,
                    promptForQuantity = checkBoxQty.isChecked,
                    nonStockItem = checkBoxNonStock.isChecked,
                    itemdetails = details)

                OrderActivity.Instance.productViewModel.insertProduct(req).observe(OrderActivity.Instance) {
                    if (it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)
                }
            } else OrderActivity.Instance.showSweetDialog(OrderActivity.Instance, resources.getString(R.string.lbl_UnitPriceValidation))
        }
    }
    //endregion

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents)
    }
}