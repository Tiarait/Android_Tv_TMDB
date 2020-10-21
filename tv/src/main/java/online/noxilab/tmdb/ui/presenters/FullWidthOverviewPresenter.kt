package online.noxilab.tmdb.ui.presenters

import android.animation.Animator
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import online.noxilab.tmdb.R
import info.movito.themoviedbapi.model.MovieDb
import online.noxilab.tmdb.AppConstants
import online.noxilab.tmdb.utils.TextViewScroller
import online.noxilab.tmdb.utils.UtilsText
import android.graphics.Color
import android.os.Handler
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.widget.*
import info.movito.themoviedbapi.model.tv.TvSeries
import online.noxilab.tmdb.utils.CircleProgressBar


class FullWidthOverviewPresenter(detailsPresenter: Presenter,
                                 logoPresenter: DetailsOverviewLogoPresenter) : FullWidthDetailsOverviewRowPresenter(detailsPresenter, logoPresenter) {
    private lateinit var mTitleView: TextView
    private lateinit var subtitleView: TextView
    private lateinit var descView: TextView
    private lateinit var taglineView: TextView
    private lateinit var progressRating: CircleProgressBar
    private lateinit var ratingTextView: TextView
    private lateinit var ratingTextProcView: TextView
    private lateinit var detailsFrameView: FrameLayout
    lateinit var actionsView: HorizontalGridView

    override fun createRowViewHolder(parent: ViewGroup?): RowPresenter.ViewHolder {
        val v = super.createRowViewHolder(parent)
        mTitleView = v.view.findViewById(R.id.lb_details_description_title)
        taglineView = v.view.findViewById(R.id.lb_details_tagline)
        subtitleView = v.view.findViewById(R.id.lb_details_description_subtitle)
        detailsFrameView = v.view.findViewById(R.id.details_frame)
        progressRating = v.view.findViewById(R.id.progress_rating)
        ratingTextView = v.view.findViewById(R.id.progress_rating_text)
        ratingTextProcView = v.view.findViewById(R.id.progress_rating_text_proc)
        actionsView = v.view.findViewById(R.id.details_overview_actions)
        descView = detailsFrameView.rootView.findViewById(R.id.lb_details_description_body)
        return v
    }

    fun setItem(item: Any) {
        val h = mTitleView.context.getString(R.string.h)
        val min = mTitleView.context.getString(R.string.min)
        val budget = mTitleView.context.getString(R.string.budget)
        val revenue = mTitleView.context.getString(R.string.revenue)
        val mln = mTitleView.context.getString(R.string.mln)
        val thous = mTitleView.context.getString(R.string.thous)
        val statusEnded = mTitleView.context.getString(R.string.status_ended)
        val statusReturn = mTitleView.context.getString(R.string.status_return)
        val statusProduction = mTitleView.context.getString(R.string.status_in_production)

        when (item) {
            is MovieDb -> {
                var title = item.title.toUpperCase()
                if (item.title != item.originalTitle)
                    title = UtilsText().delimeterStrings(
                        AppConstants.DOT_DELIMETERSPACE,
                        item.title,
                        item.originalTitle).toUpperCase()
                mTitleView.text = title

                if (!TextUtils.isEmpty(item.tagline)) {
                    taglineView.visibility = View.VISIBLE
                    taglineView.text = item.tagline
                } else taglineView.visibility = View.GONE

                val genres = if (item.genres != null) {
                    UtilsText().itemArrToStr(item.genres)
                } else AppConstants.ELLIPSIS
                val contries = if (item.productionCountries != null) {
                    UtilsText().itemArrToStr(item.productionCountries)
                } else ""
                val duration = if (item.runtime != 0) {
                    val hours = item.runtime/60
                    if (hours > 0 && item.runtime > 60) {
                        val mins = item.runtime - (hours*60)
                        "$hours$h $mins$min"
                    } else "${item.runtime}$min"
                } else ""
                val budgetCount = if (item.budget > 0) {
                    when {
                        item.budget > 1000000 -> {
                            val mlnCount = item.budget.toFloat()/1000000
                            val mlnCountS = String.format("%.03f", mlnCount)
                            if (!mlnCountS.split(".")[1].startsWith("0")) {
                                budget + ": " + mlnCountS.split(".")[0] + mln + " " +
                                        mlnCountS.split(".")[1] + thous
                            } else {
                                budget + ": " + mlnCountS.split(".")[0] + mln
                            }
                        }
                        item.budget > 1000 -> {
                            val mlnCount = item.budget.toFloat()/1000
                            budget +": $" + mlnCount.toInt() + thous
                        }
                        else -> budget +": $" + item.budget
                    }
                } else ""
                val revenueCount = if (item.revenue > 0) {
                    when {
                        item.revenue > 1000000 -> {
                            val mlnCount = item.revenue.toFloat()/1000000
                            val mlnCountS = String.format("%.03f", mlnCount)
                            if (!mlnCountS.split(".")[1].startsWith("0")) {
                                revenue + ": " + mlnCountS.split(".")[0] + mln + " " +
                                        mlnCountS.split(".")[1] + thous
                            } else {
                                revenue + ": " + mlnCountS.split(".")[0] + mln
                            }
                        }
                        item.revenue > 1000 -> {
                            val mlnCount = item.revenue.toFloat()/1000
                            revenue +": $" + mlnCount.toInt() + thous
                        }
                        else -> revenue +": $"+item.revenue
                    }
                } else ""
                subtitleView.text = UtilsText().delimeterStrings(
                    AppConstants.NEWLINE,
                    UtilsText().delimeterStrings(
                        AppConstants.COMMA,
                        UtilsText().safesplit(item.releaseDate, "-").first(),
                        duration,
                        contries,
                        genres),
                    UtilsText().delimeterStrings(
                        AppConstants.DOT_DELIMETERSPACE,
                        budgetCount,
                        revenueCount)
                    )


                descView.text = item.overview

                Handler().postDelayed({
                    when {
                        item.voteAverage < 4 -> progressRating.setColor(Color.RED)
                        item.voteAverage < 7 -> progressRating.setColor(ContextCompat.getColor(progressRating.context, R.color.rate_middle))
                        else -> progressRating.setColor(ContextCompat.getColor(progressRating.context, R.color.default_accent))
                    }
                    progressRating.setProgressWithAnimation(item.voteAverage * 10)
                    ratingTextView.text = (item.voteAverage * 10).toInt().toString()
                    ratingTextProcView.text = "%"
                }, 500)
            }
            is TvSeries -> {
                var title = item.name.toUpperCase()
                if (item.name != item.originalName)
                    title = UtilsText().delimeterStrings(
                        AppConstants.DOT_DELIMETERSPACE,
                        item.name,
                        item.originalName).toUpperCase()
                mTitleView.text = title

                taglineView.visibility = View.GONE
                val genres = if (!item.genres.isNullOrEmpty()) {
                    UtilsText().itemArrToStr(item.genres)
                } else AppConstants.ELLIPSIS
                val contries = if (!item.originCountry.isNullOrEmpty()) {
                    UtilsText().itemArrToStr(item.originCountry)
                } else ""
                val season = if (!item.seasons.isNullOrEmpty()) {
                    val s = item.seasons.last()
                    "s" + s.seasonNumber + "e" + s.episodes.last().episodeNumber
                } else ""
                val duration = if (!item.episodeRuntime.isNullOrEmpty()) {
                    val hours = item.episodeRuntime[0]/60
                    if (hours > 0 && item.episodeRuntime[0] > 60) {
                        val mins = item.episodeRuntime[0] - (hours*60)
                        "$hours$h $mins$min"
                    } else "${item.episodeRuntime[0]}$min"
                } else ""
                val networks = if (!item.networks.isNullOrEmpty()) {
                    UtilsText().itemArrToStr(item.networks)
                } else ""
                val status = if (!TextUtils.isEmpty(item.status)) {
                    item.status
                        .replace("Ended", statusEnded)
                        .replace("Returning Series", statusReturn)
                        .replace("In Production", statusProduction)
                } else ""
                subtitleView.text = UtilsText().delimeterStrings(
                    AppConstants.COMMA,
                    UtilsText().safesplit(item.firstAirDate, "-").first(),
                    duration,
                    season,
                    status,
                    networks,
                    contries,
                    genres
                )
                descView.text = item.overview

                Handler().postDelayed({
                    when {
                        item.voteAverage < 4 -> progressRating.setColor(Color.RED)
                        item.voteAverage < 7 -> progressRating.setColor(ContextCompat.getColor(progressRating.context, R.color.rate_middle))
                        else -> progressRating.setColor(ContextCompat.getColor(progressRating.context, R.color.default_accent))
                    }
                    progressRating.setProgressWithAnimation(item.voteAverage * 10)
                    ratingTextView.text = (item.voteAverage * 10).toInt().toString()
                    ratingTextProcView.text = "%"
                }, 500)
            }
        }
        TextViewScroller(mTitleView).start()
        TextViewScroller(taglineView).start()
    }

    override fun onLayoutLogo(viewHolder: ViewHolder?, oldState: Int, logoChanged: Boolean) {
        val v = viewHolder?.logoViewHolder?.view
        val lp = v?.layoutParams as ViewGroup.MarginLayoutParams
        lp.topMargin = 0
        v.layoutParams = lp

        val marginTop = v.resources.getDimensionPixelSize(R.dimen.lb_details_v2_logo_margin_top)
        val marginTopFull = v.resources.getDimensionPixelSize(R.dimen.lb_details_v2_logo_margin_top_full)

        v.animate().cancel()
        initialState = viewHolder.state
        when (viewHolder.state) {
            STATE_FULL -> {
                if (v.translationY == (marginTop + marginTopFull).toFloat()) {
//                    v.animate().scaleXBy(.1f).setDuration(300).start()
//                    v.animate().scaleYBy(.1f).setDuration(300).start()
                    v.animate()
                        .translationYBy(-marginTopFull.toFloat())
                        .setDuration(300)
                        .setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animator: Animator) {

                            }

                            override fun onAnimationEnd(animator: Animator) {
                                v.translationY = marginTop.toFloat()
//                                v.scaleX = 1f
//                                v.scaleY = 1f
                            }

                            override fun onAnimationCancel(animator: Animator) {
                                v.translationY = marginTop.toFloat()
//                                v.scaleX = 1f
//                                v.scaleY = 1f
                            }

                            override fun onAnimationRepeat(animator: Animator) {

                            }
                        })
                        .start()
                } else v.translationY = marginTop.toFloat()
            }
            STATE_HALF -> {
                if (v.translationY != (marginTop + marginTopFull).toFloat()) {
//                    v.animate().scaleXBy(-.1f).setDuration(300).start()
//                    v.animate().scaleYBy(-.1f).setDuration(300).start()
                    v.animate().translationYBy(marginTopFull.toFloat())
                        .setDuration(300)
                        .setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animator: Animator) {

                            }

                            override fun onAnimationEnd(animator: Animator) {
                                v.translationY = (marginTop+marginTopFull).toFloat()
//                                v.scaleX = .9f
//                                v.scaleY = .9f
                            }

                            override fun onAnimationCancel(animator: Animator) {
                                v.translationY = (marginTop+marginTopFull).toFloat()
//                                v.scaleX = .9f
//                                v.scaleY = .9f
                            }

                            override fun onAnimationRepeat(animator: Animator) {

                            }
                        })
                        .start()
                }
            }
            STATE_SMALL -> {
//                translateY = 0
            }
            else -> {
//                translateY = v.resources.getDimensionPixelSize(androidx.leanback.R.dimen.lb_details_v2_blank_height)
            }
        }
        v.translationX = v.resources.getDimensionPixelSize(R.dimen.lb_details_v2_logo_margin_start).toFloat()
    }
}
