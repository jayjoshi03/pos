package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.FragmentDAgecheckBinding
import com.varitas.gokulpos.tablet.utilities.DriverLicenseParser
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.PlateData

class AgeCheckPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private var binding: FragmentDAgecheckBinding? = null
    private lateinit var licenseData: PlateData

    @Deprecated("Deprecated in Java")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (context is Activity) mActivity = activity
    }

    companion object {
        fun newInstance(): AgeCheckPopupDialog {
            val f = AgeCheckPopupDialog()
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDAgecheckBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        val param = WindowManager.LayoutParams()
        param.copyFrom(dialog!!.window!!.attributes)
        param.width = resources.getInteger(R.integer.fragmentInWidth)
        param.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = param
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        postInitViews()
        manageClicks()
    }

    private fun initData() {
        licenseData = PlateData()
    }

    private fun postInitViews() {
        binding!!.apply {
            layoutToolbar.textViewTitle.text = resources.getString(R.string.Menu_AgeCheck)
            textInputLicense.requestFocus()
        }
    }

    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonSave.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, licenseData)
            }

            buttonCapture.clickWithDebounce {
                val input = "@ANSI 636036040202DL00410275ZN03160051DLDAQP07956337301902DCSPATELDDENDACPRATIKKUMARDDFNDADLALITBHAIDDGNDCADDCB1DCDNONEDBD08272021DBB01171990DBA01172025DBC1DAU069 INDAYBRODAG36 DARTMOUTH WAYDAINORTHBRUNSWICKDAJNJDAK089024287  DCFDE202123900000167DCGUSADCKP2TJ39SWPANJJ08M01DDANDDB01082020ZNZNAKSZNB24.00ZNCRENZNDZNEZNFBRNZNGZNHZNI"
                textInputLicense.text!!.clear()
                textInputLicense.setText(input)
                if (!TextUtils.isEmpty(textInputLicense.text.toString().trim())) {
                    licenseData = DriverLicenseParser.parseDriverLicense(input)
                    // Parse the date of birth and expiry date strings into LocalDate objects
                    textInputBirthDate.setText(licenseData.dobString)
                    textInputAge.setText(if (licenseData.age.toString().length > 1) licenseData.age.toString() else "0${licenseData.age}")
                    textInputName.setText(licenseData.name)
                    textInputAddress.setText(licenseData.address)
                    textInputCity.setText(licenseData.city)
                    textInputState.setText(licenseData.state)
                    textInputZip.setText(licenseData.postcode)
                }
            }
        }
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, data: PlateData? = null)
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}