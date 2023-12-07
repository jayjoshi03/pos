package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.OrderActivity
import com.varitas.gokulpos.tablet.adapter.ItemSpecificationAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDItemSpecificationBinding
import com.varitas.gokulpos.tablet.response.PriceList
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums

class ItemSpecificationPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDItemSpecificationBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private lateinit var itemSpecificationAdapter: ItemSpecificationAdapter
    private var dataList = ArrayList<PriceList>()

    companion object {
        fun newInstance(): ItemSpecificationPopupDialog {
            val f = ItemSpecificationPopupDialog()
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
        dialog?.window?.setLayout(resources.getInteger(R.integer.fragmentHeight), WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
                             ): View {
        binding = FragmentDItemSpecificationBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            if (arguments!!.getSerializable(Default.SPECIFICATION) != null) {
                dataList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) (arguments?.getSerializable(Default.SPECIFICATION))!! as ArrayList<PriceList>
                else arguments?.getSerializable(Default.SPECIFICATION)!! as ArrayList<PriceList>
            }
        }
        postInitViews()
        loadData()
        manageClicks()
    }

    //region To load data
    private fun loadData() {
        itemSpecificationAdapter.apply {
            setList(dataList)
        }
    }
    //endregion

    //region To post init views
    private fun postInitViews() {
        itemSpecificationAdapter = ItemSpecificationAdapter {
            onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.LOAD, it)
        }

        binding!!.apply {
            layoutToolbar.textViewTitle.text = resources.getString(R.string.Menu_Specification)
            recycleViewSpecificationList.apply {
                adapter = itemSpecificationAdapter
                layoutManager = GridLayoutManager(OrderActivity.Instance, 4)
            }
        }
    }
    //endregion

    //region To manage clicks
    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }
        }
    }
    //endregion

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, specification: PriceList? = null)
    }
}