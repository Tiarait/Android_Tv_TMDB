package online.noxilab.tmdb.utils

import android.app.Instrumentation
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import online.noxilab.tmdb.R

class DpadController(private val mActivity: FragmentActivity?) {
    var view: View? = null
    var dpadDown: Button? = null
    var dpadUp: Button? = null
    var dpadRight: Button? = null
    var dpadLeft: Button? = null

    data class Button(val view: View, val keyCode: Int, var enable: Boolean)

    init {
        if (mActivity != null) {
            view = mActivity.findViewById(R.id.dpad_container)
            dpadDown = Button(mActivity.findViewById(R.id.dpad_down), KeyEvent.KEYCODE_DPAD_DOWN, true)
            dpadUp = Button(mActivity.findViewById(R.id.dpad_up), KeyEvent.KEYCODE_DPAD_UP, true)
            dpadRight = Button(mActivity.findViewById(R.id.dpad_right), KeyEvent.KEYCODE_DPAD_RIGHT, true)
            dpadLeft = Button(mActivity.findViewById(R.id.dpad_left), KeyEvent.KEYCODE_DPAD_LEFT, true)


            dpadDown?.view?.setOnTouchListener { view, motionEvent ->
                if (dpadDown?.enable!!) {
                    if (motionEvent.action == MotionEvent.ACTION_DOWN) view.scale = .8f
                    else if (motionEvent.action == MotionEvent.ACTION_UP) {
                        view.scale = 1f
                        Thread(Runnable {
                            Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN)
                        }).start()
                    }
                }
                true
            }
            dpadUp?.view?.setOnTouchListener { view, motionEvent ->
                if (dpadUp?.enable!!) {
                    if (motionEvent.action == MotionEvent.ACTION_DOWN) view.scale = .8f
                    else if (motionEvent.action == MotionEvent.ACTION_UP) {
                        view.scale = 1f
                        Thread(Runnable {
                            Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP)
                        }).start()
                    }
                }
                true
            }
            dpadRight?.view?.setOnTouchListener { view, motionEvent ->
                if (dpadRight?.enable!!) {
                    if (motionEvent.action == MotionEvent.ACTION_DOWN) view.scale = .8f
                    else if (motionEvent.action == MotionEvent.ACTION_UP) {
                        view.scale = 1f
                        Thread(Runnable {
                            Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT)
                        }).start()
                    }
                }
                true
            }
            dpadLeft?.view?.setOnTouchListener { view, motionEvent ->
                if (dpadLeft?.enable!!) {
                    if (motionEvent.action == MotionEvent.ACTION_DOWN) view.scale = .8f
                    else if (motionEvent.action == MotionEvent.ACTION_UP) {
                        view.scale = 1f
                        Thread(Runnable {
                            Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_LEFT)
                        }).start()
                    }
                }
                true
            }
        }
    }

    fun dpadControl(event: KeyEvent?) {
        if (mActivity != null) {
            if (event?.action == KeyEvent.ACTION_UP) {
                when(event.keyCode) {
                    KeyEvent.KEYCODE_DPAD_DOWN -> dpadDown?.view?.scale = 1f
                    KeyEvent.KEYCODE_DPAD_UP -> dpadUp?.view?.scale = 1f
                    KeyEvent.KEYCODE_DPAD_RIGHT -> dpadRight?.view?.scale = 1f
                    KeyEvent.KEYCODE_DPAD_LEFT -> dpadLeft?.view?.scale = 1f
                }
            } else if (event?.action == KeyEvent.ACTION_DOWN) {
                when(event.keyCode) {
                    KeyEvent.KEYCODE_DPAD_DOWN -> if (dpadDown?.enable!!) dpadDown?.view?.scale = .8f
                    KeyEvent.KEYCODE_DPAD_UP -> if (dpadUp?.enable!!) dpadUp?.view?.scale = .8f
                    KeyEvent.KEYCODE_DPAD_RIGHT -> if (dpadRight?.enable!!) dpadRight?.view?.scale = .8f
                    KeyEvent.KEYCODE_DPAD_LEFT -> if (dpadLeft?.enable!!) dpadLeft?.view?.scale = .8f
                }
            }
        }
    }

    fun enableDown(b: Boolean) {
        enableButton(dpadDown, b)
    }

    fun enableUp(b: Boolean) {
        enableButton(dpadUp, b)
    }

    fun enableRight(b: Boolean) {
        enableButton(dpadRight, b)
    }

    fun enableLeft(b: Boolean) {
        enableButton(dpadLeft, b)
    }

    private fun enableButton(b: Button?, e: Boolean) {
        b?.enable = e
        if (mActivity != null) {
            if (!e) (b?.view as ImageView).setColorFilter(
                ContextCompat.getColor(
                    mActivity,
                    R.color.white_tr
                ), android.graphics.PorterDuff.Mode.MULTIPLY
            )
            else (b?.view as ImageView).setColorFilter(
                ContextCompat.getColor(
                    mActivity,
                    R.color.icon_color
                ), android.graphics.PorterDuff.Mode.MULTIPLY
            )
        }
    }
}
private var View.scale: Float
    get() {
        return this.scaleX
    }
    set(scale) {
        this.scaleX = scale
        this.scaleY = scale
    }