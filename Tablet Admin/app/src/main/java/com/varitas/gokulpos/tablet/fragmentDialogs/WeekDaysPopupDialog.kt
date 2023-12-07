package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.varitas.gokulpos.tablet.activity.PromotionAddActivity
import com.varitas.gokulpos.tablet.adapter.WeekDaysAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDManageGroupBinding
import com.varitas.gokulpos.tablet.response.DataDetails
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums

class WeekDaysPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDManageGroupBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private lateinit var weekDaysList: ArrayList<DataDetails>
    private lateinit var weekDaysAdapter: WeekDaysAdapter

    companion object {
        fun newInstance(): WeekDaysPopupDialog {
            val f = WeekDaysPopupDialog()
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
        if (arguments != null) weekDaysList = arguments?.getParcelableArrayList(Default.WEEK)!!
        initData()
        manageClicks()
        loadData()
        postInitViews()
    }

    private fun initData() {
        weekDaysAdapter = WeekDaysAdapter()
    }

    private fun postInitViews() {
        binding!!.apply {
            textViewItemName.visibility = View.GONE
            linearLayoutSpinner.visibility = View.GONE
            recycleViewSpecification.apply {
                adapter = weekDaysAdapter
                layoutManager = LinearLayoutManager(PromotionAddActivity.Instance)
            }
        }
    }

    private fun loadData() {
        weekDaysAdapter.apply {
            setList(weekDaysList)
        }
    }

    private fun manageClicks() {
        binding!!.apply {
            buttonClose.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL, ArrayList())
            }

            buttonAdd.clickWithDebounce {
                weekDaysAdapter.apply {
                    onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, list)
                }
            }
        }
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents, days: ArrayList<DataDetails>)
    }
}