package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.AddProductActivity
import com.varitas.gokulpos.tablet.activity.EditProductActivity
import com.varitas.gokulpos.tablet.databinding.FragmentDUpcBinding
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums

class UPCPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDUpcBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private var isFromEdit : Boolean = false

    companion object {
        fun newInstance(): UPCPopupDialog {
            val f = UPCPopupDialog()
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
        binding = FragmentDUpcBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null)
            isFromEdit = arguments!!.getBoolean(Default.EDIT)
        manageClicks()
    }

    private fun manageClicks() {
        binding!!.apply {
            buttonClose.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonAdd.clickWithDebounce {
                if (!TextUtils.isEmpty(textInputUPC.text.toString().trim())) {
                    if (textInputUPC.text.toString().trim().length in 4..14) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, textInputUPC.text.toString().trim())
                    else {
                        if(!isFromEdit)
                        AddProductActivity.Instance.showSweetDialog(AddProductActivity.Instance, resources.getString(R.string.lbl_UPCValidation))
                        else EditProductActivity.Instance.showSweetDialog(EditProductActivity.Instance, resources.getString(R.string.lbl_UPCValidation))
                    }
                } else {
                    if(!isFromEdit) AddProductActivity.Instance.showSweetDialog(AddProductActivity.Instance, resources.getString(R.string.lbl_UPCMissing))
                    else EditProductActivity.Instance.showSweetDialog(EditProductActivity.Instance, resources.getString(R.string.lbl_UPCMissing))
                }
            }
        }
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, upc: String = "")
    }
}