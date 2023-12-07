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
import com.varitas.gokulpos.tablet.activity.OrderActivity
import com.varitas.gokulpos.tablet.adapter.ItemAttributeAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDItemSpecificationBinding
import com.varitas.gokulpos.tablet.response.GroupId
import com.varitas.gokulpos.tablet.response.ItemAttributes
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums

class ItemAttributePopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDItemSpecificationBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private lateinit var itemAttributeAdapter: ItemAttributeAdapter
    private var dataList = ArrayList<ItemAttributes>()

    companion object {
        fun newInstance(): ItemAttributePopupDialog {
            val f = ItemAttributePopupDialog()
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
            if (arguments!!.getSerializable(Default.ATTRIBUTE) != null) {
                dataList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) (arguments?.getSerializable(Default.ATTRIBUTE))!! as ArrayList<ItemAttributes>
                else arguments?.getSerializable(Default.ATTRIBUTE)!! as ArrayList<ItemAttributes>
            }
        }
        postInitViews()
        loadData()
        manageClicks()
    }

    //region To load data
    private fun loadData() {
        itemAttributeAdapter.apply {
            setList(dataList)
        }
    }
    //endregion

    //region To post init views
    private fun postInitViews() {
        itemAttributeAdapter = ItemAttributeAdapter {

        }

        binding!!.apply {
            layoutToolbar.textViewTitle.text = resources.getString(R.string.Menu_Attribute)
            buttonSave.text = resources.getString(R.string.button_Save)
            linearLayoutButtons.visibility = View.VISIBLE
            recycleViewSpecificationList.apply {
                adapter = itemAttributeAdapter
                layoutManager = LinearLayoutManager(OrderActivity.Instance)
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

            buttonSave.clickWithDebounce {
                val selectedGroupId = mutableListOf<GroupId>()
                val notSelectedGroupId = mutableListOf<GroupId>()

                itemAttributeAdapter.apply {
                    for (data in list) {
                        for (detail in data.listAttributes) {
                            for (j in detail.groups) {
                                val groupId = GroupId(j.id!!, j.specificationId!!)
                                if (detail.isSelected)
                                    selectedGroupId.add(groupId)
                                else notSelectedGroupId.add(groupId)
                            }
                        }
                    }

                    val commonIds = selectedGroupId.filter { selectedGroup ->
                        notSelectedGroupId.any { notSelectedGroup -> selectedGroup.id == notSelectedGroup.id }
                    }

                    for (ids in commonIds) {
                        val index = selectedGroupId.indexOfFirst { it.id == ids.id && it.specId == ids.specId }
                        if (index >= 0) selectedGroupId.removeAt(index)
                    }

                    for (ids in commonIds) {
                        val index = notSelectedGroupId.indexOfFirst { it.id == ids.id && it.specId == ids.specId }
                        if (index >= 0) notSelectedGroupId.removeAt(index)
                    }

                    if (selectedGroupId.size > 0)
                        onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.LOAD, selectedGroupId[0].specId)
                    else OrderActivity.Instance.showSweetDialog(OrderActivity.Instance, resources.getString(R.string.lbl_CombinationNotMatched))
                }
            }

            buttonClose.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }
        }
    }
    //endregion

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, specId: Int? = null)
    }
}