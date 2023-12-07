package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.FragmentDVoidBinding
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums

class HoldVoidPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDVoidBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private var isVoid: Boolean = false

    companion object {
        fun newInstance(): HoldVoidPopupDialog {
            val f = HoldVoidPopupDialog()
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
        binding = FragmentDVoidBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            isVoid = arguments?.getBoolean(Default.VOID)!!
        }
        postInitViews()
        manageClicks()
    }

    private fun postInitViews() {
        binding!!.apply {
            confirmationText = resources.getString(R.string.OrderConfirm, resources.getString(if (isVoid) R.string.button_Void else R.string.button_Hold))
            confirmation = resources.getString(R.string.Confirmation, resources.getString(if (isVoid) R.string.button_Void else R.string.button_Hold))
        }
    }

    private fun manageClicks() {
        binding!!.apply {
            buttonClose.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL, "")
            }

            buttonConfirm.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, textInputRemark.text.toString().trim())
            }
        }
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, remark: String)
    }
}