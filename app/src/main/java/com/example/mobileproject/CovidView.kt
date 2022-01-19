package com.example.mobileproject

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CovidView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val paint: Paint = Paint()
        paint.setColor(Color.CYAN)//30,144,255
        paint.style = Paint.Style.STROKE
        canvas?.drawCircle(75f,75f,60f,paint)
    }
}