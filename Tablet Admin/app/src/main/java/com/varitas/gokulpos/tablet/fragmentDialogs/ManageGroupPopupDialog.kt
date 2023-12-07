package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.ProductListActivity
import com.varitas.gokulpos.tablet.adapter.SpecificationSelectorAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDManageGroupBinding
import com.varitas.gokulpos.tablet.request.GroupDetails
import com.varitas.gokulpos.tablet.request.ItemGroupRequest
import com.varitas.gokulpos.tablet.response.CommonDropDown
import com.varitas.gokulpos.tablet.utilities.CustomSpinner
import com.varitas.gokulpos.tablet.utilities.Default
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManageGroupPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDManageGroupBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private var id: Int = 0
    private var name: String = ""
    private lateinit var groupList: ArrayList<CommonDropDown>
    private lateinit var groupSpinner: ArrayAdapter<CommonDropDown>
    private lateinit var group: CommonDropDown
    private lateinit var specificationSelectorAdapter: SpecificationSelectorAdapter

    companion object {
        fun newInstance(): ManageGroupPopupDialog {
            val f = ManageGroupPopupDialog()
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
        binding = FragmentDManageGroupBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            id = arguments!!.getInt(Default.ID)
            name = arguments!!.getString(Default.PRODUCT)!!
        }
        initData()
        manageClicks()
        loadData()
        postInitViews()
    }

    private fun initData() {
        groupList = ArrayList()
        groupSpinner = ArrayAdapter(ProductListActivity.Instance, R.layout.spinner_items, groupList)
        specificationSelectorAdapter = SpecificationSelectorAdapter()
    }

    private fun postInitViews() {
        binding!!.apply {
            textViewItemName.text = resources.getString(R.string.hint_ItemName) + ": $name"
            recycleViewSpecification.apply {
                adapter = specificationSelectorAdapter
                layoutManager = LinearLayoutManager(ProductListActivity.Instance)
            }
        }
    }

    private fun loadData() {

        ProductListActivity.Instance.viewModelFeature.showProgress.observe(this) {
            ProductListActivity.Instance.manageProgress(it)
        }

        ProductListActivity.Instance.viewModelFeature.fetchGroupsById(Default.TYPE_ITEM).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                groupSpinner.apply {
                    setDropDownViewResource(R.layout.spinner_dropdown_item)
                    group = CommonDropDown(0, resources.getString(R.string.lbl_SelectGroup))
                    add(group)
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }

        ProductListActivity.Instance.viewModelFeature.getItemSpecification(id).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                specificationSelectorAdapter.apply {
                    setList(it)
                }
            }
        }
    }

    private fun manageClicks() {
        binding!!.apply {
            buttonClose.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener()
            }

            buttonAdd.clickWithDebounce {
                specificationSelectorAdapter.apply {
                    val isSpecificationChecked = list.indexOfFirst { it.isSelected } >= 0
                    if (group.value!! > 0 && isSpecificationChecked) {
                        val detailList = ArrayList<GroupDetails>()
                        for (data in list) {
                            if (data.isSelected) detailList.add(GroupDetails(id = id, specificationId = data.value))
                        }

                        val req = ItemGroupRequest(
                            id = group.value!!,
                            data = detailList)

                        ProductListActivity.Instance.viewModelFeature.insertItemToGroup(req).observe(this@ManageGroupPopupDialog) {
                            if (it) onButtonClickListener?.onButtonClickListener()
                        }
                    } else return@clickWithDebounce
                }
            }

            spinnerGroup.apply {
                adapter = groupSpinner
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner: Spinner?) {

                    }

                    override fun onSpinnerClosed(spinner: Spinner?) {
                        group = spinner?.selectedItem as CommonDropDown
                    }
                })
            }
        }
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener()
    }
}