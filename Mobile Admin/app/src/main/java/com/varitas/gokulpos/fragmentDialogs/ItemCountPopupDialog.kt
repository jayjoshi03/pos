package com.varitas.gokulpos.fragmentDialogs

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.R
import com.varitas.gokulpos.activity.BrandActivity
import com.varitas.gokulpos.activity.CategoryActivity
import com.varitas.gokulpos.activity.DepartmentActivity
import com.varitas.gokulpos.adapter.ItemCountAdapter
import com.varitas.gokulpos.databinding.FragmentDItemSpecificationBinding
import com.varitas.gokulpos.model.Header
import com.varitas.gokulpos.utilities.Default
import com.varitas.gokulpos.utilities.Enums
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
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
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
            layoutToolbar.textViewTitle.text = resources.getString(R.string.lbl_ItemsList)
            recycleViewSpecificationList.apply {
                adapter = itemCountAdapter
                layoutManager = LinearLayoutManager(context)
            }
            layoutHeader.data = Header(resources.getString(R.string.lbl_Id), resources.getString(R.string.hint_ItemName))
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
                    if(it.isNotEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            itemCountAdapter.apply {
                                setList(it)
                            }
                        }
                    } else onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
                }
            }
        }
    }

    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }
        }
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents)
    }
}