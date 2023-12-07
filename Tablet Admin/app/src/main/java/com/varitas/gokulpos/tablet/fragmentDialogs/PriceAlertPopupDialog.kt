package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.FragmentDPriceConfirmBinding
import com.varitas.gokulpos.tablet.utilities.Enums

class PriceAlertPopupDialog : BaseDialogFragment() {
    private var mActivity: Activity? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private var binding: FragmentDPriceConfirmBinding? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (context is Activity) mActivity = activity
    }

    companion object {
        fun newInstance(): PriceAlertPopupDialog {
            val f = PriceAlertPopupDialog()
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDPriceConfirmBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        val param = WindowManager.LayoutParams()
        param.copyFrom(dialog!!.window!!.attributes)
        param.width = 1000
        param.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = param
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postInitViews()
        manageClicks()
    }


    private fun postInitViews() {
        binding!!.apply {
            confirmationText = resources.getString(R.string.lbl_UnitCostValidation)
        }
    }

    private fun manageClicks() {
        binding!!.apply {
            buttonClose.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonConfirm.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)
            }
        }
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents)
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}