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
import com.varitas.gokulpos.tablet.adapter.OrdersAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDResumeBinding
import com.varitas.gokulpos.tablet.model.Header
import com.varitas.gokulpos.tablet.response.OrderItemDetails
import com.varitas.gokulpos.tablet.response.OrderList
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResumePopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDResumeBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private lateinit var ordersAdapter: OrdersAdapter

    companion object {
        fun newInstance(): ResumePopupDialog {
            val f = ResumePopupDialog()
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

        OrderActivity.Instance.viewModel.fetchHoldOrders().observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                ordersAdapter.apply {
                    setList(it)
                }
            }
        }
    }
    //endregion

    //region To post init views
    private fun postInitViews() {
        ordersAdapter = OrdersAdapter { order, pos, enum ->
            when (enum) {
                Enums.ClickEvents.SAVE -> draftOrder(order.id!!, pos)
                Enums.ClickEvents.LOAD -> onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.LOAD, order)
                Enums.ClickEvents.PRINT -> onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.PRINT, order)
                Enums.ClickEvents.DELETE -> deleteHoldOrder(order.id!!, pos)
                Enums.ClickEvents.ITEMS -> manageItems(order)
                else -> {}
            }
        }

        binding!!.apply {
            layoutToolbar.textViewTitle.text = resources.getString(R.string.Menu_HoldOrder)
            recycleViewHoldList.apply {
                adapter = ordersAdapter
                layoutManager = LinearLayoutManager(OrderActivity.Instance)
            }
            data = Header(id = resources.getString(R.string.lbl_OrderNumber), title = resources.getString(R.string.lbl_Date), title2 = resources.getString(R.string.lbl_Customer), title3 = resources.getString(R.string.lbl_Total), title4 = resources.getString(R.string.lbl_Action))
        }
    }
    //endregion

    //region To display items
    private fun manageItems(order: OrderList) {
        val ft = OrderActivity.Instance.supportFragmentManager.beginTransaction()
        val dialogFragment = ItemsPopupDialog.newInstance()
        val prevFragment: Fragment? = OrderActivity.Instance.supportFragmentManager.findFragmentByTag(ItemsPopupDialog::class.java.name)
        if (prevFragment != null) return
        val bundle = Bundle()
        bundle.putParcelable(Default.ORDER, order)
        bundle.putBoolean(Default.COMPLETE, false)
        dialogFragment.arguments = bundle
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, ItemsPopupDialog::class.java.name)
        dialogFragment.setListener(object : ItemsPopupDialog.OnButtonClickListener {
            override fun onButtonClickListener(typeButton: Enums.ClickEvents, selectedItems: List<OrderItemDetails>) {
                if (dialogFragment.isVisible) dialogFragment.dismiss()
            }
        })
    }
    //endregion

    //region To save an order as draft
    private fun draftOrder(id : Int, pos : Int){
        OrderActivity.Instance.viewModel.draftOrder(id).observe(OrderActivity.Instance){
            if(it) manageItems(pos)
        }
    }
    //endregion

    //region To delete hold order
    private fun deleteHoldOrder(id : Int, pos : Int){
        OrderActivity.Instance.viewModel.deleteHoldOrder(id).observe(OrderActivity.Instance){
            if(it) manageItems(pos)
        }
    }
    //endregion

    //region To manage listviews
    private fun manageItems(pos: Int){
        ordersAdapter.apply {
            list.removeAt(pos)
            notifyItemRemoved(pos)
            notifyItemRangeChanged(pos, list.size)
        }
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
                    ordersAdapter.getFilter().filter(newText)
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
        fun onButtonClickListener(typeButton: Enums.ClickEvents, order: OrderList? = null)
    }
}