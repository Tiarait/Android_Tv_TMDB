package online.noxilab.tmdb.ui.fragments

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import info.movito.themoviedbapi.TmdbApi
import info.movito.themoviedbapi.TmdbTvSeasons
import info.movito.themoviedbapi.model.MovieDb
import info.movito.themoviedbapi.model.tv.TvSeries
import online.noxilab.tmdb.*
import online.noxilab.tmdb.models.ObjectSerializable
import online.noxilab.tmdb.models.RowObjectAdapter
import online.noxilab.tmdb.ui.presenters.CardPresenter
import online.noxilab.tmdb.ui.presenters.CustomListRowPresenter
import online.noxilab.tmdb.utils.*
import online.noxilab.tmdb.ui.activities.DetailsActivity


class MainRowFragment(mContext: Context, private val category: String) : RowsSupportFragment() {
    val arrayAdapter: ArrayList<RowObjectAdapter>
    val mRowsAdapter: ArrayObjectAdapter
    private lateinit var tmdb: TmdbApi
    private var mPb: ProgressBar? = null

    private var array: Array<String>
    private var nowPlaying: String
    private var upcoming: String
    private var popular: String
    private var topRated: String
    private var airingToday: String
    private var onTv: String

    init {
        val presenter = CustomListRowPresenter()
        mRowsAdapter = ArrayObjectAdapter(presenter)
        arrayAdapter = ArrayList()

        onItemViewClickedListener = OnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->
            DetailsActivity().start(activity, ObjectSerializable(item), (itemViewHolder.view as ImageCardView).mainImageView)
        }
        onItemViewSelectedListener = OnItemViewSelectedListener { itemViewHolder, item, rowViewHolder, row ->
            val curPos = arrayAdapter[selectedPosition].list.indexOf(item)
            if (curPos == arrayAdapter[selectedPosition].list.size() - 7 || curPos == arrayAdapter[selectedPosition].list.size() - 2) {
                loadMoreItems(arrayAdapter[selectedPosition])
            }
            if (item is MovieDb) {
                var movie = item
                DoAsync(object : CompletedListener {
                    override fun onCompleted() {
                        setPresenterDesc(presenter, movie)
                    }
                }) {
                    movie = tmdb.movies.getMovie(item.id, "ru-RU")
                }
                var title = item.title
                if (item.title != item.originalTitle)
                    title = UtilsText().delimeterStrings(
                        AppConstants.DOT_DELIMETERSPACE,
                        item.title,
                        item.originalTitle)
                presenter.setDesc(
                    title,
                    SpannableString(UtilsText().delimeterStrings(
                        AppConstants.COMMA,
                        UtilsText().safesplit(item.releaseDate, "-").first(),
                        AppConstants.ELLIPSIS))
                )
            } else if (item is TvSeries) {
                var serial = item
                DoAsync(object : CompletedListener {
                    override fun onCompleted() {
                        setPresenterDesc(presenter, serial)
                    }
                }) {
                    val c = tmdb.tvSeries.getSeries(item.id, "ru")
                    val lastSeason = tmdb.tvSeasons.getSeason(c.id, c.seasons.last().seasonNumber, "ru",
                        TmdbTvSeasons.SeasonMethod.external_ids)
                    c.seasons[c.seasons.size-1] = lastSeason
                    serial = c
                }
                var title = item.name
                if (item.name != item.originalName)
                    title = UtilsText().delimeterStrings(
                        AppConstants.DOT_DELIMETERSPACE,
                        item.name,
                        item.originalName)
                presenter.setDesc(
                    title,
                    SpannableString(UtilsText().safesplit(item.firstAirDate, "-").first()+" ${AppConstants.ELLIPSIS}")
                )
            }
        }
        adapter = mRowsAdapter
        array = mContext.resources.getStringArray(R.array.category_title)
        nowPlaying = category + AppConstants.DOT_DELIMETERSPACE + mContext.getString(R.string.now_playing)
        upcoming = category + AppConstants.DOT_DELIMETERSPACE + mContext.getString(R.string.upcoming)
        popular = category + AppConstants.DOT_DELIMETERSPACE + mContext.getString(R.string.popular)
        topRated = category + AppConstants.DOT_DELIMETERSPACE + mContext.getString(R.string.top_rated)
        airingToday = category + AppConstants.DOT_DELIMETERSPACE + mContext.getString(R.string.airing_today)
        onTv = category + AppConstants.DOT_DELIMETERSPACE + mContext.getString(R.string.on_tv)

