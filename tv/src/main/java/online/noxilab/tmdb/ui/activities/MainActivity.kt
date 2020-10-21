package online.noxilab.tmdb.ui.activities

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import online.noxilab.tmdb.R
import online.noxilab.tmdb.ui.fragments.MainFragment
import online.noxilab.tmdb.utils.DpadController


class MainActivity : FragmentActivity() {
    var dpadController: DpadController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, MainFragment(), "main")
            .addToBackStack(null)
            .commitAllowingStateLoss()

        dpadController = DpadController(this)
        dpadController?.enableDown(true)
        dpadController?.enableUp(true)
        dpadController?.enableRight(true)
        dpadController?.enableLeft(false)
    }

    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.content)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        dpadController?.dpadControl(event)
        if (getCurrentFragment() is MainFragment) {
            return if ((getCurrentFragment() as MainFragment).dispatchKeyEvent(event))
                true
            else super.dispatchKeyEvent(event)
        }
        return super.dispatchKeyEvent(event)
    }
}
