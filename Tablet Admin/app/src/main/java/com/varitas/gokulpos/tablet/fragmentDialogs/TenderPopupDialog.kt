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
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.TenderActivity
import com.varitas.gokulpos.tablet.databinding.FragmentDTenderBinding
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.response.Tender
import com.varitas.gokulpos.tablet.utilities.CustomSpinner
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TenderPopupDialog : BaseDialogFragment() {

    private var mActivity : Activity? = null
    private var binding : FragmentDTenderBinding? = null
    private var onButtonClickListener : OnButtonClickListener? = null
    private var tenderDetails : Tender? = null
    private lateinit var paymentModeList : ArrayList<CommonDropDown>
    private lateinit var paymentModeSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var paymentMode : CommonDropDown
    private lateinit var cardTypeList : ArrayList<CommonDropDown>
    private lateinit var cardTypeSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var cardType : CommonDropDown

    companion object {
        fun newInstance() : TenderPopupDialog {
            val f = TenderPopupDialog()
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
        dialog?.window?.setLayout(resources.getInteger(R.integer.fragmentWidth), WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?,
    ) : View {
        binding = FragmentDTenderBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val tenderParcelable = arguments?.getParcelable<Tender>(Default.TENDER)
            if (tenderParcelable != null) tenderDetails = tenderParcelable
        }
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun initData() {
        paymentModeList = ArrayList()
        paymentModeSpinner = ArrayAdapter(TenderActivity.Instance, R.layout.spinner_items, paymentModeList)
        cardTypeList = ArrayList()
        cardTypeSpinner = ArrayAdapter(TenderActivity.Instance, R.layout.spinner_items, cardTypeList)
    }

    private fun loadData() {
        TenderActivity.Instance.viewModel.showProgress.observe(this) {
            TenderActivity.Instance.manageProgress(it)
        }

        if(tenderDetails != null) {
            binding!!.apply {
                textInputTenderDesc.setText(tenderDetails!!.name!!)
                textInputCurrencySymbol.setText(tenderDetails!!.currencySymbol!!)
                textInputExchangeRate.setText(
                    Utils.getTwoDecimalValue(
                        if(tenderDetails!!.exchangeRate != null) tenderDetails!!.exchangeRate!! else 0.00
                    )
                )
                textInputConversionRate.setText(
                    Utils.getTwoDecimalValue(
                        if(tenderDetails!!.conversionRate != null) tenderDetails!!.conversionRate!! else 0.00
                    )
                )
                textInputMinPaymentAmount.setText(
                    Utils.getTwoDecimalValue(
                        if(tenderDetails!!.minPaymentAmount != null) tenderDetails!!.minPaymentAmount!! else 0.00
                    )
                )
            }
        }
        TenderActivity.Instance.viewModel.getPaymentModeDropDown().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                paymentModeSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    paymentMode = CommonDropDown(0, resources.getString(R.string.lbl_SelectPaymentMode))
                    add(paymentMode)
                    addAll(it)
                    if(tenderDetails != null) {
                        val ind = paymentModeList.indexOfFirst { tender-> tender.value == tenderDetails!!.paymentMode }
                        if(ind >= 0) {
                            binding!!.spinnerPaymentMode.setSelection(ind)
                            paymentMode = CommonDropDown(paymentModeList[ind].value, paymentModeList[ind].label)
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }

        TenderActivity.Instance.viewModel.getCardTypeDropDown().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                cardTypeSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    cardType = CommonDropDown(0, resources.getString(R.string.lbl_SelectCardType))
                    add(cardType)
                    addAll(it)
                    if(tenderDetails != null) {
                        val ind = cardTypeList.indexOfFirst { tender-> tender.value == tenderDetails!!.cardType }
                        if(ind >= 0) {
                            binding!!.spinnerCardType.setSelection(ind)
                            cardType = CommonDropDown(cardTypeList[ind].value, cardTypeList[ind].label)
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun postInitViews() {
        binding!!.layoutToolbar.textViewTitle.text = resources.getString(R.string.Tender, if(tenderDetails == null) resources.getString(R.string.lbl_Add) else resources.getString(R.string.lbl_Edit))
    }

    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonSave.clickWithDebounce {
                val tenderDesc = textInputTenderDesc.text.toString().trim()
                val currencySymbol = textInputCurrencySymbol.text.toString().trim()
                val exchangeRate = if(!TextUtils.isEmpty(textInputExchangeRate.text.toString().trim())) textInputExchangeRate.text.toString().trim().toDouble() else 0.00
                val conversionRate = if(!TextUtils.isEmpty(textInputConversionRate.text.toString().trim())) textInputConversionRate.text.toString().trim().toDouble() else 0.00
                val minPayment = if(!TextUtils.isEmpty(textInputMinPaymentAmount.text.toString().trim())) textInputMinPaymentAmount.text.toString().trim().toDouble() else 0.00
                if(tenderDesc.isNotEmpty()) {
                    if(tenderDetails == null) {
                        val req = Tender(
                            name = tenderDesc,
                            exchangeRate = exchangeRate,
                            paymentMode = paymentMode.value,
                            cardType = cardType.value,
                            conversionRate = conversionRate,
                            currencySymbol = currencySymbol,
                            minPaymentAmount = minPayment
                        )
                        TenderActivity.Instance.viewModel.insertTender(req).observe(this@TenderPopupDialog) {
                            if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)
                        }
                    } else {
                        val req = Tender(
                            id = tenderDetails!!.id!!,
                            name = tenderDesc,
                            exchangeRate = exchangeRate,
                            paymentMode = paymentMode.value,
                            cardType = cardType.value,
                            conversionRate = conversionRate,
                            currencySymbol = currencySymbol,
                            minPaymentAmount = minPayment
                        )
                        TenderActivity.Instance.viewModel.updateTender(req).observe(this@TenderPopupDialog) {
                            if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.UPDATE, Tender(name = tenderDesc, sCardType = cardType.label))
                        }
                    }
                } else TenderActivity.Instance.showSweetDialog(TenderActivity.Instance, resources.getString(R.string.lbl_TenderValidation))
            }

            spinnerPaymentMode.apply {
                adapter = paymentModeSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        paymentMode = spinner?.selectedItem as CommonDropDown
                    }
                })
            }

            spinnerCardType.apply {
                adapter = cardTypeSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        cardType = spinner?.selectedItem as CommonDropDown
                    }
                })
            }
        }
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents, tender : Tender? = null)
    }

}