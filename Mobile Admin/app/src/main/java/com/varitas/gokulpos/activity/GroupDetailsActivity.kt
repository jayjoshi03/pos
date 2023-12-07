package com.varitas.gokulpos.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.R
import com.varitas.gokulpos.adapter.TaxSelectorAdapter
import com.varitas.gokulpos.databinding.ActivityGroupDetailsBinding
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.request.GroupInsertUpdateRequest
import com.varitas.gokulpos.response.CommonDropDown
import com.varitas.gokulpos.utilities.CustomSpinner
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.viewmodel.ProductFeatureViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupDetailsActivity : BaseActivity() {
    lateinit var binding : ActivityGroupDetailsBinding
    val viewModel : ProductFeatureViewModel by viewModels()
    private lateinit var groupTypeList : ArrayList<CommonDropDown>
    private lateinit var groupTypeSpinner : ArrayAdapter<CommonDropDown>
    private lateinit var groupType : CommonDropDown
    private var groupId = 0
    private lateinit var notSelectorAdapter : TaxSelectorAdapter
    private lateinit var selectorAdapter : TaxSelectorAdapter
    private lateinit var groupDetail : GroupInsertUpdateRequest

    companion object {
        lateinit var Instance : GroupDetailsActivity
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.layoutToolbar.toolbarCommon)
        initData()
        postInitView()
        loadData()
        manageClicks()
    }

    //region To init data
    private fun initData() {
        groupDetail = GroupInsertUpdateRequest()
        groupId = if(intent.extras?.getInt(Default.ID) != null) intent.extras?.getInt(Default.ID)!! else 0
        groupTypeList = ArrayList()
        groupTypeSpinner = ArrayAdapter(this, R.layout.spinner_items, groupTypeList)
        notSelectorAdapter = TaxSelectorAdapter()
        selectorAdapter = TaxSelectorAdapter()
    }

    private fun postInitView() {
        binding.apply {
            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Group, if(groupId == 0) resources.getString(R.string.lbl_Add) else resources.getString(R.string.lbl_Edit))
            layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@GroupDetailsActivity, R.drawable.ic_save))
            layoutToolbar.imageViewBack.visibility = View.VISIBLE
            header = Header(resources.getString(R.string.hint_Name), "", "")
            id = groupId
            recycleViewNonSelect.apply {
                adapter = notSelectorAdapter
                layoutManager = LinearLayoutManager(this@GroupDetailsActivity)
            }
            recycleViewSelect.apply {
                adapter = selectorAdapter
                layoutManager = LinearLayoutManager(this@GroupDetailsActivity)
            }
        }
    }
    //endregion

    //region To manage click
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewBack.clickWithDebounce {
                openActivity(Intent(this@GroupDetailsActivity, GroupActivity::class.java))
            }

            layoutToolbar.imageViewAction.clickWithDebounce {
                manageGroupInsert()
            }

            spinnerGroupType.apply {
                adapter = groupTypeSpinner
                var previousSelectedItem : CommonDropDown? = null
                setSpinnerEventsListener(object : CustomSpinner.OnSpinnerEventsListener {
                    override fun onSpinnerOpened(spinner : Spinner?) {
                        previousSelectedItem = spinner?.selectedItem as? CommonDropDown
                    }

                    override fun onSpinnerClosed(spinner : Spinner?) {
                        groupType = spinner?.selectedItem as CommonDropDown
                        checkBoxShortcut.isEnabled = groupType.value == Default.TYPE_ITEM
                        if(groupType != previousSelectedItem) {
                            selectorAdapter.apply {
                                if(list.isNotEmpty()) {
                                    list.clear()
                                    notifyDataSetChanged()
                                }
                            }
                            manageGroupType(groupType.value!!)
                        }
                    }
                })
            }

            buttonAdd.clickWithDebounce {
                notSelectorAdapter.apply {
                    val filteredList = list.filter { it.isGroup == true }
                    list.removeAll(filteredList.toSet())
                    notifyDataSetChanged()

                    selectorAdapter.apply {
                        for(d in filteredList) {
                            d.isGroup = false
                            list.add(0, d)
                        }
                        notifyItemRangeInserted(0, filteredList.size)
                    }
                }
            }

            buttonAddAll.clickWithDebounce {
                notSelectorAdapter.apply {
                    val removedItems = ArrayList(list)
                    list.clear()
                    notifyDataSetChanged()

                    selectorAdapter.apply {
                        for(d in removedItems) {
                            d.isGroup = false
                            list.add(0, d)
                        }
                        notifyItemRangeInserted(0, removedItems.size)
                    }
                }
            }

            buttonRemove.clickWithDebounce {
                selectorAdapter.apply {
                    val filteredList = list.filter { it.isGroup == true }
                    list.removeAll(filteredList.toSet())
                    notifyDataSetChanged()

                    notSelectorAdapter.apply {
                        for(d in filteredList) {
                            d.isGroup = false
                            list.add(0, d)
                        }
                        notifyItemRangeInserted(0, filteredList.size)
                    }
                }
            }

            buttonRemoveAll.clickWithDebounce {
                selectorAdapter.apply {
                    val removedItems = ArrayList(list)
                    list.clear()
                    notifyDataSetChanged()

                    notSelectorAdapter.apply {
                        for(d in removedItems) {
                            d.isGroup = false
                            list.add(0, d)
                        }
                        notifyItemRangeInserted(0, removedItems.size)
                    }
                }
            }
        }
    }
    //endregion

    @Deprecated("Deprecated in Java", ReplaceWith("onBackPressedDispatcher.onBackPressed()")) override fun onBackPressed() {
        openActivity(Intent(this@GroupDetailsActivity, GroupActivity::class.java))
        onBackPressedDispatcher.onBackPressed() //with this line
    }

    //region To load data
    private fun loadData() {
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }
        viewModel.errorMsg.observe(this) {
            showSweetDialog(this, it)
        }

        if(groupId > 0) {
            viewModel.getGroupTypeDetails(groupId).observe(this) {
                CoroutineScope(Dispatchers.Main).launch {
                    if(it != null) {
                        groupDetail = it
                        binding.apply {
                            editTextName.setText(groupDetail.name)
                            editTextCode.setText(groupDetail.code)
                            layoutToolbar.textViewToolbarName.text = resources.getString(R.string.Group, groupDetail.sType)

                            checkBoxShortcut.isEnabled = groupDetail.type == Default.TYPE_ITEM
                            checkBoxShortcut.isChecked = if(groupDetail.isShortcut != null) groupDetail.isShortcut!! else false
                            notSelectorAdapter.apply {
                                setList(it.sortedData!!.nonSelectedItems)
                            }
                            selectorAdapter.apply {
                                for(j in it.sortedData!!.selectedItems) j.isGroup = false
                                setList(it.sortedData!!.selectedItems)
                            }
                        }
                    }
                }
            }
        } else {
            viewModel.getGroupTypeDropdown(1).observe(this) {
                CoroutineScope(Dispatchers.Main).launch {
                    groupTypeSpinner.apply {
                        setDropDownViewResource(R.layout.spinner_dropdown_item)
                        addAll(it)
                        groupType = it[0]
                        notifyDataSetChanged()
                        manageGroupType(groupType.value!!)
                    }
                }
            }
        }
    }

    private fun manageGroupType(type : Int) {
        viewModel.getGroupTypeList(type).observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                notSelectorAdapter.apply {
                    list.clear()
                    notifyDataSetChanged()
                    setList(it)
                }
            }
        }
    }
    //endregion

    //region To manage group insert/update
    private fun manageGroupInsert() {
        binding.apply {
            val name = editTextName.text.toString().trim()
            selectorAdapter.apply {
                if(!TextUtils.isEmpty(name) && list.isNotEmpty()) {
                    val req = GroupInsertUpdateRequest(
                        id = groupId,
                        name = name,
                        isShortcut = checkBoxShortcut.isChecked,
                        code = editTextCode.text.toString().trim(),
                        type = if(groupId == 0) groupType.value else groupDetail.type,
                        data = list
                    )

                    if(groupId == 0) {
                        viewModel.insertGroup(req).observe(this@GroupDetailsActivity) {
                            CoroutineScope(Dispatchers.Main).launch {
                                if(it) openActivity(Intent(this@GroupDetailsActivity, GroupActivity::class.java))
                            }
                        }
                    } else {
                        viewModel.updateGroup(req).observe(this@GroupDetailsActivity) {
                            CoroutineScope(Dispatchers.Main).launch {
                                if(it) openActivity(Intent(this@GroupDetailsActivity, GroupActivity::class.java))
                            }
                        }
                    }
                } else if(TextUtils.isEmpty(name)) showSweetDialog(this@GroupDetailsActivity, resources.getString(R.string.lbl_NameMissing))
                else showSweetDialog(this@GroupDetailsActivity, resources.getString(R.string.lbl_groupValidation))
            }
        }
    }
    //endregion
}