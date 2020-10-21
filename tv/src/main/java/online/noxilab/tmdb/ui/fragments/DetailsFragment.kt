package online.noxilab.tmdb.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import info.movito.themoviedbapi.model.MovieDb
import online.noxilab.tmdb.AppConstants
import online.noxilab.tmdb.R
import online.noxilab.tmdb.models.ObjectSerializable
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.DetailsOverviewLogoPresenter
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import info.movito.themoviedbapi.TmdbApi
import info.movito.themoviedbapi.TmdbMovies
import info.movito.themoviedbapi.TmdbTV
import info.movito.themoviedbapi.TmdbTvSeasons
import info.movito.themoviedbapi.model.tv.TvSeries
import online.noxilab.tmdb.ui.activities.DetailsActivity
import online.noxilab.tmdb.ui.presenters.CustomActionPresenterSelector
import online.noxilab.tmdb.ui.presenters.FullWidthOverviewPresenter
import online.noxilab.tmdb.utils.CompletedListener
import online.noxilab.tmdb.utils.DoAsync
import online.noxilab.tmdb.utils.UtilsView


class DetailsFragment : DetailsSupportFragment() {
    private var mRowsAdapter: ArrayObjectAdapter? = null
    lateinit var overviewRowPresenter: FullWidthOverviewPresenter
    lateinit var overviewRow: DetailsOverviewRow
    private var mLogoView: ImageView? = null
    private var tmdb: TmdbApi? = null
    private var obj: Any? = null
    private var lastSelectedItem: Any? = null

    private var trailer = ""

    var canUpToTrailer = false
    var loadTrailer = false

    private val ACTION_IMDB = 1
    private val ACTION_TVDB = 2
    private val ACTION_WEB = 3

    fun newInstance(card: ObjectSerializable): DetailsFragment {
        val fragment = DetailsFragment()
        val args = Bundle()
        args.putSerializable(AppConstants.ITEM, card)
        fragment.arguments = args
        return fragment
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ps = ClassPresenterSelector()
        overviewRowPresenter = FullWidthOverviewPresenter(DetailsDescriptionPresenter(),
            object : DetailsOverviewLogoPresenter() {
                override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
                    super.onBindViewHolder(viewHolder, item)
                    mLogoView = viewHolder.view as ImageView
                    mLogoView?.elevation = 14f
                    mLogoView?.setBackgroundColor(Color.BLACK)
                    mLogoView?.transitionName = AppConstants.TRANSITION_POSTER
                }

            })

        overviewRowPresenter.onActionClickedListener =
            OnActionClickedListener { action ->
                Toast.makeText(activity, action.toString(), Toast.LENGTH_SHORT).show()
//                (activity as DetailsActivity).youtubeFragment.mYouTubePlayer
//                    ?.loadVideo("yy96yJjkvjo", 0f)
//                val youtubeLink = "http://youtube.com/watch?v=yy96yJjkvjo"
//                if (action.id == ACTION_BUY.toLong()) {
//                    // on the UI thread, we can modify actions adapter directly
//                    val actions = overviewRow.actionsAdapter as SparseArrayObjectAdapter
//                    actions.clear(ACTION_BUY)
//                }
//                else if (action.id == ACTION_RENT.toLong()) {
//                    view?.findViewById<FrameLayout>(R.id.details_fragment_root)?.visibility= View.GONE
//                }
            }
        ps.addClassPresenter(DetailsOverviewRow::class.java, overviewRowPresenter)
        ps.addClassPresenter(ListRow::class.java, ListRowPresenter())

        mRowsAdapter = ArrayObjectAdapter(ps)
        adapter = mRowsAdapter

        (activity as DetailsActivity).bgScroll.visibility = View.VISIBLE
        (activity as DetailsActivity).bgScroll.isFocusable = false
        (activity as DetailsActivity).bgScroll.isFocusableInTouchMode = false
        (activity as DetailsActivity).bgScroll.clearFocus()

        UtilsView().animationBlink((activity as DetailsActivity).drawerBottomArrow)
        UtilsView().animationBlink((activity as DetailsActivity).drawerTopText)
        UtilsView().animationBlink((activity as DetailsActivity).drawerTopArrow)
        (activity as DetailsActivity).drawerTop.translationY = -(activity as DetailsActivity).resources.getDimensionPixelOffset(R.dimen.lb_details_v2_logo_margin_top).toFloat()

