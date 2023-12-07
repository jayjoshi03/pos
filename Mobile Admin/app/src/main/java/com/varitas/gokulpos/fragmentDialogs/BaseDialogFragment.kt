package com.varitas.gokulpos.fragmentDialogs

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.varitas.gokulpos.R
import com.varitas.gokulpos.activity.BaseActivity
import com.varitas.gokulpos.utilities.Constants
import com.varitas.gokulpos.utilities.Utils

open class BaseDialogFragment : DialogFragment() {

    private var myActivity: AppCompatActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val style = DialogFragment.STYLE_NO_FRAME
        val theme = R.style.DialogTheme
        setStyle(style, theme)
    }

    @Deprecated("Deprecated in Java") override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (context is Activity) myActivity = context as BaseActivity
    }

    companion object {
        fun newInstance(): BaseDialogFragment {
            val f = BaseDialogFragment()
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    fun View.clickWithDebounce(debounceTime: Long = Constants.clickDelay, action: () -> Unit) {
        this.setOnClickListener { // if (!isBatchClose) {
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, debounceTime) }
            action() //} else Utils.instance.handleError(context, Constants.BATCH_CLOSE, resources.getString(R.string.lbl_StoreIsClosed))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    open fun hideKeyboardInFragment(view: View) {
        try {
            val windowToken = dialog!!.window!!.decorView.rootView
            val imm = dialog!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (ex: Exception) {
            Utils.printAndWriteException(ex)
        }
    }
}