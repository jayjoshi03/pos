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
import com.varitas.gokulpos.tablet.databinding.FragmentDQuantityBinding
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums

class QuantityPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDQuantityBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private var qty = 0
    private var promptQty = false

    companion object {
        fun newInstance(): QuantityPopupDialog {
            val f = QuantityPopupDialog()
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
        binding = FragmentDQuantityBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            qty = arguments?.getInt(Default.QUANTITY)!!
            promptQty = arguments?.getBoolean(Default.PROMPT_QUANTITY)!!
        }
        postInitViews()
        manageClicks()
    }

    private fun postInitViews() {
        binding!!.apply {
            quantity = qty
            promptQuantity = promptQty
        }
    }

    private fun manageClicks() {
        binding!!.apply {
            buttonClose.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL, 0)
            }

            buttonAdd.clickWithDebounce {
                if(fetchQuantity() > 0) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, fetchQuantity())
                else OrderActivity.Instance.showSweetDialog(OrderActivity.Instance, resources.getString(R.string.PriceQtyValidation, resources.getString(R.string.lbl_Qty), "0"))
            }

            buttonReplace.clickWithDebounce {
                if (fetchQuantity() > 0) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.REPLACE, fetchQuantity())
                else OrderActivity.Instance.showSweetDialog(OrderActivity.Instance, resources.getString(R.string.PriceQtyValidation, resources.getString(R.string.lbl_Qty), "0"))
            }

            buttonUpdate.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.UPDATE, fetchQuantity())
            }
        }
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, qty: Int)
    }

    private fun fetchQuantity(): Int {
        var qty: Int
        binding!!.apply {
            qty = if (!TextUtils.isEmpty(textInputQty.text.toString().trim())) {
                textInputQty.text.toString().trim().toInt()
            } else 0
        }
        return qty
    }
}