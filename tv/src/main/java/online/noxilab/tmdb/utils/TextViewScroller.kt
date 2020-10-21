package online.noxilab.tmdb.utils

import android.os.Handler
import android.text.TextUtils
import android.widget.TextSwitcher
import android.widget.TextView

class TextViewScroller @JvmOverloads constructor(private var mTextView: TextView? = null) {

    private var mHandler = Handler()
    private var mRunnableEnable: Runnable = Runnable {
        if (mTextView != null) {
            mTextView!!.setSingleLine(true)
            mTextView!!.ellipsize = TextUtils.TruncateAt.MARQUEE
            mTextView!!.isSelected = true
        }
    }

    fun setView(view: TextView) {
        stop()
        mTextView = view
    }

    fun restart() {
        stop()
        start()
    }

    fun start() {
        mHandler.removeCallbacks(mRunnableEnable)
        mHandler.postDelayed(mRunnableEnable, 2000)
    }

    fun stop() {
        mHandler.removeCallbacks(mRunnableEnable)
        if (mTextView != null) {
            mTextView!!.setSingleLine(false)
            mTextView!!.ellipsize = TextUtils.TruncateAt.END
            mTextView!!.isSelected = false
            mTextView!!.maxLines = 1
        }
    }
}
