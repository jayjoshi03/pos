package com.varitas.gokulpos.tablet.utilities

import android.content.Context
import android.util.AttributeSet
import android.widget.Spinner

class CustomSpinner : androidx.appcompat.widget.AppCompatSpinner {

    var mcontext: Context? = null

    constructor(context: Context?) : super(context!!) {
        this.mcontext = context
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle)

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }


    /**
     * An interface which a client of this Spinner could use to receive
     * open/closed events for this Spinner.
     */
    interface OnSpinnerEventsListener {
        /**
         * Callback triggered when the spinner was opened.
         */
        fun onSpinnerOpened(spinner: Spinner?)

        /**
         * Callback triggered when the spinner was closed.
         */
        fun onSpinnerClosed(spinner: Spinner?)
    }

    private var mListener: OnSpinnerEventsListener? = null
    private var mOpenInitiated = false

    // implement the Spinner constructors that you need
    override fun performClick(): Boolean { // register that the Spinner was opened so we have a status
        // indicator for when the container holding this Spinner may lose focus
        mOpenInitiated = true
        if (mListener != null) {
            mListener?.onSpinnerOpened(this)
        }
        return super.performClick()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasBeenOpened() && hasFocus) {
            performClosedEvent()
        }
    }

    /**
     * Register the listener which will listen for events.
     */
    fun setSpinnerEventsListener(onSpinnerEventsListener: OnSpinnerEventsListener?) {
        mListener = onSpinnerEventsListener
    }

    /**
     * Propagate the closed Spinner event to the listener from outside if needed.
     */
    private fun performClosedEvent() {
        mOpenInitiated = false
        if (mListener != null) {
            mListener?.onSpinnerClosed(this)
        }
    }

    fun performClosed() {
        mOpenInitiated = false

    }

    fun performOpen() {
        mOpenInitiated = true

    }


    /**
     * A boolean flag indicating that the Spinner triggered an open event.
     *
     * @return true for opened Spinner
     */
    private fun hasBeenOpened(): Boolean {
        return mOpenInitiated
    }

}

