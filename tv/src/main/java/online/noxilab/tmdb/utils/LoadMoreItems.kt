package online.noxilab.tmdb.utils

import android.os.AsyncTask
import android.text.TextUtils
import info.movito.themoviedbapi.TmdbApi
import info.movito.themoviedbapi.model.MovieDb
import info.movito.themoviedbapi.model.people.Person
import info.movito.themoviedbapi.model.tv.TvSeries
import online.noxilab.tmdb.AppConstants
import online.noxilab.tmdb.models.RowObjectAdapter

class LoadMoreItems(private val rowObject: RowObjectAdapter, private val tmdb: TmdbApi,
                    private val cListener: ListListener) : AsyncTask<Void, Void, Void>() {
    val list = ArrayList<Any>()

    init {
        execute()
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        cListener.onResult(list)
    }

    override fun doInBackground(vararg params: Void?): Void? {
        rowObject.page += 1
        when (rowObject.type) {
            AppConstants.TypeRows.MOVIE_NOW -> {
                val movieNowPlaying =
                    tmdb.movies.getNowPlayingMovies("ru-RU", rowObject.page, "")
                for (item: MovieDb in movieNowPlaying.results) {
                    if (!TextUtils.isEmpty(item.title))
                        list.add(item)
                }
            }
            AppConstants.TypeRows.MOVIE_UPCOMING -> {
                val movieUpcoming = tmdb.movies.getUpcoming("ru-RU", rowObject.page, "")
                for (item: MovieDb in movieUpcoming.results) {
                    if (!TextUtils.isEmpty(item.title))
                        list.add(item)
                }
            }
            AppConstants.TypeRows.MOVIE_POPULAR -> {
                val moviePopular = tmdb.movies.getPopularMovies("ru-RU", rowObject.page)
                for (item: MovieDb in moviePopular.results) {
                    if (!TextUtils.isEmpty(item.title))
                        list.add(item)
                }
            }
            AppConstants.TypeRows.MOVIE_TOP -> {
                val movieTopRated = tmdb.movies.getTopRatedMovies("ru-RU", rowObject.page)
                for (item: MovieDb in movieTopRated.results) {
                    if (!TextUtils.isEmpty(item.title))
                        list.add(item)
                }
            }
            AppConstants.TypeRows.SERIAL_TODAY -> {
                val serialToday = tmdb.tvSeries.getAiringToday("ru-RU", rowObject.page, null)
                for (item: TvSeries in serialToday.results) {
                    if (!TextUtils.isEmpty(item.name))
                        list.add(item)
                }
            }
            AppConstants.TypeRows.SERIAL_ONTV -> {
                val serialTv = tmdb.tvSeries.getOnTheAir("ru-RU", rowObject.page)
                for (item: TvSeries in serialTv.results) {
                    if (!TextUtils.isEmpty(item.name))
                        list.add(item)
                }
            }
            AppConstants.TypeRows.SERIAL_POPULAR -> {
                val serialPopular = tmdb.tvSeries.getPopular("ru-RU", rowObject.page)
                for (item: TvSeries in serialPopular.results) {
                    if (!TextUtils.isEmpty(item.name))
                        list.add(item)
                }
            }
            AppConstants.TypeRows.SERIAL_TOP -> {
                val serialTopRated = tmdb.tvSeries.getTopRated("ru-RU", rowObject.page)
                for (item: TvSeries in serialTopRated.results) {
                    if (!TextUtils.isEmpty(item.name))
                        list.add(item)
                }
            }
            AppConstants.TypeRows.PEOPLE -> {
                val people = tmdb.people.getPersonPopular(rowObject.page)
                for (item: Person in people.results) {
                    if (!TextUtils.isEmpty(item.name))
                        list.add(item)
                }
            }
        }
        return null
    }
}