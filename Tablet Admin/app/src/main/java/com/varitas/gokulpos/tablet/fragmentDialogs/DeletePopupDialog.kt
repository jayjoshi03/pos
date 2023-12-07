package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.FragmentDDeleteBinding
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums

class DeletePopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDDeleteBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private var qty = 0

    companion object {
        fun newInstance(): DeletePopupDialog {
            val f = DeletePopupDialog()
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
        binding = FragmentDDeleteBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            qty = arguments?.getInt(Default.QUANTITY)!!
        }
        postInitViews()
        manageClicks()
    }

    private fun postInitViews() {
        binding!!.apply {
            confirmationText = resources.getString(R.string.OrderConfirm, resources.getString(R.string.button_Delete))
            confirmation = resources.getString(R.string.Confirmation, resources.getString(R.string.button_Delete))
            quantity = qty
            buttonReduce.visibility = if(qty == 1) View.GONE else View.VISIBLE
        }
    }

    private fun manageClicks() {
        binding!!.apply {
            buttonClose.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonDelete.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)
            }

            buttonReduce.clickWithDebounce {
                if (!TextUtils.isEmpty(binding!!.textInputQty.text.toString())) {
                    val reduce = binding!!.textInputQty.text.toString().toInt()
                    when {
                        reduce < qty -> onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.REDUCE, reduce)
                        reduce == qty -> onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.DELETE)
                        else -> onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
                    }
                }
            }
        }
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, qty: Int = 0)
    }
}