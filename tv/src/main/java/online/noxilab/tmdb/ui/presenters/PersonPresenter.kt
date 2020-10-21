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

import android.view.LayoutInflater
import androidx.leanback.widget.Presenter
import androidx.core.content.ContextCompat
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import info.movito.themoviedbapi.model.people.Person
import online.noxilab.tmdb.R
import kotlin.properties.Delegates

/**
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an ImageCardView.
 */
class PersonPresenter : Presenter() {
    private var sSelectedBackgroundColor: Int by Delegates.notNull()
    private var sDefaultBackgroundColor: Int by Delegates.notNull()

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        sDefaultBackgroundColor = ContextCompat.getColor(parent.context,
            R.color.default_background
        )
        sSelectedBackgroundColor =
            ContextCompat.getColor(parent.context, R.color.default_accent)

        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.card_person, null)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val thumb = viewHolder.view.findViewById(R.id.thumb) as ImageView
        val title = viewHolder.view.findViewById(R.id.title) as TextView
        thumb.setImageResource(R.drawable.ic_person_circle)
        when (item) {
            is Person -> {
                title.text = item.name
                Glide.with(viewHolder.view.context)
                    .load("https://image.tmdb.org/t/p/w235_and_h235_face"+item.profilePath)
                    .apply(RequestOptions.circleCropTransform())
                    .error(ContextCompat.getDrawable(thumb.context, R.drawable.ic_person_circle))
                    .into(thumb)
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
    }

    companion object {
        private val TAG = "CardPresenter"
        private val CARD_WIDTH = 264
        private val CARD_HEIGHT = 396
    }
}
