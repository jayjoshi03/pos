package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.UOMActivity
import com.varitas.gokulpos.tablet.databinding.FragmentDUomBinding
import com.varitas.gokulpos.tablet.request.UOMInsertRequest
import com.varitas.gokulpos.tablet.request.UOMUpdateRequest
import com.varitas.gokulpos.tablet.response.UOM
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums

class UOMPopupDialog : BaseDialogFragment() {

    private var mActivity : Activity? = null
    private var binding : FragmentDUomBinding? = null
    private var onButtonClickListener : OnButtonClickListener? = null
    private var uomDetails : UOM? = null

    companion object {
        fun newInstance() : UOMPopupDialog {
            val f = UOMPopupDialog()
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
        binding = FragmentDUomBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val uomParcelable = arguments?.getParcelable<UOM>(Default.UOM)
            if (uomParcelable != null) uomDetails = uomParcelable
        }
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun loadData() {
        UOMActivity.Instance.viewModel.showProgress.observe(this) {
            UOMActivity.Instance.manageProgress(it)
        }

        if(uomDetails != null) binding!!.textInputUOMType.setText(uomDetails!!.uom!!)
    }

    private fun postInitViews() {
        binding!!.layoutToolbar.textViewTitle.text = resources.getString(R.string.UOM, if(uomDetails == null) resources.getString(R.string.lbl_Add) else resources.getString(R.string.lbl_Edit))
    }

    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonSave.clickWithDebounce {
                val uomType = textInputUOMType.text.toString().trim()
                if(uomType.isNotEmpty()) {
                    if(uomDetails == null) {
                        val req = UOMInsertRequest(
                            uom = uomType
                        )
                        UOMActivity.Instance.viewModel.insertUOM(req).observe(UOMActivity.Instance) {
                            if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)
                        }
                    } else {
                        val req = UOMUpdateRequest(
                            id = uomDetails!!.id,
                            uom = uomType
                                                  )
                        UOMActivity.Instance.viewModel.updateUOM(req).observe(UOMActivity.Instance) {
                            if (it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, UOM(uom = uomType), true)
                        }
                    }
                } else UOMActivity.Instance.showSweetDialog(UOMActivity.Instance, resources.getString(R.string.Validation, resources.getString(R.string.hint_UOMType)))
            }
        }
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents, uom : UOM? = null, isUpdate : Boolean = false)
    }

}