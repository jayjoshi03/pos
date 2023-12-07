package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.OrderActivity
import com.varitas.gokulpos.tablet.adapter.PaymentAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDCheckoutBinding
import com.varitas.gokulpos.tablet.request.OrderPaymentRequest
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.Utils

class CheckOutPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDCheckoutBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private var totalAmount: Double = 0.00
    private lateinit var paymentAdapter: PaymentAdapter
    private lateinit var typeId: Enums.Tender

    companion object {
        fun newInstance(): CheckOutPopupDialog {
            val f = CheckOutPopupDialog()
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
        dialog?.window?.setLayout(resources.getInteger(R.integer.fragmentInWidth), WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
                             ): View {
        binding = FragmentDCheckoutBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        postInitViews()
        manageClicks()
    }

    private fun initViews() {
        totalAmount = OrderActivity.Instance.viewModel.grossAmount
        typeId = Enums.Tender.CASH
        paymentAdapter = PaymentAdapter()
    }

    private fun postInitViews() {
        binding!!.apply {
            textInputCheckOut.setText(Utils.getTwoDecimalValue(totalAmount))
            textViewPaymentAmount.text = Utils.setAmountWithCurrency(OrderActivity.Instance, 0.00)

            recycleViewPayment.apply {
                adapter = paymentAdapter
                layoutManager = LinearLayoutManager(OrderActivity.Instance)
            }

            OrderActivity.Instance.viewModel.displaySubTotal.observe(this@CheckOutPopupDialog) {
                textViewSubTotal.text = Utils.setAmountWithCurrency(OrderActivity.Instance, it)
            }
            OrderActivity.Instance.viewModel.displaySavings.observe(this@CheckOutPopupDialog) {
                textViewAddOn.text = Utils.setAmountWithCurrency(OrderActivity.Instance, it)
            }

            OrderActivity.Instance.viewModel.displayTotalTax.observe(this@CheckOutPopupDialog) {
                textViewTotalTax.text = Utils.setAmountWithCurrency(OrderActivity.Instance, it)
            }

            OrderActivity.Instance.viewModel.displayFoodStamp.observe(this@CheckOutPopupDialog) {
                textViewFoodStamp.text = Utils.setAmountWithCurrency(OrderActivity.Instance, it)
            }

            OrderActivity.Instance.viewModel.displayNetPayable.observe(this@CheckOutPopupDialog) {
                textViewNetPayable.text = Utils.setAmountWithCurrency(OrderActivity.Instance, it)
            }
            OrderActivity.Instance.viewModel.displayGrossTotal.observe(this@CheckOutPopupDialog) {
                textViewGrossTotal.text = Utils.setAmountWithCurrency(OrderActivity.Instance, it)
            }

            OrderActivity.Instance.viewModel.displayChangeDue.observe(this@CheckOutPopupDialog) {
                textViewDue.text = Utils.setAmountWithCurrency(OrderActivity.Instance, it)
            }

            OrderActivity.Instance.viewModel.displayQuantity.observe(this@CheckOutPopupDialog) {
                textViewQuantity.text = if (it.toString().length > 1) it.toString() else "0${it}"
            }
        }
    }

    private fun manageClicks() {
        binding!!.apply {
            groupTender.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radioCash -> typeId = Enums.Tender.CASH
                    R.id.radioCard -> typeId = Enums.Tender.CARD
                    R.id.radioFoodStamp -> typeId = Enums.Tender.FOODSTAMP
                }
            }

            buttonAdd.clickWithDebounce {
                val price = fetchPrice()
                if (price > 0.00) {
                    OrderActivity.Instance.hideKeyBoard(OrderActivity.Instance)
                    val tenderType = when (typeId) {
                        Enums.Tender.CASH -> Default.TENDER_CASH
                        Enums.Tender.CARD -> Default.TENDER_CARD
                        Enums.Tender.FOODSTAMP -> Default.TENDER_FOODSTAMP
                    }
                    val tenderName = when (typeId) {
                        Enums.Tender.CASH -> resources.getString(R.string.lbl_Cash)
                        Enums.Tender.CARD -> resources.getString(R.string.lbl_Card)
                        Enums.Tender.FOODSTAMP -> resources.getString(R.string.lbl_FoodStamp)
                    }
                    paymentAdapter.apply {
                        val payment = OrderPaymentRequest(
                            orderId = 0,
                            amount = price,
                            tenderType = tenderType,
                            details = "",
                            tenderName = tenderName)
                        addToList(payment)

                        val payAmount = list.sumOf { it.amount!! }
                        textViewPaymentAmount.text = Utils.setAmountWithCurrency(OrderActivity.Instance, payAmount)

                        buttonAdd.isEnabled = payAmount < totalAmount
                        buttonAdd.isClickable = payAmount < totalAmount
                        textInputCheckOut.isEnabled = payAmount < totalAmount

                        textInputCheckOut.text!!.clear()
                    }
                } else OrderActivity.Instance.showSweetDialog(OrderActivity.Instance, resources.getString(R.string.PriceQtyValidation, resources.getString(R.string.lbl_Price), "00.00"))
            }

            buttonSave.clickWithDebounce {
                paymentAdapter.apply {
                    val payAmount = list.sumOf { it.amount!! }
                    if (payAmount >= totalAmount)
                        onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, list, payAmount)
                    else OrderActivity.Instance.showSweetDialog(OrderActivity.Instance, resources.getString(R.string.PriceQtyValidation, resources.getString(R.string.lbl_Price), Utils.getTwoDecimalValue(totalAmount)))
                }

//                val price = fetchPrice()
//                if (price > 0.00) {
//                    if (totalAmount <= price) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, price)
//                    else OrderActivity.Instance.showSweetDialog(OrderActivity.Instance, resources.getString(R.string.PriceQtyValidation, resources.getString(R.string.lbl_Price), Utils.getTwoDecimalValue(totalAmount)))
//                } else OrderActivity.Instance.showSweetDialog(OrderActivity.Instance, resources.getString(R.string.PriceQtyValidation, resources.getString(R.string.lbl_Price), "00.00"))
            }
        }
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, payment: ArrayList<OrderPaymentRequest>, price: Double)
    }

    private fun fetchPrice(): Double {
        var price: Double
        binding!!.apply {
            price = if (!TextUtils.isEmpty(textInputCheckOut.text.toString().trim())) {
                textInputCheckOut.text.toString().trim().toDouble()
            } else 0.00
        }
        return price
    }
}