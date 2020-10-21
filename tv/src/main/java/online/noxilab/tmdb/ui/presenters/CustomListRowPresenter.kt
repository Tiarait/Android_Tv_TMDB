package online.noxilab.tmdb.ui.presenters

import android.graphics.Color
import android.text.SpannableString
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.*
import online.noxilab.tmdb.utils.TextViewScroller
import online.noxilab.tmdb.R
import online.noxilab.tmdb.utils.CircleProgressBar


class CustomListRowPresenter: ListRowPresenter() {
    var curTitle: String = ""
    private var descView: View? = null
    private var titleScroller: TextViewScroller? = null
    var expanded = false
//    init {
//        headerPresenter = CustomRowHeaderPresenter()
//    }

    fun setRating(rating: Float) {
        val progressRating = descView?.findViewById<CircleProgressBar>(R.id.progress_rating)
        val ratingTextView = descView?.findViewById<TextView>(R.id.progress_rating_text)
        val ratingTextProcView = descView?.findViewById<TextView>(R.id.progress_rating_text_proc)
        when {
            rating < 4 -> progressRating?.setColor(Color.RED)
            rating < 7 -> progressRating?.setColor(ContextCompat.getColor(progressRating.context, R.color.rate_middle))
            else -> progressRating?.setColor(ContextCompat.getColor(progressRating.context, R.color.default_accent))
        }
        progressRating?.setStrokeWidth(10f)
        progressRating?.setProgressWithAnimation(rating * 10)
        ratingTextView?.text = (rating * 10).toInt().toString()
        ratingTextProcView?.text = "%"
    }

    fun setDesc(title: String, subtitle: SpannableString) {
        val titleView = descView?.findViewById<TextView>(R.id.row_title)
        val subtitleView = descView?.findViewById<TextView>(R.id.row_subtitle)
        if (!TextUtils.isEmpty(title)) {
            if (title != curTitle || titleView?.text!!.isEmpty()) {
                curTitle = title

                titleScroller?.stop()
                titleView?.setText(curTitle)
                titleScroller = TextViewScroller(titleView)
                titleScroller?.start()

                titleView?.alpha = 0f
                titleView?.animate()
                    ?.alphaBy(1f)
                    ?.setDuration(400)
                    ?.start()
                subtitleView?.alpha = 0f
                subtitleView?.animate()
                    ?.alphaBy(1f)
                    ?.setDuration(400)
                    ?.start()
            }
        } else {
            titleView?.setText("")
        }
        if (!TextUtils.isEmpty(subtitle)) {
            subtitleView?.setText(subtitle)
        } else {
            subtitleView?.setText("")
        }
    }

    //worked if row is first
    override fun onRowViewExpanded(holder: RowPresenter.ViewHolder?, expanded: Boolean) {
        super.onRowViewExpanded(holder, expanded)
        this.expanded = expanded
        if (expanded) {
//            if (descView?.findViewById<TextView>(R.id.row_title)?.text.toString().isNotEmpty())
                descView?.visibility = View.VISIBLE
        } else {
            titleScroller = null
            descView?.visibility = View.GONE
        }
    }

    //worked if row not first
    override fun onSelectLevelChanged(holder: RowPresenter.ViewHolder?) {
        super.onSelectLevelChanged(holder)
        if (holder?.row?.headerItem?.name != null) {
            descView = holder.view.findViewById(R.id.description)
        }
    }

    override fun onRowViewSelected(holder: RowPresenter.ViewHolder, selected: Boolean) {
        super.onRowViewSelected(holder, selected)
        if (selected && expanded) {
//            if (descView?.findViewById<TextView>(R.id.row_title)?.text.toString().isNotEmpty())
                descView?.visibility = View.VISIBLE
        } else {
            titleScroller = null
            descView?.visibility = View.GONE
        }
    }

    override fun createRowViewHolder(parent: ViewGroup): RowPresenter.ViewHolder {
        val viewHolder = super.createRowViewHolder(parent)
        if (descView == null)
            descView = viewHolder.view.findViewById(R.id.description)

        with((viewHolder.view as ListRowView).gridView) {
            //WINDOW_ALIGN_BOTH_EDGE - при окончании фокус вправо
            //WINDOW_ALIGN_NO_EDGE - при окончании фокус наместе
            windowAlignment = BaseGridView.WINDOW_ALIGN_NO_EDGE
            windowAlignmentOffsetPercent = 0f
            windowAlignmentOffset = parent.resources.getDimensionPixelSize(R.dimen.lb_browse_padding_start)
            itemAlignmentOffsetPercent = 0f
        }

        return viewHolder
    }
}