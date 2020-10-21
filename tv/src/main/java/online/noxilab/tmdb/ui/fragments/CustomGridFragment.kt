package online.noxilab.tmdb.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import online.noxilab.tmdb.R

open class CustomGridFragment : Fragment(), BrowseSupportFragment.MainFragmentAdapterProvider {

    var adapter: ObjectAdapter? = null
        set(adapter) {
            field = adapter
            updateAdapter()
        }
    var gridPresenter: VerticalGridPresenter? = null
        set(gridPresenter) {
            requireNotNull(gridPresenter) { "Grid presenter may not be null" }
            field = gridPresenter
            this.gridPresenter!!.onItemViewSelectedListener = mViewSelectedListener
            if (onItemViewClickedListener != null) {
                this.gridPresenter!!.onItemViewClickedListener = onItemViewClickedListener
            }
        }
    private var mGridViewHolder: VerticalGridPresenter.ViewHolder? = null
    private var mOnItemViewSelectedListener: OnItemViewSelectedListener? = null
    var onItemViewClickedListener: OnItemViewClickedListener? = null
        set(listener) {
            field = listener
            if (gridPresenter != null) {
                gridPresenter!!.onItemViewClickedListener = onItemViewClickedListener
            }
        }
    var mSelectedPosition = -1
    private val mMainFragmentAdapter = object : BrowseSupportFragment.MainFragmentAdapter<Fragment>(this) {
        override fun setEntranceTransitionState(state: Boolean) {
            this@CustomGridFragment.setEntranceTransitionState(state)
        }
    }

    private val mViewSelectedListener = OnItemViewSelectedListener { itemViewHolder, item, rowViewHolder, row ->
        val position = mGridViewHolder!!.gridView.selectedPosition
        gridOnItemSelected(position)
        if (mOnItemViewSelectedListener != null) {
            mOnItemViewSelectedListener!!.onItemSelected(
                itemViewHolder, item,
                rowViewHolder, row
            )
        }
    }

    private val mChildLaidOutListener = OnChildLaidOutListener { parent, view, position, id ->
        if (position == 0) {
            showOrHideTitle()
        }
    }

    fun setOnItemViewSelectedListener(listener: OnItemViewSelectedListener) {
        mOnItemViewSelectedListener = listener
    }

    private fun gridOnItemSelected(position: Int) {
        if (position != mSelectedPosition) {
            mSelectedPosition = position
            showOrHideTitle()
        }
    }

    private fun showOrHideTitle() {
        if (mGridViewHolder!!.gridView.findViewHolderForAdapterPosition(mSelectedPosition) == null) {
            return
        }
        if (!mGridViewHolder!!.gridView.hasPreviousViewInSameRow(mSelectedPosition)) {
            mMainFragmentAdapter.fragmentHost.showTitleView(true)
        } else {
            mMainFragmentAdapter.fragmentHost.showTitleView(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.grid_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gridDock = view.findViewById<ViewGroup>(R.id.browse_grid_dock)
        mGridViewHolder = gridPresenter!!.onCreateViewHolder(gridDock)
        gridDock.addView(mGridViewHolder!!.view)
        mGridViewHolder!!.gridView.setOnChildLaidOutListener(mChildLaidOutListener)

        mainFragmentAdapter.fragmentHost.notifyViewCreated(mMainFragmentAdapter)
        updateAdapter()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mGridViewHolder = null
    }

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> {
        return mMainFragmentAdapter
    }

    fun setSelectedPosition(position: Int) {
        mSelectedPosition = position
        if (mGridViewHolder != null && mGridViewHolder!!.gridView.adapter != null) {
            mGridViewHolder!!.gridView.setSelectedPositionSmooth(position)
        }
    }

    private fun updateAdapter() {
        if (mGridViewHolder != null) {
            gridPresenter!!.onBindViewHolder(mGridViewHolder!!, adapter)
            if (mSelectedPosition != -1) {
                mGridViewHolder!!.gridView.selectedPosition = mSelectedPosition
            }
        }
    }

    internal fun setEntranceTransitionState(afterTransition: Boolean) {
        gridPresenter!!.setEntranceTransitionState(mGridViewHolder!!, afterTransition)
    }
}