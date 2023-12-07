package com.varitas.gokulpos.tablet.fragmentDialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.varitas.gokulpos.tablet.databinding.DeleteAlertBinding
import com.varitas.gokulpos.tablet.utilities.Enums

class LogoutAlertPopupDialog(private val message: String) : BaseDialogFragment() {

    private var _binding: DeleteAlertBinding? = null
    private var onButtonClickListener: OnButtonClickListener? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
                             ): View {
        _binding = DeleteAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val param = WindowManager.LayoutParams()
        param.copyFrom(dialog!!.window!!.attributes)
        param.width = 800
        param.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = param
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.messageTextView.text = message
        binding.buttonYes.setOnClickListener {
            dismiss()
            onButtonClickListener?.onButtonClickListener(Enums.ClickEvents.LOGOUT)
        }
        binding.buttonNo.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setListener(listener: OnButtonClickListener) {
        this.onButtonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClickListener(typeButton: Enums.ClickEvents)
    }
}