package online.noxilab.tmdb.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.leanback.widget.*
import info.movito.themoviedbapi.TmdbApi
import online.noxilab.tmdb.*
import online.noxilab.tmdb.models.RowObjectAdapter
import online.noxilab.tmdb.ui.presenters.PersonPresenter
import online.noxilab.tmdb.utils.CompletedListener
import online.noxilab.tmdb.utils.DoAsync
import online.noxilab.tmdb.utils.ListListener
import online.noxilab.tmdb.utils.LoadMoreItems

class MainGridFragment(private val category: String) : CustomGridFragment() {
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_LARGE
    private var page = 1
    private var load = false
    private lateinit var obj: RowObjectAdapter
    private lateinit var tmdb: TmdbApi
    private lateinit var presenterGrid: VerticalGridPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAdapter()
        loadData()
        mainFragmentAdapter.fragmentHost.notifyDataReady(mainFragmentAdapter)
    }


    private fun setupAdapter() {
        presenterGrid = VerticalGridPresenter(ZOOM_FACTOR, false)
        gridPresenter = presenterGrid

        onItemViewClickedListener =
            OnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->

            }
        setOnItemViewSelectedListener(
            OnItemViewSelectedListener { itemViewHolder, item, rowViewHolder, row ->
                if (obj.list.indexOf(item) > obj.list.size() - (COLUMNS *2) && !load) {
                    page++
                    loadMoreItems(obj)
                }
            })
    }

    fun nextPosition(): Boolean {
        if (canNextPosition()) {
            setSelectedPosition(mSelectedPosition + 1)
            return true
        }
        return false
    }

    fun canNextPosition(): Boolean {
        val f = (mSelectedPosition.toFloat()+1)/COLUMNS.toFloat()
        return (f.toString().endsWith(".0"))
    }

    private fun loadData() {
        pb(true)
        obj = RowObjectAdapter()
        obj.page = 0
        if (category.equals(context?.getString(R.string.main_person), true)) {
            COLUMNS = 5
            presenterGrid.numberOfColumns = COLUMNS
            presenterGrid.shadowEnabled = false
            obj.list = ArrayObjectAdapter(PersonPresenter())
            obj.type = AppConstants.TypeRows.PEOPLE
            obj.header = context!!.getString(R.string.main_person)
        }
        adapter = obj.list
        DoAsync(object : CompletedListener {
            override fun onCompleted() {
                loadMoreItems(obj)
            }

        }) {
            tmdb = TmdbApi(AppConstants.API_TMDB)
        }
    }


    private fun loadMoreItems(rowObject: RowObjectAdapter) {
        pb(true)
        LoadMoreItems(rowObject, tmdb, object : ListListener {
            override fun onResult(list: ArrayList<Any>) {
                pb(false)
                rowObject.list.addAll(rowObject.list.size(), list)
            }
        })
    }

    private fun pb(b:Boolean) {
        load = b
        if (b) {
            activity?.findViewById<ProgressBar>(R.id.pb)?.visibility = View.VISIBLE
        } else {
            activity?.findViewById<ProgressBar>(R.id.pb)?.visibility = View.GONE
        }
    }

    companion object {
        var COLUMNS = 5
    }
}