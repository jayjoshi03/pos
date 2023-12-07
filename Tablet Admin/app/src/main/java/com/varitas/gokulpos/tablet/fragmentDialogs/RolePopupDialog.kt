package com.varitas.gokulpos.tablet.fragmentDialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.activity.RoleActivity
import com.varitas.gokulpos.tablet.databinding.FragmentDRoleBinding
import com.varitas.gokulpos.tablet.response.Role
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Enums

class RolePopupDialog : BaseDialogFragment() {

    private var mActivity : Activity? = null
    private var binding : FragmentDRoleBinding? = null
    private var onButtonClickListener : OnButtonClickListener? = null
    private var roleDetails : Role? = null

    companion object {
        fun newInstance() : RolePopupDialog {
            val f = RolePopupDialog()
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
        dialog?.window?.setLayout(resources.getInteger(R.integer.fragmentHeight), WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?,
    ) : View {
        binding = FragmentDRoleBinding.inflate(LayoutInflater.from(context))
        return binding!!.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val roleParcelable = arguments?.getParcelable<Role>(Default.ROLE)
            if (roleParcelable != null) roleDetails = roleParcelable
        }
        postInitViews()
        loadData()
        manageClicks()
    }

    private fun loadData() {
        RoleActivity.Instance.viewModel.showProgress.observe(this) {
            RoleActivity.Instance.manageProgress(it)
        }
        if(roleDetails != null) binding!!.textInputRoleName.setText(roleDetails!!.name!!)
    }

    private fun postInitViews() {
        binding!!.layoutToolbar.textViewTitle.text = resources.getString(R.string.Role, if(roleDetails == null) resources.getString(R.string.lbl_Add) else resources.getString(R.string.lbl_Edit))
    }

    private fun manageClicks() {
        binding!!.apply {
            layoutToolbar.imageViewCancel.clickWithDebounce {
                onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.CANCEL)
            }

            buttonSave.clickWithDebounce {
                val roleName = textInputRoleName.text.toString().trim()
                if(roleName.isNotEmpty()) {
                    if(roleDetails == null) {
                        val req = Role(
                            name = roleName
                        )
                        RoleActivity.Instance.viewModel.insertRole(req).observe(RoleActivity.Instance) {
                            if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE)
                        }
                    } else {
                        val req = Role(
                            id = roleDetails!!.id,
                            name = roleName
                        )
                        RoleActivity.Instance.viewModel.updateRole(req).observe(RoleActivity.Instance) {
                            if(it) onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.SAVE, Role(name = roleName), isUpdate = true)
                        }
                    }
                } else RoleActivity.Instance.showSweetDialog(RoleActivity.Instance, resources.getString(R.string.Validation, resources.getString(R.string.lbl_Role)))
            }
        }
    }

    fun setListener(listener : OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton : Enums.ClickEvents, role : Role? = null, isUpdate : Boolean = false)
    }

}