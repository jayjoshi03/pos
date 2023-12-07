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
import com.varitas.gokulpos.tablet.adapter.SetupMenuAdapter
import com.varitas.gokulpos.tablet.databinding.FragmentDItemSpecificationBinding
import com.varitas.gokulpos.tablet.utilities.Default

class SetupMenuPopupDialog : BaseDialogFragment() {

    private var mActivity: Activity? = null
    private var binding: FragmentDItemSpecificationBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private lateinit var setupMenuAdapter: SetupMenuAdapter
    private var dataList = ArrayList<String>()

    companion object {
        fun newInstance(): SetupMenuPopupDialog {
            val f = SetupMenuPopupDialog()
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
            if (arguments!!.getSerializable(Default.MENU) != null) {
                dataList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) (arguments?.getSerializable(Default.MENU))!! as ArrayList<String>
                else arguments?.getSerializable(Default.MENU)!! as ArrayList<String>
            }
        }
        postInitViews()
        loadData()
        manageClicks()
    }

    //region To load data
    private fun loadData() {
        setupMenuAdapter.apply {
            setList(dataList)
        }
    }
    //endregion

    //region To post init views
    private fun postInitViews() {
        setupMenuAdapter = SetupMenuAdapter {
            onButtonClickListener?.onButtonClickListener(it)
        }

        binding!!.apply {
            layoutToolbar.textViewTitle.text = resources.getString(R.string.Menu)
            recycleViewSpecificationList.apply {
                adapter = setupMenuAdapter
                layoutManager = GridLayoutManager(OrderActivity.Instance, 4)
            }
        }
    }
    //endregion

    //region To manage clicks
    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener()
            }
        }
    }
    //endregion

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(specification: String? = null)
    }
}