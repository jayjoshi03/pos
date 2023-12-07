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
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.ProductListActivity
import com.varitas.gokulpos.tablet.adapter.SpecificationSelectorAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDAddSaleBinding
import com.varitas.gokulpos.tablet.response.AddItemToDiscount
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.ItemToDiscountMap
import com.varitas.gokulpos.tablet.response.ProductList
import com.varitas.gokulpos.tablet.utilities.CustomSpinner
import com.varitas.gokulpos.tablet.utilities.Default
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManageAddToSalePopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDAddSaleBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private lateinit var discountList: ArrayList<CommonDropDown>
    private lateinit var discountSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var promotionType: CommonDropDown
    private lateinit var discountTypeList: ArrayList<CommonDropDown>
    private lateinit var discountTypeSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var discountType: CommonDropDown
    private lateinit var specificationSelectorAdapter: SpecificationSelectorAdapter
    private lateinit var product: ProductList
//    private val viewModelFeature: ProductFeatureViewModel by viewModels()
//    private val orderViewModel: OrdersViewModel by viewModels()

    companion object {
        fun newInstance(): ManageAddToSalePopupDialog {
            val f = ManageAddToSalePopupDialog()
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
        binding = FragmentDAddSaleBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<ProductList>(Default.PRODUCT)?.let { details ->
            product = details
        }
        initData()
        manageClicks()
        loadData()
        postInitViews()
    }

    private fun initData() {
        discountList = ArrayList()
        discountTypeList = ArrayList()
        discountSpinner = ArrayAdapter(ProductListActivity.Instance, R.layout.spinner_items, discountList)
        discountTypeSpinner = ArrayAdapter(ProductListActivity.Instance, R.layout.spinner_items, discountTypeList)
        specificationSelectorAdapter = SpecificationSelectorAdapter()
    }

    private fun postInitViews() {
        binding!!.apply {
            textViewItemName.text = resources.getString(R.string.hint_ItemName).uppercase() + ": ${product.name} ${product.specification}, (${product.sku})"
            recycleViewSpecification.apply {
                adapter = specificationSelectorAdapter
                layoutManager = LinearLayoutManager(ProductListActivity.Instance)
            }
        }
    }

    private fun loadData() {

        ProductListActivity.Instance.viewModelFeature.showProgress.observe(this) {
            ProductListActivity.Instance.manageProgress(it)
        }

        ProductListActivity.Instance.orderViewModel.showProgress.observe(this) {
            ProductListActivity.Instance.manageProgress(it)
        }


        ProductListActivity.Instance.viewModelFeature.fetchPromotionDropDown(Default.PROMOTION_ITEM).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                discountSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    promotionType = CommonDropDown(0, resources.getString(R.string.lbl_SelectPromotion))
                    add(promotionType)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }

        ProductListActivity.Instance.viewModelFeature.getGroupTypeDropdown(Default.TYPE_DISCOUNT).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                discountTypeSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    discountType = CommonDropDown(0, resources.getString(R.string.lbl_DiscountType))
                    add(discountType)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }

        ProductListActivity.Instance.viewModelFeature.getItemSpecification(product.id!!).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                specificationSelectorAdapter.apply {
                    setList(it)
                }
            }
        }
    }

    private fun manageClicks() {
        binding!!.apply {
            buttonClose.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener()
            }

            checkBoxIsBOGO.setOnCheckedChangeListener { _, isChecked ->
                linearLayoutBogo.visibility = if (isChecked) View.VISIBLE else View.GONE
                linearLayoutNotBogo.visibility = if (isChecked) View.GONE else View.VISIBLE
            }

            buttonAdd.clickWithDebounce {
                specificationSelectorAdapter.apply {
                    val isSpecificationChecked = list.indexOfFirst { it.isSelected } >= 0
                    if (promotionType.value!! > 0 && isSpecificationChecked) {

                        val discValue = if (!TextUtils.isEmpty(textInputDiscountValue.text.toString().trim())) textInputDiscountValue.text.toString().trim().toDouble() else 0.00
                        val bogoBuy = if (!TextUtils.isEmpty(textInputBOGOBuy.text.toString().trim())) textInputBOGOBuy.text.toString().trim().toInt() else 0
                        val bogoGet = if (!TextUtils.isEmpty(textInputBOGOGet.text.toString().trim())) textInputBOGOGet.text.toString().trim().toInt() else 0
                        val qty = if (!TextUtils.isEmpty(textInputQuantity.text.toString().trim())) textInputQuantity.text.toString().trim().toInt() else 0

                        val discountList = ArrayList<ItemToDiscountMap>()
                        if (!checkBoxIsBOGO.isChecked) {
                            if (discountType.value!! > 0) {
                                if (qty > 0) {
                                    if (discValue > 0.00) {
                                        for (j in list.filter { it.isSelected }) {
                                            discountList.add(
                                                ItemToDiscountMap(
                                                    discountAppId = Default.PROMOTION_ITEM,
                                                    detailId = product.id,
                                                    quantity = qty,
                                                    discountAmount = discValue,
                                                    itemBuy = bogoBuy,
                                                    itemGet = bogoGet,
                                                    isBogo = checkBoxIsBOGO.isChecked,
                                                    discountType = discountType.value,
                                                    specificationId = j.value,
                                                    isMinimumAmount = false,
                                                    minimumAmount = 0.00,
                                                    requiredQuantity = 0
                                                                 )
                                                            )
                                        }
                                        insertItemToPromotion(discountList)
                                    } else ProductListActivity.Instance.showSweetDialog(ProductListActivity.Instance, resources.getString(R.string.Validation, resources.getString(R.string.hint_DiscountValue)))
                                } else ProductListActivity.Instance.showSweetDialog(ProductListActivity.Instance, resources.getString(R.string.Validation, resources.getString(R.string.lbl_Quantity)))
                            } else ProductListActivity.Instance.showSweetDialog(ProductListActivity.Instance, resources.getString(R.string.Validation, resources.getString(R.string.lbl_DiscountType)))
                        } else {
                            if (bogoBuy > 0.00 && bogoGet > 0.00) {
                                for (j in list.filter { it.isSelected }) {
                                    discountList.add(
                                        ItemToDiscountMap(
                                            discountAppId = Default.PROMOTION_ITEM,
                                            detailId = product.id,
                                            quantity = 0,
                                            discountAmount = 0.00,
                                            itemBuy = bogoBuy,
                                            itemGet = bogoGet,
                                            isBogo = checkBoxIsBOGO.isChecked,
                                            discountType = 0,
                                            specificationId = j.value,
                                            isMinimumAmount = false,
                                            minimumAmount = 0.00,
                                            requiredQuantity = 0
                                                         )
                                                    )
                                }
                                insertItemToPromotion(discountList)
                            } else ProductListActivity.Instance.showSweetDialog(ProductListActivity.Instance, resources.getString(R.string.Validation, "${resources.getString(R.string.hint_Buy)} Or ${resources.getString(R.string.hint_Get)}"))
                        }
                    } else return@clickWithDebounce
                }
            }

            spinnerDiscount.apply {
                adapter = discountSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner: Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner: Spinner?) {
                        promotionType = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            spinnerDiscountType.apply {
                adapter = discountTypeSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner: Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner: Spinner?) {
                        discountType = spinner?.selectedItem as CommonDropDown
                    }
                })
            }
        }
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    private fun insertItemToPromotion(discountList: ArrayList<ItemToDiscountMap>) {
        val req = AddItemToDiscount(
            discountId = promotionType.value,
            discountMapList = discountList
                                   )
        ProductListActivity.Instance.orderViewModel.insertItemToPromotion(req).observe(this@ManageAddToSalePopupDialog) {
            if (it) onButtonClickListener?.onButtonClickListener()
        }
    }

    interface OnButtonClickListener {
        fun onButtonClickListener()
    }
}