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

import android.animation.Animator
import androidx.leanback.widget.Presenter
import androidx.core.content.ContextCompat
import android.view.ViewGroup
import androidx.leanback.widget.PresenterSelector

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.leanback.widget.Action
import online.noxilab.tmdb.R
import android.widget.Button
import online.noxilab.tmdb.utils.UtilsView


/**
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an ImageCardView.
 */
class CustomActionPresenterSelector : PresenterSelector() {
    private val mOneLineActionPresenter = OneLineActionPresenter()

    override fun getPresenter(item: Any?): Presenter {
        return mOneLineActionPresenter
    }

    class ActionViewHolder(view: View, internal var mLayoutDirection: Int) :
        Presenter.ViewHolder(view) {
        internal var mAction: Action? = null
        var mButton: Button = view.findViewById<View>(androidx.leanback.R.id.lb_action_button) as Button

    }

    internal class OneLineActionPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(androidx.leanback.R.layout.lb_action_1_line, parent, false)
            return ActionViewHolder(v, parent.layoutDirection)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            val action = item as Action
            val vh = viewHolder as ActionViewHolder
            vh.mAction = action

            val icon = action.icon

            val r = vh.mButton.context.resources.getDimensionPixelOffset(R.dimen.action_rounded)
            vh.mButton.background = UtilsView().getCustomShape(
                ContextCompat.getColor(
                    vh.mButton.context,
                    R.color.default_background_dark
                ), r)
            if (icon != null) {
                if (vh.mLayoutDirection == View.LAYOUT_DIRECTION_RTL) {
                    vh.mButton.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
                } else {
                    vh.mButton.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
                }
            } else {
                vh.mButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
            val line1 = action.label1
            val line2 = action.label2
            when {
                TextUtils.isEmpty(line1) -> vh.mButton.text = line2
                TextUtils.isEmpty(line2) -> vh.mButton.text = line1
                else -> vh.mButton.text = line1.toString() + "\n" + line2
            }
            vh.mButton.elevation = 4f
            vh.view.setOnFocusChangeListener { view, b ->
                if (b) {
                    vh.mButton.setTextColor(ContextCompat.getColor(
                        vh.mButton.context,
                        R.color.default_background_dark
                    ))
                    vh.mButton.background = UtilsView().getCustomShape(
                        ContextCompat.getColor(
                            vh.mButton.context,
                            R.color.default_accent
                        ), r)
                    vh.mButton.animate()
                        .translationZBy(6f)
                        .setDuration(100)
                        .setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animator: Animator) {

                            }

                            override fun onAnimationEnd(animator: Animator) {
                                vh.mButton.translationZ = 10f
                            }

                            override fun onAnimationCancel(animator: Animator) {
                                vh.mButton.translationZ = 10f
                            }

                            override fun onAnimationRepeat(animator: Animator) {

                            }
                        })
                        .start()
                } else {
                    vh.mButton.setTextColor(ContextCompat.getColor(
                        vh.mButton.context,
                        R.color.card_primary_text
                    ))
                    vh.mButton.background = UtilsView().getCustomShape(
                        ContextCompat.getColor(
                            vh.mButton.context,
                            R.color.default_background_dark
                        ), r)
                    vh.mButton.animate()
                        .translationZBy(-6f)
                        .setDuration(100)
                        .setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animator: Animator) {

                            }

                            override fun onAnimationEnd(animator: Animator) {
                                vh.mButton.translationZ = 4f
                            }

                            override fun onAnimationCancel(animator: Animator) {
                                vh.mButton.translationZ = 4f
                            }

                            override fun onAnimationRepeat(animator: Animator) {

                            }
                        })
                        .start()
                }
                vh.mButton.requestLayout()
            }

        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
            (viewHolder as ActionViewHolder).mAction = null
        }
    }

}
