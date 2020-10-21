package online.noxilab.tmdb.ui.presenters

import android.content.Context
import androidx.leanback.widget.Presenter
import android.view.ViewGroup

import androidx.leanback.widget.RowHeaderPresenter
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.leanback.widget.PageRow
import online.noxilab.tmdb.models.HeaderIconItem
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import online.noxilab.tmdb.AppConstants
import online.noxilab.tmdb.R


/**
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an ImageCardView.
 */
class HeaderIconItemPresenter : RowHeaderPresenter() {

    override fun onCreateViewHolder(viewGroup: ViewGroup): ViewHolder {
        val inflater = viewGroup.context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.header_icon_item, null)
        val label = view.findViewById<TextView>(R.id.header_label)
        val icon = view.findViewById<ImageView>(R.id.header_icon)

        icon.setColorFilter(ContextCompat.getColor(view.context,
            R.color.card_secondary_text
        ), android.graphics.PorterDuff.Mode.MULTIPLY);
        label.setTextColor(ContextCompat.getColor(view.context,
            R.color.card_secondary_text
        ))
        label.ellipsize = TextUtils.TruncateAt.END

        view.setOnFocusChangeListener { v, b ->
            if (b) {
                icon.setColorFilter(ContextCompat.getColor(v.context,
                    R.color.card_primary_text
                ), android.graphics.PorterDuff.Mode.MULTIPLY);
                label.setTextColor(ContextCompat.getColor(v.context,
                    R.color.card_primary_text
                ))
                label.ellipsize = TextUtils.TruncateAt.MARQUEE
            } else {
                icon.setColorFilter(ContextCompat.getColor(v.context,
                    R.color.card_secondary_text
                ), android.graphics.PorterDuff.Mode.MULTIPLY);
                label.setTextColor(ContextCompat.getColor(v.context,
                    R.color.card_secondary_text
                ))
                label.ellipsize = TextUtils.TruncateAt.END
            }
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, o: Any?) {
        val iconHeaderItem = (o as PageRow).headerItem as HeaderIconItem
        val rootView = viewHolder.view
        rootView.alpha = .7f

        val iconView = rootView.findViewById(R.id.header_icon) as ImageView
        val iconResId = iconHeaderItem.iconResId
        if (iconResId != HeaderIconItem.ICON_NONE) { // Show icon only when it is set.
            val icon = rootView.resources.getDrawable(iconResId, null)
            iconView.setImageDrawable(icon)
        }

        val label = rootView.findViewById(R.id.header_label) as TextView
        val space = rootView.findViewById<View>(R.id.space)
        if (iconHeaderItem.name == AppConstants.SPACE_TEXT) {
            iconView.visibility = View.GONE
            label.visibility = View.GONE
            space.visibility = View.VISIBLE
            rootView.isFocusable = false
        } else {
            space.visibility = View.GONE
            iconView.visibility = View.VISIBLE
            label.visibility = View.VISIBLE
            rootView.isFocusable = true
            label.text = iconHeaderItem.name
        }
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
    }

    override fun onSelectLevelChanged(holder: ViewHolder) {
        holder.view.alpha = .7f
    }

}