        Handler().postDelayed({
            (activity as DetailsActivity).drawerTopText.visibility = View.VISIBLE
            (activity as DetailsActivity).drawerBottomArrow.visibility = View.VISIBLE
            (activity as DetailsActivity).drawerBottom.visibility = View.VISIBLE
            UtilsView().showBottomDrawer((activity as DetailsActivity).drawerBottomArrow,500)
            UtilsView().showBottomDrawer((activity as DetailsActivity).drawerBottom,500)
        }, 800)

        setOnItemViewSelectedListener { itemViewHolder, item, rowViewHolder, row ->
            if (overviewRowPresenter.initialState == FullWidthDetailsOverviewRowPresenter.STATE_HALF &&
                !(activity as DetailsActivity).detailsHiden) {
                if (loadTrailer) {
                    canUpToTrailer = false
                    (activity as DetailsActivity).videoFragment.pause(false)
                    UtilsView().hideTopDrawer((activity as DetailsActivity).drawerTop,300)
                }
                (activity as DetailsActivity).bgScroll.smoothScrollTo(0, 540)
                (activity as DetailsActivity).dpadController?.enableDown(mRowsAdapter!!.size() > 1)
                (activity as DetailsActivity).dpadController?.enableRight(false)
                (activity as DetailsActivity).dpadController?.enableLeft(false)
                UtilsView().animScaleY((activity as DetailsActivity).drawerBottomArrow, mRowsAdapter!!.size() > 1)
            } else if (overviewRowPresenter.initialState == FullWidthDetailsOverviewRowPresenter.STATE_FULL &&
                !(activity as DetailsActivity).detailsHiden) {
                if (loadTrailer) {
                    canUpToTrailer = true
                    (activity as DetailsActivity).videoFragment.play(false)
                    UtilsView().showTopDrawer((activity as DetailsActivity).drawerTop,300)
                }
                (activity as DetailsActivity).bgScroll.smoothScrollTo(0, 0)
                (activity as DetailsActivity).dpadController?.enableDown(true)
                if (lastSelectedItem != null && lastSelectedItem is Action) {
                    dpadOnAction(lastSelectedItem!! as Action)
                }
                UtilsView().animScaleY((activity as DetailsActivity).drawerBottomArrow, true)
            }
            if (item is Action) {
                if (loadTrailer) {
                    canUpToTrailer = true
                    (activity as DetailsActivity).videoFragment.play(false)
                }
                lastSelectedItem = item
                dpadOnAction(item)
            }
        }

        val item = arguments?.getSerializable(AppConstants.ITEM) as ObjectSerializable

        overviewRow = DetailsOverviewRow(item.obj)
        val adapter = SparseArrayObjectAdapter()
        adapter.presenterSelector = CustomActionPresenterSelector()
        overviewRow.actionsAdapter = adapter
        mRowsAdapter!!.add(0, overviewRow)

