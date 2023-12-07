package com.varitas.gokulpos.fragmentDialogs

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.varitas.gokulpos.R
import com.varitas.gokulpos.activity.BrandActivity
import com.varitas.gokulpos.databinding.FragmentDBrandBinding
import com.varitas.gokulpos.request.BrandInsertRequest
import com.varitas.gokulpos.request.BrandUpdateRequest
import com.varitas.gokulpos.response.Brand
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums

class BrandPopupDialog : BaseDialogFragment() {

    private var mActivity : Activity? = null
    private var binding : FragmentDBrandBinding? = null
    private var onButtonClickListener : OnButtonClickListener? = null
    private var brandDetail : Brand? = null

    companion object {
        fun newInstance() : BrandPopupDialog {
            val f = BrandPopupDialog()
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
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?,
    ) : View {
        binding = FragmentDBrandBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            if(arguments!!.getParcelable<Brand>(Default.BRAND) != null) {
                brandDetail = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arguments?.getParcelable(Default.BRAND, Brand::class.java)!!
                else arguments?.getParcelable(Default.BRAND)!!
            }
        }
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun loadData() {
        BrandActivity.Instance.viewModel.showProgress.observe(this) {
            BrandActivity.Instance.manageProgress(it)
        }

        if(brandDetail != null) {
            binding!!.apply {
                textInputBrandName.setText(if(brandDetail!!.name != null) if(!TextUtils.isEmpty(brandDetail!!.name)) brandDetail!!.name!! else "" else "")
                textInputManufacturer.setText(if(brandDetail!!.manufacture != null) if(!TextUtils.isEmpty(brandDetail!!.manufacture!!)) brandDetail!!.manufacture!! else "" else "")
                checkBoxPMUSA.isChecked = if(brandDetail!!.pmUsa != null) brandDetail!!.pmUsa!! else false
                checkBoxRJRT.isChecked = if(brandDetail!!.rjrt != null) brandDetail!!.rjrt!! else false
                checkBoxITG.isChecked = if(brandDetail!!.itg != null) brandDetail!!.itg!! else false
            }
        }
    }

    private fun initData() {

    }

    private fun postInitViews() {
        binding!!.layoutToolbar.textViewTitle.text = resources.getString(R.string.Brand, if(brandDetail == null) resources.getString(R.string.lbl_Add) else resources.getString(R.string.lbl_Edit))
    }

    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonSave.clickWithDebounce {
                val brandName = textInputBrandName.text.toString().trim()
                val manufacturer = textInputManufacturer.text.toString().trim()
                if(!TextUtils.isEmpty(brandName)) {
                    if(brandDetail == null) {
                        val req = BrandInsertRequest(
                            allowOnWeb = false,
                            itg = checkBoxITG.isChecked,
                            manufacture = manufacturer,
                            name = brandName,
                            pmusa = checkBoxPMUSA.isChecked,
                            rjrt = checkBoxRJRT.isChecked
                        )
                        BrandActivity.Instance.viewModel.insertBrand(req).observe(BrandActivity.Instance) {
                            if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)
                        }
                    } else {
                        val req = BrandUpdateRequest(allowOnWeb = false, itg = checkBoxITG.isChecked, manufacture = manufacturer, name = brandName, pmusa = checkBoxPMUSA.isChecked, rjrt = checkBoxRJRT.isChecked, id = brandDetail!!.id, status = Default.ACTIVE)
                        BrandActivity.Instance.viewModel.updateBrand(req).observe(BrandActivity.Instance) {
                            if(it) {
                                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, Brand(name = brandName), true)
                            }
                        }
                    }
                } else BrandActivity.Instance.showSweetDialog(BrandActivity.Instance, resources.getString(R.string.lbl_NameMissing))
            }
        }
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents, brand : Brand? = null, isUpdate : Boolean = false)
    }
}