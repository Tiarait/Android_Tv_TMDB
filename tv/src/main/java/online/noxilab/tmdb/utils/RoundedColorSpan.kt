package online.noxilab.tmdb.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Paint.FontMetricsInt
import android.text.style.BackgroundColorSpan
import android.text.style.ReplacementSpan
import androidx.annotation.NonNull


class RoundedColorSpan(
    private val mColorBackground: Int,
    private val mColorText: Int,
    private val mCornerRadius: Float,
    private val mPaddingStart: Int,
    private val mPaddingEnd: Int) : ReplacementSpan() {

    override fun getSize(
        paint: Paint, text: CharSequence,
        start: Int,
        end: Int,
        fm: FontMetricsInt?
    ): Int {
        return (mPaddingStart + paint.measureText(
            text.subSequence(
                start,
                end
            ).toString()
        ) + mPaddingEnd).toInt()
    }

    override fun draw(
        @NonNull canvas: Canvas, text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int, @NonNull paint: Paint
    ) {
        val width = paint.measureText(text.subSequence(start, end).toString())
        val rect = RectF(
            x - mPaddingStart,
            top.toFloat(),
            x + width + mPaddingEnd,
            bottom.toFloat()
        )
        paint.color = mColorBackground
        canvas.drawRoundRect(rect, mCornerRadius, mCornerRadius, paint)
        paint.color = mColorText
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }
}