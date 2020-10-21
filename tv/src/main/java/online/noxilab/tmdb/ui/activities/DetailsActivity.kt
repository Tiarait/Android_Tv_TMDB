package online.noxilab.tmdb.ui.activities

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import online.noxilab.tmdb.R
import online.noxilab.tmdb.utils.DpadController
import android.content.Intent
import android.os.Handler
import android.transition.Fade
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import online.noxilab.tmdb.AppConstants
import online.noxilab.tmdb.models.ObjectSerializable
import online.noxilab.tmdb.ui.fragments.DetailsFragment
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import online.noxilab.tmdb.ui.fragments.DetailsVideoFragment
import online.noxilab.tmdb.utils.DetailsTransition
import online.noxilab.tmdb.utils.UtilsView


class DetailsActivity : FragmentActivity() {
    var dpadController: DpadController? = null
//    lateinit var youtubeFragment: DetailsYoutubeFragment
    lateinit var videoFragment: DetailsVideoFragment
    lateinit var detailsFragment: DetailsFragment
    lateinit var bgView: ImageView
    lateinit var bgContainerView: View
    lateinit var bgScroll: ScrollView
    lateinit var progressMain: ProgressBar

    var detailsHiden = false

    lateinit var drawerTopText: TextView
    lateinit var drawerTop: View
    lateinit var drawerTopArrow: View
    lateinit var drawerBottom: View
    lateinit var drawerBottomArrow: View

    fun start(activity: FragmentActivity?, item: ObjectSerializable, view: View) {
        if (activity != null) {
            val intent = Intent(activity, DetailsActivity::class.java)
            intent.putExtra(AppConstants.ITEM, item)

            val p1 = Pair.create(view, AppConstants.TRANSITION_POSTER)
            val p2 = Pair.create(activity.findViewById<View>(R.id.dpad_container), "dpad")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, p1, p2)
            activity.startActivity(intent, options.toBundle())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        postponeEnterTransition()
        
        bgView = findViewById(R.id.bg_img)
        bgContainerView = findViewById(R.id.bg_img_container)
        bgScroll = findViewById(R.id.bg_scroll)
        progressMain = findViewById(R.id.progress_main)
        progressMain.visibility = View.VISIBLE

        drawerTopText = findViewById(R.id.drawer_top_text)
        drawerTop = findViewById<View>(R.id.drawer_top)
        drawerTopArrow = findViewById<View>(R.id.drawer_top_arrow)
        drawerBottom = findViewById<View>(R.id.drawer_bottom_bg)
        drawerBottomArrow = findViewById<View>(R.id.drawer_bottom_arrow)

        detailsFragment = DetailsFragment().newInstance(
            intent.getSerializableExtra(AppConstants.ITEM) as ObjectSerializable)

        detailsFragment.sharedElementEnterTransition = DetailsTransition()
        detailsFragment.enterTransition = Fade()
        detailsFragment.sharedElementReturnTransition = DetailsTransition()

        videoFragment = DetailsVideoFragment().newInstance()
//        youtubeFragment = DetailsYoutubeFragment().newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.video_bg, videoFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, detailsFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()



        dpadController = DpadController(this)
        dpadController?.enableDown(true)
        dpadController?.enableUp(false)
        dpadController?.enableRight(false)
        dpadController?.enableLeft(false)
    }

    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.content)
    }

    fun hideFragment(fr: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .hide(fr)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun showFragment(fr: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .show(fr)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    private fun hideDetails(): Boolean {
        if (detailsFragment.canUpToTrailer && detailsFragment.loadTrailer && !detailsFragment.isHidden) {
            detailsHiden = true
            UtilsView().hideTopDrawer(drawerTop, 300)
            UtilsView().hideBottomDrawer(drawerBottom, 300)
            Handler().postDelayed({ drawerTop.visibility = View.GONE }, 300)
            bgScroll.animate().alpha(0f).setDuration(400).start()
            dpadController?.view?.animate()?.alpha(0f)?.setDuration(400)?.start()
            detailsFragment.hideInterface()
            videoFragment.unmute()
            Handler().postDelayed({ hideFragment(detailsFragment) }, 600)
            return true
        }
        return false
    }

    private fun showDetails(): Boolean {
        if (detailsFragment.isHidden) {
            detailsHiden = false
            videoFragment.mute()
            showFragment(detailsFragment)
            UtilsView().showTopDrawer(drawerTop, 300)
            UtilsView().showBottomDrawer(drawerBottom, 300)
            drawerTop.visibility = View.VISIBLE
            Handler().postDelayed({ drawerTop.visibility = View.VISIBLE }, 300)
            bgScroll.animate().alpha(1f).setDuration(400).start()
            dpadController?.view?.animate()?.alpha(1f)?.setDuration(400)?.start()
            detailsFragment.showInterface()
            detailsFragment.requestActions()
            return true
        }
        return false
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        dpadController?.dpadControl(event)
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_ESCAPE, KeyEvent.KEYCODE_B -> {
                    finishAfterTransition()
                    return true
                }
                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_SPACE  -> {
                    if (detailsFragment.isHidden) {
                        if (videoFragment.isPlaying())
                            videoFragment.pause()
                        else videoFragment.play()
                        return true
                    }
                }
                KeyEvent.KEYCODE_DPAD_RIGHT  -> {
                    if (detailsFragment.isHidden) {
                        videoFragment.seekPlus(10*1000)
                        return true
                    }
                }
                KeyEvent.KEYCODE_DPAD_LEFT  -> {
                    if (detailsFragment.isHidden) {
                        videoFragment.seekMinus(10*1000)
                        return true
                    }
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    return hideDetails()
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    return showDetails()
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }
}
