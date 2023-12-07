package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.AddProductActivity
import com.varitas.gokulpos.tablet.activity.EditProductActivity
import com.varitas.gokulpos.tablet.adapter.ManageStockAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDFacilityquantityBinding
import com.varitas.gokulpos.tablet.response.Facility
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums

class FacilityQuantityPopupDialog : BaseDialogFragment() {
    private var mActivity: Activity? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private var binding: FragmentDFacilityquantityBinding? = null
    private lateinit var stockAdapter: ManageStockAdapter
    private var isFromEdit = false

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (context is Activity) mActivity = activity
    }

    companion object {
        fun newInstance(): FacilityQuantityPopupDialog {
            val f = FacilityQuantityPopupDialog()
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDFacilityquantityBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        val param = WindowManager.LayoutParams()
        param.copyFrom(dialog!!.window!!.attributes)
        param.width = resources.getInteger(R.integer.fragmentHeight)
        param.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = param
        //dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null)
            isFromEdit = arguments!!.getBoolean(Default.EDIT)
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun initData() {
        stockAdapter = ManageStockAdapter()
    }

    private fun loadData() {
        if (isFromEdit) {
            EditProductActivity.Instance.viewModelDropDown.fetchFacility().observe(EditProductActivity.Instance) {
                stockAdapter.apply {
                    setList(it)
                }
            }
        } else {
            AddProductActivity.Instance.viewModelFeature.fetchFacility().observe(AddProductActivity.Instance) {
                stockAdapter.apply {
                    setList(it)
                }
            }
        }
    }

    private fun postInitViews() {
        binding!!.layoutToolbar.textViewTitle.text = resources.getString(R.string.Menu_Stock)

        binding?.apply {
            recycleViewFacility.apply {
                adapter = stockAdapter
                layoutManager = LinearLayoutManager(if (isFromEdit) EditProductActivity.Instance else AddProductActivity.Instance)
            }
        }

    }

    private fun manageClicks() {
        binding!!.layoutToolbar.imageViewCancel.clickWithDebounce {
            onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL, ArrayList())
        }
        binding?.apply {
            buttonUpdate.setOnClickListener {
                stockAdapter.apply {
                    onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.UPDATE, list)
                }
            }
        }
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, list: ArrayList<Facility>)
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}