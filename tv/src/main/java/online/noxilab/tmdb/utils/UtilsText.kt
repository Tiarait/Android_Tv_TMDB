package online.noxilab.tmdb.utils

import android.animation.ValueAnimator
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import info.movito.themoviedbapi.model.Genre
import info.movito.themoviedbapi.model.ProductionCountry
import info.movito.themoviedbapi.model.tv.Network

class UtilsText {

    fun delimeterStrings(delimeter: String, vararg list: String?): String {
        val result = StringBuilder()
        for (item in list) {
            if (!TextUtils.isEmpty(item)) {
                if (result.isNotEmpty()) {
                    result.append(delimeter)
                }
                result.append(item)
            }
        }
        return result.toString()
    }

    fun changeTextColor(textView: TextView, fromColor: Int, toColor: Int, direction: Int = View.LAYOUT_DIRECTION_LTR, duration:Long = 200) {

        var startValue = 0
        var endValue = 0
        if(direction == View.LAYOUT_DIRECTION_LTR){
            startValue = 0
            endValue = textView.text.length
        }
        else if(direction == View.LAYOUT_DIRECTION_RTL) {
            startValue = textView.text.length
            endValue = 0
        }

        textView.setTextColor(fromColor)
        val valueAnimator = ValueAnimator.ofInt(startValue, endValue)
        valueAnimator.addUpdateListener { animator ->
            val spannableString = SpannableString(
                textView.text
            )

            if (direction == View.LAYOUT_DIRECTION_LTR) spannableString.setSpan(
                ForegroundColorSpan(
                    toColor
                ), startValue, animator.animatedValue.toString().toInt(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            else if (direction == View.LAYOUT_DIRECTION_RTL) spannableString.setSpan(
                ForegroundColorSpan(
                    toColor
                ), animator.animatedValue.toString().toInt(),spannableString.length , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textView.text = spannableString
        }
        valueAnimator.duration = duration
        valueAnimator.start()
    }

    fun itemArrToStr(list: List<Any>): String {
        val arrList = ArrayList<String>()
        for (any in list) {
            when (any) {
                is ProductionCountry -> arrList.add(any.name)
                is Genre -> arrList.add(any.name)
                is Network -> arrList.add(any.name)
                is String -> arrList.add(any)
            }
        }
        return arrList.joinToString()
    }

    fun safesplit(s: String, delimiters: String): List<String> {
        return if (s.contains(delimiters))
            s.split(delimiters)
        else {
            val list = ArrayList<String>()
            list.add(s)
            list
        }
    }

    fun msToStringTime(ms: Int): String {
        val durationSec = ms.toFloat()/1000
        val min = (durationSec/60).toInt()
        val sec = (durationSec - min*60).toInt()
        return when {
            min < 10 -> {
                if (sec < 10) "0$min:0$sec"
                else "0$min:$sec"
            }
            sec < 10 -> {
                "$min:0$sec"
            }
            else -> {
                "$min:$sec"
            }
        }
    }
}