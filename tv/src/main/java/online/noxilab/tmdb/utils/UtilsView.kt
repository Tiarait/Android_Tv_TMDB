package online.noxilab.tmdb.utils

import android.animation.Animator
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import online.noxilab.tmdb.R


class UtilsView {
    fun getCustomShape(backgroundColor: Int, radius: Int): GradientDrawable {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadii = floatArrayOf(
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat()
        )
        shape.setColor(backgroundColor)
        return shape
    }

    fun animationBlink(v: View) {
        val animation = AlphaAnimation(1f, 0.5f) //to change visibility from visible to invisible
        animation.duration = 1000 //1 second duration for each animation cycle
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE //repeating indefinitely
        animation.repeatMode = Animation.REVERSE //animation will start from end point once ended.
        v.startAnimation(animation) //to start animation
    }

    fun animScaleY(v: View, b: Boolean) {
        val sc = if (b) 1f else -1f
        v.animate().cancel()
        v.animate().scaleY(sc).setDuration(300)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    v.scaleY = sc
                }

                override fun onAnimationCancel(animator: Animator) {
                    v.scaleY = -sc
                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })
            .start()
    }

    fun hideBottomDrawer(v: View, duration: Long) {
        v.translationY = 0f
        v.animate().cancel()
        v.animate().translationY(v.context.resources.getDimensionPixelOffset(R.dimen.lb_details_v2_logo_margin_top).toFloat()).setDuration(duration)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    v.translationY = v.context.resources.getDimensionPixelOffset(R.dimen.lb_details_v2_logo_margin_top).toFloat()
                }

                override fun onAnimationCancel(animator: Animator) {
                    v.translationY = 0f
                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })
            .start()
    }

    fun showBottomDrawer(v: View, duration: Long) {

        v.translationY = v.context.resources.getDimensionPixelOffset(R.dimen.lb_details_v2_logo_margin_top).toFloat()
        v.animate().cancel()
        v.animate().translationY(0f).setDuration(duration)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    v.translationY = 0f
                }

                override fun onAnimationCancel(animator: Animator) {
                    v.translationY = v.context.resources.getDimensionPixelOffset(R.dimen.lb_details_v2_logo_margin_top).toFloat()
                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })
            .start()
    }

    fun hideTopDrawer(v: View, duration: Long) {
        v.translationY = 0f
        v.animate().cancel()
        v.animate().translationY(-v.context.resources.getDimensionPixelOffset(R.dimen.lb_details_v2_logo_margin_top).toFloat())
            .setDuration(duration)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    v.translationY = -v.context.resources.getDimensionPixelOffset(R.dimen.lb_details_v2_logo_margin_top).toFloat()
                }

                override fun onAnimationCancel(animator: Animator) {
                    v.translationY = 0f
                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })
            .start()
    }

    fun showTopDrawer(v: View, duration: Long) {
        v.translationY = -v.context.resources.getDimensionPixelOffset(R.dimen.lb_details_v2_logo_margin_top).toFloat()
        v.animate().cancel()
        v.animate().translationY(0f).setDuration(duration)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    v.translationY = 0f
                }

                override fun onAnimationCancel(animator: Animator) {
                    v.translationY = -v.context.resources.getDimensionPixelOffset(R.dimen.lb_details_v2_logo_margin_top).toFloat()
                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })
            .start()
    }
}