        if (item.obj != null) {
            setItem(item.obj!!)
            loadDeatails(item)
        }
    }

    private fun loadDeatails(item: ObjectSerializable) {
        DoAsync(object : CompletedListener {
            override fun onCompleted() {
                if (obj != null) {
                    overviewRowPresenter.setItem(obj!!)
                    setActions(obj!!)
                    if (trailer.isEmpty())
                        (activity as DetailsActivity).progressMain.visibility = View.GONE
                    else loadYoutubeUrl(trailer, false)
                }
            }
        }) {
            if (tmdb == null)
                tmdb = TmdbApi(AppConstants.API_TMDB)
            if (item.obj is MovieDb) {
                obj = tmdb!!.movies.getMovie((item.obj as MovieDb).id, "ru-RU", TmdbMovies.MovieMethod.videos)
                if ((obj as MovieDb).videos.isEmpty()) {
                    val vid = tmdb!!.movies.getVideos((item.obj as MovieDb).id, "en-US")
                    if (vid.isNotEmpty()) {
                        for (video in vid) {
                            if (video.site.equals("youtube", true)){
                                trailer = "http://youtube.com/watch?v=${video.key}"
                                break
                            }
                        }
                    }
                } else if ((obj as MovieDb).videos[0].site.equals("youtube", true)){
                    trailer = "http://youtube.com/watch?v=${(obj as MovieDb).videos[0].key}"
                }

            } else if (item.obj is TvSeries) {
                val c = tmdb!!.tvSeries.getSeries((item.obj as TvSeries).id, "ru", TmdbTV.TvMethod.external_ids, TmdbTV.TvMethod.content_ratings, TmdbTV.TvMethod.videos)
                val lastSeason = tmdb!!.tvSeasons.getSeason(c.id, c.seasons.last().seasonNumber, "ru", TmdbTvSeasons.SeasonMethod.external_ids)
                c.seasons[c.seasons.size-1] = lastSeason
                if (c.videos.isEmpty()) {
                    val vid = tmdb!!.tvSeries.getSeries((item.obj as TvSeries).id, "en-US", TmdbTV.TvMethod.videos)
                    if (vid.videos.isNotEmpty()) {
                        for (video in vid.videos) {
                            if (video.site.equals("youtube", true)){
                                trailer = "http://youtube.com/watch?v=${video.key}"
                                break
                            }
                        }
                    }
                } else if (c.videos[0].site.equals("youtube", true)) {
                    trailer = "http://youtube.com/watch?v=${c.videos[0].key}"
                }
                obj = c
            }
        }
    }

    private fun loadYoutubeUrl(youtubeLink: String, orig: Boolean) {
        val yt = @SuppressLint("StaticFieldLeak")
        object: YouTubeExtractor(context!!) {
            override fun onExtractionComplete(
                ytFiles: SparseArray<YtFile>?,
                videoMeta: VideoMeta?
            ) {
                val s = when {
                    ytFiles?.get(22)?.url != null -> ytFiles.get(22)?.url
                    ytFiles?.get(135)?.url != null -> ytFiles.get(135)?.url
                    ytFiles?.get(18)?.url != null -> ytFiles.get(18)?.url
                    else -> null
                }
                if (s != null) {
                    (activity as DetailsActivity).videoFragment.setVideo(s)
                    (activity as DetailsActivity).drawerTop.visibility = View.VISIBLE
                    (activity as DetailsActivity).progressMain.visibility = View.GONE
                    (activity as DetailsActivity).dpadController?.enableUp(true)
                    Handler().postDelayed({
                        if (!(activity as DetailsActivity).detailsFragment.isHidden && overviewRowPresenter.initialState == FullWidthDetailsOverviewRowPresenter.STATE_FULL) {
                            UtilsView().showTopDrawer((activity as DetailsActivity).drawerTop, 300)
                        }
                    }, 600)
                    loadTrailer = true
                } else if (!orig) {
                    loadTrailerOrig()
                } else {
                    (activity as DetailsActivity).progressMain.visibility = View.GONE
                    (activity as DetailsActivity).dpadController?.enableUp(false)
                    Log.e("loadYoutubeUrl", "null result from $youtubeLink")
                    Log.e("loadYoutubeUrl", "null result from ${ytFiles.toString()}")
                }
            }

        }
        yt.extract(youtubeLink, true, true)
    }

    private fun loadTrailerOrig() {
        DoAsync(object : CompletedListener {
            override fun onCompleted() {
                loadYoutubeUrl(trailer, true)
            }
        }) {
            if (tmdb == null)
                tmdb = TmdbApi(AppConstants.API_TMDB)
            if (obj is MovieDb) {
                val vid = tmdb!!.movies.getVideos((obj as MovieDb).id, "en-US")
                if (vid.isNotEmpty()) {
                    for (video in vid) {
                        if (video.site.equals("youtube", true)){
                            trailer = "http://youtube.com/watch?v=${video.key}"
                            break
                        }
                    }
                }
            } else if (obj is TvSeries) {
                val vid = tmdb!!.tvSeries.getSeries((obj as TvSeries).id, "en-US", TmdbTV.TvMethod.videos)
                if (vid.videos.isNotEmpty()) {
                    for (video in vid.videos) {
                        if (video.site.equals("youtube", true)){
                            trailer = "http://youtube.com/watch?v=${video.key}"
                            break
                        }
                    }
                }
            }
        }
    }

    private fun dpadOnAction(item: Action) {
        for (i in 0 until overviewRow.actionsAdapter.size()) {
            if (item.label1 == (overviewRow.actionsAdapter[i] as Action).label1) {
                (activity as DetailsActivity).dpadController?.enableRight(i < overviewRow.actionsAdapter.size() - 1)
                (activity as DetailsActivity).dpadController?.enableLeft(i > 0)
                break
            }
        }
    }

    private fun setActions(obj: Any) {
        if (obj is MovieDb) {
            if (!TextUtils.isEmpty(obj.homepage)) {
                val actions = overviewRow.actionsAdapter as SparseArrayObjectAdapter
                actions.set(
                    ACTION_WEB, Action(
                        ACTION_WEB.toLong(), "HOMEPAGE"
                    )
                )
            }
            if (!TextUtils.isEmpty(obj.imdbID)) {
                val actions = overviewRow.actionsAdapter as SparseArrayObjectAdapter
                actions.set(
                    ACTION_IMDB, Action(
                        ACTION_IMDB.toLong(), "IMDB"
                    )
                )
            }
        } else if (obj is TvSeries) {
            if (!TextUtils.isEmpty(obj.homepage)) {
                val actions = overviewRow.actionsAdapter as SparseArrayObjectAdapter
                actions.set(
                    ACTION_WEB, Action(
                        ACTION_WEB.toLong(), "HOMEPAGE"
                    )
                )
            }
            if (obj.externalIds != null) {
                if (!TextUtils.isEmpty(obj.externalIds.imdbId)) {
                    val actions = overviewRow.actionsAdapter as SparseArrayObjectAdapter
                    actions.set(
                        ACTION_IMDB, Action(
                            ACTION_IMDB.toLong(), "IMDB"
                        )
                    )
                }
                if (!TextUtils.isEmpty(obj.externalIds.tvdbId)) {
                    val actions = overviewRow.actionsAdapter as SparseArrayObjectAdapter
                    actions.set(
                        ACTION_TVDB, Action(
                            ACTION_TVDB.toLong(), "TVDB"
                        )
                    )
                }
            }
        }
        requestActions()
    }

    fun requestActions() {
        canUpToTrailer = true
        overviewRowPresenter.actionsView.requestFocus()
    }

    private fun setItem(item: Any) {
        if (item is MovieDb) {
            loadPoster("https://image.tmdb.org/t/p/original" + item.posterPath)
            (activity as DetailsActivity).bgContainerView.visibility = View.VISIBLE
            loadBackPoster("https://image.tmdb.org/t/p/original" + item.backdropPath)
        } else if (item is TvSeries) {
            loadPoster("https://image.tmdb.org/t/p/original" + item.posterPath)
            (activity as DetailsActivity).bgContainerView.visibility = View.VISIBLE
            loadBackPoster("https://image.tmdb.org/t/p/original" + item.backdropPath)
        }
    }

    fun hideInterface() {
        titleView?.animate()?.translationY(-titleView.height.toFloat())?.setDuration(400)?.start()
        val frame = activity?.findViewById<FrameLayout>(R.id.details_fragment_root)
        frame?.animate()?.translationY(400f)?.setDuration(400)?.start()
        frame?.animate()?.alpha(0f)?.setDuration(400)?.start()
    }

    fun showInterface() {
        titleView?.animate()?.translationY(0f)?.setDuration(400)?.start()
        val frame = activity?.findViewById<FrameLayout>(R.id.details_fragment_root)
        frame?.animate()?.translationY(0f)?.setDuration(400)?.start()
        frame?.animate()?.alpha(1f)?.setDuration(400)?.start()
    }

    private fun loadPoster(posterPathFull: String?) {
        val thumb = posterPathFull?.replace("/original/","/w185_and_h278_bestv2/")
        val thumbnailRequest = Glide.with(this).asBitmap().load(thumb)
        Glide.with(this)
            .asBitmap()
            .thumbnail(thumbnailRequest)
            .load(posterPathFull)
            .override(300, 450)
            .centerCrop()
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    overviewRow.imageDrawable = BitmapDrawable(resources, resource)
                    activity?.startPostponedEnterTransition()
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    private fun loadBackPoster(backdropPath: String?) {
        val thumb = backdropPath?.replace("/original/","/w500_and_h282_face/")
        val thumbnailRequest = Glide.with(this).asBitmap().load(thumb)
        Glide.with(this)
            .asBitmap()
            .load(backdropPath)
            .thumbnail(thumbnailRequest)
            .fitCenter()
            .into((activity as DetailsActivity).bgView)
    }

    inner class DetailsDescriptionPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.lb_details_description, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            overviewRowPresenter.setItem(item)
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {

        }
    }

}
