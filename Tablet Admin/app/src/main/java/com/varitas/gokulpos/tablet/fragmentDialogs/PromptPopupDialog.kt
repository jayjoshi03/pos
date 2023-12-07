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
import com.varitas.gokulpos.tablet.databinding.FragmentDPromptBinding
import com.varitas.gokulpos.tablet.utilities.Enums

class PromptPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDPromptBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null

    companion object {
        fun newInstance(): PromptPopupDialog {
            val f = PromptPopupDialog()
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
        binding = FragmentDPromptBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageClicks()
    }

    private fun manageClicks() {
        binding!!.apply {
            buttonClose.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonAdd.clickWithDebounce {
                if(fetchPrice() > 0.00) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, fetchPrice())
                else OrderActivity.Instance.showSweetDialog(OrderActivity.Instance, resources.getString(R.string.PriceQtyValidation, resources.getString(R.string.lbl_Price), "00.00"))
            }
        }
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, amount: Double = 0.00)
    }

    private fun fetchPrice(): Double {
        var price: Double
        binding!!.apply {
            price = if (!TextUtils.isEmpty(textInputPrice.text.toString().trim())) {
                textInputPrice.text.toString().trim().toDouble()
            } else 0.00
        }
        return price
    }
}