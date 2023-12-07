package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.BrandActivity
import com.varitas.gokulpos.tablet.activity.CategoryActivity
import com.varitas.gokulpos.tablet.activity.DepartmentActivity
import com.varitas.gokulpos.tablet.activity.GroupActivity
import com.varitas.gokulpos.tablet.adapter.ItemCountAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDItemSpecificationBinding
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums
import com.varitas.gokulpos.tablet.utilities.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable

class ItemCountPopupDialog : BaseDialogFragment() {

    private var mActivity : Activity? = null
    private var binding : FragmentDItemSpecificationBinding? = null
    private var onButtonClickListener : OnButtonClickListener? = null
    private var id : Int? = null
    private var itemCount : Serializable? = null
    private lateinit var itemCountAdapter : ItemCountAdapter


    companion object {
        fun newInstance() : ItemCountPopupDialog {
            val f = ItemCountPopupDialog()
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
        dialog?.window?.setLayout(resources.getInteger(R.integer.fragmentHeight), resources.getInteger(R.integer.receiptHeight))
    }

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?,
    ) : View {
        binding = FragmentDItemSpecificationBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null) {
            if(arguments!!.getSerializable(Default.ITEMCOUNT) != null) {
                itemCount = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arguments?.getSerializable(Default.ITEMCOUNT, Serializable::class.java)!!
                else arguments?.getSerializable(Default.ITEMCOUNT)!!
            }
            id = arguments?.getInt(Default.ID)
        }
        itemCountAdapter = ItemCountAdapter()
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun postInitViews() {
        binding!!.apply {
            layoutToolbar.root.visibility = View.GONE
            linearLayoutCounts.visibility = View.VISIBLE
            recycleViewSpecificationList.apply {
                adapter = itemCountAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    private fun loadData() {
        when(itemCount) {
            Enums.Menus.DEPARTMENT -> {
                DepartmentActivity.Instance.viewModel.showProgress.observe(this) {
                    DepartmentActivity.Instance.manageProgress(it)
                }
                DepartmentActivity.Instance.viewModel.getDepartmentItems(id!!).observe(DepartmentActivity.Instance) {
                    if(it.isNotEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            itemCountAdapter.apply {
                                setList(it)
                            }
                        }
                    } else onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
                }
            }

            Enums.Menus.CATEGORY -> {
                CategoryActivity.Instance.viewModel.showProgress.observe(this) {
                    CategoryActivity.Instance.manageProgress(it)
                }
                CategoryActivity.Instance.viewModel.getCategoryItems(id!!).observe(CategoryActivity.Instance) {
                    if(it.isNotEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            itemCountAdapter.apply {
                                setList(it)
                            }
                        }
                    } else onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
                }
            }

            Enums.Menus.BRAND -> {
                BrandActivity.Instance.viewModel.showProgress.observe(this) {
                    BrandActivity.Instance.manageProgress(it)
                }
                BrandActivity.Instance.viewModel.getBrandItems(id!!).observe(BrandActivity.Instance) {
                    if (it.isNotEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            itemCountAdapter.apply {
                                setList(it)
                            }
                        }
                    } else onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
                }
            }

            Enums.Menus.GROUP -> {
                GroupActivity.Instance.orderViewModel.showProgress.observe(this) {
                    GroupActivity.Instance.manageProgress(it)
                }
                lifecycleScope.launch {
                    try {
                        val result = GroupActivity.Instance.orderViewModel.fetchShortcutGroupItems(id!!)
                        if (result.isNotEmpty()) {
                            CoroutineScope(Dispatchers.Main).launch {
                                itemCountAdapter.apply {
                                    setList(result)
                                }
                            }
                        } else onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
                    } catch (ex: Exception) {
                        Utils.printAndWriteException(ex)
                    }
                }
            }
        }
    }

    private fun manageClicks() {
        binding!!.apply {

        }
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents)
    }
}