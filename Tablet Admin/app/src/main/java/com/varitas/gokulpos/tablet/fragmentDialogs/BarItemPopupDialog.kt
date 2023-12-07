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
import com.varitas.gokulpos.tablet.adapter.BarItemAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDManageGroupBinding
import com.varitas.gokulpos.tablet.request.Attributes
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums

class BarItemPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDManageGroupBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private lateinit var barItemList: ArrayList<Attributes>
    private lateinit var barItemAdapter: BarItemAdapter
    private var isForEdit = false

    companion object {
        fun newInstance(): BarItemPopupDialog {
            val f = BarItemPopupDialog()
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
        binding = FragmentDManageGroupBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            barItemList = arguments?.getParcelableArrayList(Default.BAR_ITEM)!!
            isForEdit = arguments?.getBoolean(Default.EDIT)!!
        }
        initData()
        manageClicks()
        loadData()
        postInitViews()
    }

    private fun initData() {
        barItemAdapter = BarItemAdapter {
            barItemAdapter.apply {
                list.removeAt(it)
                notifyItemRemoved(it)
                notifyItemRangeChanged(it, list.size)
            }
        }
    }

    private fun postInitViews() {
        binding!!.apply {
            textViewItemName.visibility = View.GONE
            linearLayoutSpinner.visibility = View.GONE
            buttonAdd.visibility = View.GONE
            recycleViewSpecification.apply {
                adapter = barItemAdapter
                layoutManager = LinearLayoutManager(if(!isForEdit) AddProductActivity.Instance else EditProductActivity.Instance)
            }
        }
    }

    private fun loadData() {
        barItemAdapter.apply {
            setList(barItemList)
        }
    }

    private fun manageClicks() {
        binding!!.apply {
            buttonClose.clickWithDebounce {
                barItemAdapter.apply {
                    onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, list)
                }
            }
        }
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, items: ArrayList<Attributes>)
    }
}