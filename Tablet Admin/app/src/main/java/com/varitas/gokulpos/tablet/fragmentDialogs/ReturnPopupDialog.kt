package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.OrderActivity
import com.varitas.gokulpos.tablet.adapter.ReturnAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDResumeBinding
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.response.CompletedOrder
import com.varitas.gokulpos.tablet.response.OrderItemDetails
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReturnPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDResumeBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private lateinit var returnAdapter: ReturnAdapter

    companion object {
        fun newInstance(): ReturnPopupDialog {
            val f = ReturnPopupDialog()
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
        dialog?.window?.setLayout(resources.getInteger(R.integer.fragmentWidth), resources.getInteger(R.integer.fragmentHeight))
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
                             ): View {
        binding = FragmentDResumeBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postInitViews()
        loadData()
        manageClicks()
    }

    //region To load data
    private fun loadData() {
        OrderActivity.Instance.viewModel.showProgress.observe(this){
            OrderActivity.Instance.manageProgress(it)
        }

        OrderActivity.Instance.viewModel.fetchCompleteOrders().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                returnAdapter.apply {
                    setList(it)
                }
            }
        }
    }
    //endregion

    //region To post init views
    private fun postInitViews() {
        returnAdapter = ReturnAdapter { order, _, enum ->
            when (enum) {
                Enums.ClickEvents.PRINT -> onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.PRINT, order)
                Enums.ClickEvents.ITEMS -> manageItems(order)
                else -> {}
            }
        }

        binding!!.apply {
            layoutToolbar.textViewTitle.text = resources.getString(R.string.button_Return)
            recycleViewHoldList.apply {
                adapter = returnAdapter
                layoutManager = LinearLayoutManager(OrderActivity.Instance)
            }
            data = Header(id = resources.getString(R.string.lbl_OrderNumber), title = resources.getString(R.string.lbl_Date), title2 = resources.getString(R.string.lbl_Customer), title3 = resources.getString(R.string.lbl_Total), title4 = resources.getString(R.string.lbl_Action))
        }
    }
    //endregion

    //region To display items
    private fun manageItems(order: CompletedOrder) {
        val ft = OrderActivity.Instance.supportFragmentManager.beginTransaction()
        val dialogFragment = ItemsPopupDialog.newInstance()
        val prevFragment: Fragment? = OrderActivity.Instance.supportFragmentManager.findFragmentByTag(ItemsPopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putParcelable(Default.ORDER, order)
        bundle.putBoolean(Default.COMPLETE, true)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, ItemsPopupDialog::class.java.name)
        dialogFragment.setListener(object : ItemsPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, selectedItems: List<OrderItemDetails>) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
                when (typeButton) {
                    Enums.ClickEvents.LOAD -> onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.LOAD, order)
                    else -> {}
                }
            }
        })
    }
    //endregion

    //region To manage clicks
    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }
            searchOrder.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    returnAdapter.getFilter().filter(newText)
                    return true
                }
            })
        }
    }
    //endregion

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, order: CompletedOrder? = null)
    }
}