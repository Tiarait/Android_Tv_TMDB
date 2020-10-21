package online.noxilab.tmdb.ui.fragments


import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import online.noxilab.tmdb.R
import online.noxilab.tmdb.ui.activities.MainActivity
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.PresenterSelector
import androidx.leanback.widget.TitleViewAdapter.SEARCH_VIEW_VISIBLE
import kotlinx.android.synthetic.main.lb_title_view.view.*
import online.noxilab.tmdb.AppConstants
import online.noxilab.tmdb.ui.presenters.HeaderIconItemPresenter
import online.noxilab.tmdb.models.HeaderIconItem


class MainFragment : BrowseSupportFragment() {
    private var mRowsAdapter: ArrayObjectAdapter? = null
    private var fragmentFactory: PageRowFragmentFactory? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
        loadData()

        fragmentFactory = PageRowFragmentFactory(context)
        mainFragmentRegistry.registerFragment(
            PageRow::class.java,
            fragmentFactory
        )
    }

    private fun setupUi() {
        brandColor = ContextCompat.getColor(activity!!,
            R.color.default_background
        )
        searchAffordanceColors = SearchOrbView.Colors (
            ContextCompat.getColor(context!!, R.color.search_opaque),
            ContextCompat.getColor(context!!, R.color.search_bright_color),
            ContextCompat.getColor(context!!, R.color.search_color)
        )
        setHeaderPresenterSelector(object : PresenterSelector() {
            override fun getPresenter(o: Any): Presenter {
                return HeaderIconItemPresenter()
            }
        })
        setOnSearchClickedListener {
            Toast.makeText(
                activity, getString(R.string.implement_search), Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun loadData() {
        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = mRowsAdapter
        createRows()
    }

    private fun createRows() {
        for (title in resources.getStringArray(R.array.category_title)) {
            val headerItem = when(title) {
                resources.getString(R.string.main_movie) -> {
                    HeaderIconItem(title.toUpperCase(), R.drawable.ic_main_movie)
                }
                resources.getString(R.string.main_serial) -> {
                    HeaderIconItem(title.toUpperCase(), R.drawable.ic_main_serial)
                }
                resources.getString(R.string.main_person) -> {
                    HeaderIconItem(title.toUpperCase(), R.drawable.ic_main_persons)
                }
                resources.getString(R.string.main_settings) -> {
                    mRowsAdapter!!.add(PageRow(HeaderIconItem(AppConstants.SPACE_TEXT)))
                    HeaderIconItem(title.toUpperCase(), R.drawable.ic_main_settings)
                }
                else -> {
                    HeaderIconItem(title.toUpperCase())
                }
            }
            mRowsAdapter!!.add(PageRow(headerItem))
        }
    }

    private class PageRowFragmentFactory(private val context: Context?) :
        BrowseSupportFragment.FragmentFactory<Fragment>() {
        var fragment: Fragment? = null
        private val mCashedFragments: MutableMap<String, Fragment> = HashMap()

        override fun createFragment(rowObj: Any): Fragment {
            val row = rowObj as Row
            val cachedId = row.headerItem.name
            fragment = when {
                mCashedFragments.containsKey(cachedId) -> {
                    mCashedFragments[cachedId]
                }
                row.headerItem.name.equals(context?.getString(R.string.main_person),true) -> {
                    mCashedFragments[cachedId] = MainGridFragment(row.headerItem.name)
                    mCashedFragments[cachedId]
                }
                else -> {
                    mCashedFragments[cachedId] = MainRowFragment(context!!, row.headerItem.name)
                    mCashedFragments[cachedId]
                }
            }
            when (fragment){
                is MainRowFragment -> {
                    Log.e("test","fragment is MainRowFragment")
                    (fragment as MainRowFragment).selectedPosition = 0
                }
            }
            return fragment!!
        }
    }

    fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        val headerGridView = headersSupportFragment.verticalGridView
        if (event?.action == KeyEvent.ACTION_UP) {
            if (activity is MainActivity) {
                (activity as MainActivity).dpadController?.enableLeft(!isShowingHeaders)
                (activity as MainActivity).dpadController?.enableUp(!titleView.hasFocus())
                if (fragmentFactory?.fragment is MainRowFragment && !isShowingHeaders) {
                    val fr: MainRowFragment = fragmentFactory?.fragment as MainRowFragment
                    (activity as MainActivity).dpadController?.enableDown(fr.selectedPosition != fr.mRowsAdapter.size() - 1)
                } else if (isShowingHeaders && headerGridView?.selectedPosition == mRowsAdapter!!.size()-1){
                    (activity as MainActivity).dpadController?.enableDown(false)
                } else{
                    (activity as MainActivity).dpadController?.enableDown(true)
                }
            }
        } else if (event?.action == KeyEvent.ACTION_DOWN) {
            when(event.keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN -> {
//                    //появляется на время
//                    Log.e("test","orb isShown="+titleView.title_orb.isShown)
//                    Log.e("test","orb visibility="+titleView.title_orb.visibility)
//                    Log.e("test","orb searchAffordanceView="+titleViewAdapter.searchAffordanceView.isShown)
                    if (!isShowingHeaders && titleView.hasFocus() && fragmentFactory?.fragment is MainRowFragment) {
                        (fragmentFactory?.fragment as MainRowFragment).verticalGridView.requestFocus()
                        (fragmentFactory?.fragment as MainRowFragment).setSelectedPosition(0, true)
                        return true
                    } else if (isShowingHeaders && titleView.hasFocus()) {
                        headerGridView?.requestFocus()
                        headerGridView?.setSelectedPositionSmooth(0)
                        return true
                    }
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    if (fragmentFactory?.fragment is MainGridFragment) {
                        if ((fragmentFactory?.fragment as MainGridFragment).canNextPosition() &&
                            (fragmentFactory?.fragment as MainGridFragment).mSelectedPosition >= MainGridFragment.COLUMNS - 1)
                            titleView.animate().translationYBy((-titleView.height).toFloat())
                                .setDuration(300)
                                .setListener(object : Animator.AnimatorListener {
                                    override fun onAnimationStart(animator: Animator) {

                                    }

                                    override fun onAnimationEnd(animator: Animator) {
                                        titleView.visibility = View.GONE
                                        titleView.translationY = 0f
                                    }

                                    override fun onAnimationCancel(animator: Animator) {

                                    }

                                    override fun onAnimationRepeat(animator: Animator) {

                                    }
                                }).start()
                        return (fragmentFactory?.fragment as MainGridFragment).nextPosition()
                    }
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    if (fragmentFactory?.fragment is MainGridFragment) {
                        if ((fragmentFactory?.fragment as MainGridFragment).mSelectedPosition == MainGridFragment.COLUMNS) {
                            titleView.translationY = (-titleView.height).toFloat()
                            titleView.visibility = View.VISIBLE
                            titleView.animate().translationYBy((titleView.height).toFloat())
                                .setDuration(300)
                                .setListener(object : Animator.AnimatorListener {
                                    override fun onAnimationStart(animator: Animator) {

                                    }

                                    override fun onAnimationEnd(animator: Animator) {
                                        titleView.visibility = View.VISIBLE
                                        titleView.translationY = 0f
                                    }

                                    override fun onAnimationCancel(animator: Animator) {

                                    }

                                    override fun onAnimationRepeat(animator: Animator) {

                                    }
                                }).start()
                        }
                    }
                }
                KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_ESCAPE, KeyEvent.KEYCODE_B -> {
                    if (isShowingHeaders) {
                        activity?.finish()
                        return true
                    }
                }
            }
        }
        return false
    }
}
