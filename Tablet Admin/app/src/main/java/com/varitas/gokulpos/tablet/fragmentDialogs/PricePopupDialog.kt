package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.ProductListActivity
import com.varitas.gokulpos.tablet.databinding.FragmentDPriceBinding
import com.varitas.gokulpos.tablet.request.ItemPrice
import com.varitas.gokulpos.tablet.response.ProductList
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.Utils

class PricePopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    var title: String = ""
    private var binding: FragmentDPriceBinding? = null
    private var product: ProductList? = null
    private var productDetail: ItemPrice? = null
    private var itemId = 0

    @Deprecated("Deprecated in Java")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (context is Activity) mActivity = activity
    }

    companion object {
        fun newInstance(): PricePopupDialog {
            val f = PricePopupDialog()
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View {
        binding = FragmentDPriceBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        val param = WindowManager.LayoutParams()
        param.copyFrom(dialog!!.window!!.attributes)
        param.width = resources.getInteger(R.integer.fragmentInWidth)
        param.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = param
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val productParcelable = arguments?.getParcelable<ItemPrice>(Default.PRODUCT)
            if (productParcelable != null) productDetail = productParcelable
        }
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun loadData() {
        if(productDetail != null) {
            binding!!.apply {
                editTextBuyDown.setSelectAllOnFocus(true)
                editTextUnitPrice.setSelectAllOnFocus(true)
                editTextUnitCost.setSelectAllOnFocus(true)
                editTextSalePrice.setSelectAllOnFocus(true)
                editTextBuyDown.setText(Utils.getTwoDecimalValue(productDetail!!.buyDown!!))
                editTextUnitPrice.setText(Utils.getTwoDecimalValue(productDetail!!.unitPrice!!))
                editTextUnitCost.setText(Utils.getTwoDecimalValue(productDetail!!.unitCost!!))
                editTextMinPrice.setText(Utils.getTwoDecimalValue(productDetail!!.minPrice!!))
                editTextMSRP.setText(Utils.getTwoDecimalValue(productDetail!!.msrp!!))
                editTextMargin.setText(Utils.getTwoDecimalValue(productDetail!!.margin!!))
                editTextMarkup.setText(Utils.getTwoDecimalValue(productDetail!!.markup!!))
                editTextSalePrice.setText(Utils.getTwoDecimalValue(productDetail!!.salesPrice!!))
            }
            itemId = productDetail!!.id!!
        }
    }

    private fun postInitViews() {
        if(product != null) {
            binding!!.layoutToolbar.textViewTitle.isSelected = true
            binding!!.layoutToolbar.textViewTitle.text = product!!.name
            //binding!!.product = product
        } else binding!!.layoutToolbar.textViewTitle.text = title

        binding!!.editTextUnitPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {
            }

            override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
            }

            override fun afterTextChanged(s : Editable?) {
                setMarginMarkup(unitCost = if(!binding!!.editTextUnitCost.text.isNullOrEmpty()) binding!!.editTextUnitCost.text.toString().toDouble() else 0.00, unitPrice = if(!TextUtils.isEmpty(s)) s.toString().toDouble() else 0.00, buyDown = if(!binding!!.editTextBuyDown.text.isNullOrEmpty()) binding!!.editTextBuyDown.text.toString().toDouble() else 0.00)
            }
        })

        binding!!.editTextUnitCost.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {
            }

            override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
            }

            override fun afterTextChanged(s : Editable?) {
                setMarginMarkup(unitPrice = if(!binding!!.editTextUnitPrice.text.isNullOrEmpty()) binding!!.editTextUnitPrice.text.toString().toDouble() else 0.00, unitCost = if(!TextUtils.isEmpty(s)) s.toString().toDouble() else 0.00, buyDown = if(!binding!!.editTextBuyDown.text.isNullOrEmpty()) binding!!.editTextBuyDown.text.toString().toDouble() else 0.00)
            }
        })

        binding!!.editTextBuyDown.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {
            }

            override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
            }

            override fun afterTextChanged(s : Editable?) {
                setMarginMarkup(unitCost = if(!binding!!.editTextUnitCost.text.isNullOrEmpty()) binding!!.editTextUnitCost.text.toString().toDouble() else 0.00, buyDown = if(!TextUtils.isEmpty(s)) s.toString().toDouble() else 0.00, unitPrice = if(!binding!!.editTextUnitPrice.text.isNullOrEmpty()) binding!!.editTextUnitPrice.text.toString().toDouble() else 0.00)
            }
        })

    }

    private fun manageClicks() {
        binding!!.layoutToolbar.imageViewCancel.clickWithDebounce {
            onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
        }

        binding!!.buttonSave.clickWithDebounce {
            var unitCost: Double
            var unitPrice: Double
            var minPrice: Double
            var msrp: Double
            var margin: Double
            var markup: Double
            var salePrice: Double
            var buyDown: Double
            binding!!.apply {
                unitCost = if (!TextUtils.isEmpty(editTextUnitCost.text.toString())) editTextUnitCost.text.toString().toDouble() else 0.00
                unitPrice = if (!TextUtils.isEmpty(editTextUnitPrice.text.toString())) editTextUnitPrice.text.toString().toDouble() else 0.00
                minPrice = if (!TextUtils.isEmpty(editTextMinPrice.text.toString())) editTextMinPrice.text.toString().toDouble() else 0.00
                msrp = if (!TextUtils.isEmpty(editTextMSRP.text.toString())) editTextMSRP.text.toString().toDouble() else 0.00
                margin = if (!TextUtils.isEmpty(editTextMargin.text.toString())) editTextMargin.text.toString().toDouble() else 0.00
                markup = if (!TextUtils.isEmpty(editTextMarkup.text.toString())) editTextMarkup.text.toString().toDouble() else 0.00
                salePrice = if (!TextUtils.isEmpty(editTextSalePrice.text.toString())) editTextSalePrice.text.toString().toDouble() else 0.00
                buyDown = if (!TextUtils.isEmpty(editTextBuyDown.text.toString())) editTextBuyDown.text.toString().toDouble() else 0.00
            }
            if(unitPrice >= unitCost) {
                //region To update product price
                val req = ItemPrice(
                    id = itemId,
                    unitCost = unitCost,
                    unitPrice = unitPrice,
                    minPrice = minPrice,
                    buyDown = buyDown,
                    msrp = msrp,
                    salesPrice = salePrice,
                    margin = margin,
                    markup = markup,
                    quantity = productDetail!!.quantity
                )
                ProductListActivity.Instance.viewModel.updateItemPrice(req).observe(ProductListActivity.Instance) {
                    if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)
                }
                //endregion
            }else ProductListActivity.Instance.showSweetDialog(ProductListActivity.Instance, resources.getString(R.string.lbl_UnitPriceValidation))
        }
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents)
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    //region To  set margin and markp
    private fun setMarginMarkup(unitCost : Double, unitPrice : Double, buyDown : Double) {
        binding?.apply {
            editTextMargin.setText(Utils.getTwoDecimalValue(Utils.calculateMargin(unitPrice, unitCost, buyDown)))
            editTextMarkup.setText(Utils.getTwoDecimalValue(Utils.calculateMarkUp(unitPrice, unitCost, buyDown)))
        }
    }
    //endregion
}