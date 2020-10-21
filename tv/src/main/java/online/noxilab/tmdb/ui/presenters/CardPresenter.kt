/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package online.noxilab.tmdb.ui.presenters

import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import androidx.core.content.ContextCompat
import android.view.ViewGroup
import androidx.leanback.widget.BaseCardView.*

import com.bumptech.glide.Glide
import info.movito.themoviedbapi.model.MovieDb
import info.movito.themoviedbapi.model.tv.TvSeries
import online.noxilab.tmdb.AppConstants
import online.noxilab.tmdb.R
import kotlin.properties.Delegates

/**
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an ImageCardView.
 */
class CardPresenter : Presenter() {
    private var sSelectedBackgroundColor: Int by Delegates.notNull()
    private var sDefaultBackgroundColor: Int by Delegates.notNull()

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        sDefaultBackgroundColor = ContextCompat.getColor(parent.context,
            R.color.default_background
        )
//        sSelectedBackgroundColor =
//            ContextCompat.getColor(parent.context, R.color.default_accent)
        sSelectedBackgroundColor =
            ContextCompat.getColor(parent.context, android.R.color.transparent)

        val cardView = object : ImageCardView(parent.context) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        updateCardBackgroundColor(cardView, false)
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val cardView = viewHolder.view as ImageCardView
        cardView.mainImageView.transitionName = AppConstants.TRANSITION_POSTER
        when (item) {
            is MovieDb -> {
                cardView.titleText = item.title
                cardView.contentText = item.originalTitle
                cardView.cardType = CARD_TYPE_MAIN_ONLY
                cardView.setMainImageDimensions(
                    CARD_WIDTH,
                    CARD_HEIGHT
                )
                Glide.with(viewHolder.view.context)
                    .load("https://image.tmdb.org/t/p/w185_and_h278_bestv2"+item.posterPath)
                    .centerCrop()
                    .into(cardView.mainImageView)
            }
            is TvSeries -> {
                cardView.cardType = CARD_TYPE_MAIN_ONLY
                cardView.setMainImageDimensions(
                    CARD_WIDTH,
                    CARD_HEIGHT
                )
                Glide.with(viewHolder.view.context)
                    .load("https://image.tmdb.org/t/p/w185_and_h278_bestv2"+item.posterPath)
                    .centerCrop()
                    .into(cardView.mainImageView)
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        // Remove references to images so that the garbage collector can free up memory
        cardView.badgeImage = null
        cardView.mainImage = null
    }

    private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
        val color = if (selected) sSelectedBackgroundColor else sDefaultBackgroundColor
        // Both background colors should be set because the view's background is temporarily visible
        // during animations.
        view.setBackgroundColor(color)
        view.setInfoAreaBackgroundColor(color)
    }

    companion object {
        private val TAG = "CardPresenter"

//        private val CARD_WIDTH = 176
//        private val CARD_HEIGHT = 313
        private val CARD_WIDTH = 264
        private val CARD_HEIGHT = 396
    }
}
