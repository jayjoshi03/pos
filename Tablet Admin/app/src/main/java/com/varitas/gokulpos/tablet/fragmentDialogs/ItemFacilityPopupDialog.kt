package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.AddProductActivity
import com.varitas.gokulpos.tablet.activity.EditProductActivity
import com.varitas.gokulpos.tablet.adapter.FacilityStockAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDItemSpecificationBinding
import com.varitas.gokulpos.tablet.response.Facility
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums

class ItemFacilityPopupDialog : BaseDialogFragment() {
    private var mActivity: Activity? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private var binding: FragmentDItemSpecificationBinding? = null
    private lateinit var facilityStockAdapter: FacilityStockAdapter
    private lateinit var facilities: ArrayList<Facility>
    private var isEdit: Boolean = false

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (context is Activity) mActivity = activity
    }

    companion object {
        fun newInstance(): ItemFacilityPopupDialog {
            val f = ItemFacilityPopupDialog()
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDItemSpecificationBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        val param = WindowManager.LayoutParams()
        param.copyFrom(dialog!!.window!!.attributes)
        param.width = resources.getInteger(R.integer.fragmentHeight)
        param.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = param
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            if (arguments!!.getSerializable(Default.FACILITY) != null) {
                facilities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) (arguments?.getSerializable(Default.FACILITY))!! as ArrayList<Facility>
                else arguments?.getSerializable(Default.FACILITY)!! as ArrayList<Facility>
            }
            isEdit = arguments?.getBoolean(Default.EDIT)!!
        }
        initData()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun initData() {
        facilityStockAdapter = FacilityStockAdapter()
    }

    private fun loadData() {
        facilityStockAdapter.apply {
            setList(facilities)
        }
    }

    private fun postInitViews() {
        binding!!.layoutToolbar.root.visibility = View.GONE

        binding?.apply {
            recycleViewSpecificationList.apply {
                adapter = facilityStockAdapter
                layoutManager = LinearLayoutManager(if (!isEdit) AddProductActivity.Instance else EditProductActivity.Instance)
            }
        }

    }

    private fun manageClicks() {
        binding!!.layoutToolbar.imageViewCancel.clickWithDebounce {
            onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
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