        mPb = activity?.findViewById(R.id.pb)
        mPb?.visibility = View.VISIBLE
        DoAsync(object : CompletedListener {
            override fun onCompleted() {
                mPb?.visibility = View.GONE
                for (obj in arrayAdapter) {
                    mRowsAdapter.add(
                        ListRow(
                            HeaderItem(obj.header),
                            obj.list
                        )
                    )
                }
            }

        }) {
            tmdb = TmdbApi(AppConstants.API_TMDB)
            val presenter = CardPresenter()
            when (category.toLowerCase()) {
                array[0].toLowerCase() -> {
                    val listMovieNowPlaying =
                        RowObjectAdapter()
                    listMovieNowPlaying.list = ArrayObjectAdapter(presenter)
                    listMovieNowPlaying.page = 1
                    listMovieNowPlaying.type = AppConstants.TypeRows.MOVIE_NOW
                    listMovieNowPlaying.header = nowPlaying
                    val movieNowPlaying = tmdb.movies.getNowPlayingMovies("ru-RU", listMovieNowPlaying.page, "")
                    for (item: MovieDb in movieNowPlaying.results) {
                        if (!TextUtils.isEmpty(item.title))
                            listMovieNowPlaying.list.add(item)
                    }
                    arrayAdapter.add(listMovieNowPlaying)
                    //---------------------------------------------
                    val listMovieUpcoming = RowObjectAdapter()
                    listMovieUpcoming.list = ArrayObjectAdapter(presenter)
                    listMovieUpcoming.page = 1
                    listMovieUpcoming.type = AppConstants.TypeRows.MOVIE_UPCOMING
                    listMovieUpcoming.header = upcoming

                    val movieUpcoming = tmdb.movies.getUpcoming("ru-RU", listMovieUpcoming.page, "")
                    for (item: MovieDb in movieUpcoming.results) {
                        if (!TextUtils.isEmpty(item.title))
                            listMovieUpcoming.list.add(item)
                    }
                    arrayAdapter.add(listMovieUpcoming)
                    //---------------------------------------------
                    val listMoviePopular = RowObjectAdapter()
                    listMoviePopular.list = ArrayObjectAdapter(presenter)
                    listMoviePopular.page = 1
                    listMoviePopular.type = AppConstants.TypeRows.MOVIE_POPULAR
                    listMoviePopular.header = popular

                    val moviePopular = tmdb.movies.getPopularMovies("ru-RU", listMoviePopular.page)
                    for (item: MovieDb in moviePopular.results) {
                        if (!TextUtils.isEmpty(item.title))
                            listMoviePopular.list.add(item)
                    }
                    arrayAdapter.add(listMoviePopular)
                    //---------------------------------------------
                    val listMovieTopRated = RowObjectAdapter()
                    listMovieTopRated.list = ArrayObjectAdapter(presenter)
                    listMovieTopRated.page = 1
                    listMovieTopRated.type = AppConstants.TypeRows.MOVIE_TOP
                    listMovieTopRated.header = topRated

                    val movieTopRated = tmdb.movies.getTopRatedMovies("ru-RU", listMovieTopRated.page)
                    for (item: MovieDb in movieTopRated.results) {
                        if (!TextUtils.isEmpty(item.title))
                            listMovieTopRated.list.add(item)
                    }
                    arrayAdapter.add(listMovieTopRated)
                }
                array[1].toLowerCase() -> {
                    val listToday = RowObjectAdapter()
                    listToday.list = ArrayObjectAdapter(presenter)
                    listToday.page = 1
                    listToday.type = AppConstants.TypeRows.SERIAL_TODAY
                    listToday.header = airingToday

                    val serialToday = tmdb.tvSeries.getAiringToday("ru-RU", listToday.page, null)
                    for (item: TvSeries in serialToday.results) {
                        if (!TextUtils.isEmpty(item.name))
                            listToday.list.add(item)
                    }
                    arrayAdapter.add(listToday)
                    //---------------------------------------------
                    val listTv = RowObjectAdapter()
                    listTv.list = ArrayObjectAdapter(presenter)
                    listTv.page = 1
                    listTv.type = AppConstants.TypeRows.SERIAL_ONTV
                    listTv.header = onTv

                    val serialTv = tmdb.tvSeries.getOnTheAir("ru-RU", listTv.page)
                    for (item: TvSeries in serialTv.results) {
                        if (!TextUtils.isEmpty(item.name))
                            listTv.list.add(item)
                    }
                    arrayAdapter.add(listTv)
                    //---------------------------------------------
                    val listPopular = RowObjectAdapter()
                    listPopular.list = ArrayObjectAdapter(presenter)
                    listPopular.page = 1
                    listPopular.type = AppConstants.TypeRows.SERIAL_POPULAR
                    listPopular.header = popular

                    val serialPopular = tmdb.tvSeries.getPopular("ru-RU", listPopular.page)
                    for (item: TvSeries in serialPopular.results) {
                        if (!TextUtils.isEmpty(item.name))
                            listPopular.list.add(item)
                    }
                    arrayAdapter.add(listPopular)
                    //---------------------------------------------
                    val listTopRated = RowObjectAdapter()
                    listTopRated.list = ArrayObjectAdapter(presenter)
                    listTopRated.page = 1
                    listTopRated.type = AppConstants.TypeRows.SERIAL_TOP
                    listTopRated.header = topRated

                    val serialTopRated = tmdb.tvSeries.getTopRated("ru-RU", listTopRated.page)
                    for (item: TvSeries in serialTopRated.results) {
                        if (!TextUtils.isEmpty(item.name))
                            listTopRated.list.add(item)
                    }
                    arrayAdapter.add(listTopRated)
                }
                array[2].toLowerCase() -> {

                }
                array[3].toLowerCase() -> {

                }
            }
        }
    }

    private fun loadMoreItems(rowObject: RowObjectAdapter) {
        LoadMoreItems(rowObject, tmdb, object : ListListener{
            override fun onResult(list: ArrayList<Any>) {
                rowObject.list.addAll(rowObject.list.size(), list)
            }
        })
    }

    private fun setPresenterDesc(presenter: CustomListRowPresenter, item: Any) {
        if (item is MovieDb) {
            val genres = if (item.genres != null) {
                UtilsText().itemArrToStr(item.genres)
            } else AppConstants.ELLIPSIS
            val contries = if (item.productionCountries != null) {
                UtilsText().itemArrToStr(item.productionCountries)
            } else ""
            var title = item.title
            if (item.title != item.originalTitle)
                title = UtilsText().delimeterStrings(
                AppConstants.DOT_DELIMETERSPACE,
                item.title,
                item.originalTitle)
            val subtitle = UtilsText().safesplit(item.releaseDate, "-").first() + ", " +
                contries + "  " + genres.replace(",", " ")
            val spanSubtitle = SpannableString(subtitle)
            if (item.genres != null) {
                for (g in item.genres) {
                    spanSubtitle.setSpan(RoundedColorSpan(Color.rgb(55, 71, 79), Color.rgb(238, 238, 238), 5f, 10,10),
                        subtitle.split(" "+g.name)[0].length+1,
                        subtitle.split(" "+g.name)[0].length+1+g.name.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            if (presenter.curTitle == title) {
                presenter.setRating(item.voteAverage)
                presenter.setDesc(title, spanSubtitle)
            }
        } else if (item is TvSeries) {
            val genres = if (item.genres != null) {
                UtilsText().itemArrToStr(item.genres)
            } else AppConstants.ELLIPSIS
            val contries = if (item.originCountry != null) {
                UtilsText().itemArrToStr(item.originCountry)
            } else ""
            var title = item.name
            if (item.name != item.originalName)
                title = UtilsText().delimeterStrings(
                AppConstants.DOT_DELIMETERSPACE,
                item.name,
                item.originalName)
            if (presenter.curTitle == title) {
                val season = if (item.seasons != null) {
                    val s = item.seasons.last()
                    "s" + s.seasonNumber + "e" + s.episodes.last().episodeNumber
                } else ""
                val subtitle = UtilsText().safesplit(item.firstAirDate, "-").first() + "  " + season +
                        contries + "  " + genres.replace(",", " ")
                val spanSubtitle = SpannableString(subtitle)
                if (season.isNotEmpty())
                    spanSubtitle.setSpan(RoundedColorSpan(Color.rgb(55, 71, 79), Color.rgb(238, 238, 238), 5f, 10,10),
                        subtitle.split(season)[0].length, subtitle.split(season)[0].length + season.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (item.genres != null) {
                    for (g in item.genres) {
                        spanSubtitle.setSpan(RoundedColorSpan(Color.rgb(55, 71, 79), Color.rgb(238, 238, 238), 5f, 10,10),
                            subtitle.split(" "+g.name)[0].length+1,
                            subtitle.split(" "+g.name)[0].length+1+g.name.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                presenter.setRating(item.voteAverage)
                presenter.setDesc(title, spanSubtitle)
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        selectedPosition = 0
        mRowsAdapter.clear()

        mPb = activity?.findViewById(R.id.pb)
        mPb?.visibility = View.VISIBLE
        Handler().postDelayed({
            setSelectedPosition(0,false)
            for (obj in arrayAdapter) {
                mRowsAdapter.add(
                    ListRow(
                        HeaderItem(obj.header),
                        obj.list
                    )
                )
            }
            if (mRowsAdapter.size() > 0)
                mPb?.visibility = View.GONE
        },0)
    }
}