package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.OrderActivity
import com.varitas.gokulpos.tablet.databinding.FragmentDCustomPaymentBinding
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.Utils

class CustomPaymentPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDCustomPaymentBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private var totalAmount: Double = 0.00

    companion object {
        fun newInstance(): CustomPaymentPopupDialog {
            val f = CustomPaymentPopupDialog()
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
        dialog?.window?.setLayout(1000, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
                             ): View {
        binding = FragmentDCustomPaymentBinding.inflate(LayoutInflater.from(context))
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
    }

    private fun postInitViews() {
        binding!!.apply {
            textInputCheckOut.setText(Utils.getTwoDecimalValue(totalAmount))
        }
    }

    private fun manageClicks() {
        binding!!.apply {
            buttonClose.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonSave.clickWithDebounce {
                if (fetchPrice() > 0.00) {
                    if (totalAmount <= fetchPrice()) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, fetchPrice())
                    else OrderActivity.Instance.showSweetDialog(OrderActivity.Instance, resources.getString(R.string.PriceQtyValidation, resources.getString(R.string.lbl_Price), Utils.getTwoDecimalValue(totalAmount)))
                } else OrderActivity.Instance.showSweetDialog(OrderActivity.Instance, resources.getString(R.string.PriceQtyValidation, resources.getString(R.string.lbl_Price), "00.00"))
            }
        }
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, price: Double = 0.00)
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