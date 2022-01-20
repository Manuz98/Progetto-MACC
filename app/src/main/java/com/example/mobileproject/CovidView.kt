package com.example.mobileproject

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.R

import androidx.core.content.ContextCompat




class CovidView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val paint: Paint = Paint() //cerchio
        paint.setColor(Color.parseColor("#1E90FF"))
        paint.strokeWidth = 3f
        paint.style = Paint.Style.FILL_AND_STROKE
        canvas?.drawCircle(90f,90f,55f,paint)

        val paint1: Paint = Paint() //punti bianchi
        paint1.setColor(Color.WHITE)
        paint1.strokeWidth = 3f
        paint.style = Paint.Style.FILL
        canvas?.drawCircle(55f,75f, 6f, paint1) //sinistra
        canvas?.drawCircle(55f,107f, 6f, paint1) //sinistra
        canvas?.drawCircle(90f,55f, 6f, paint1) //centro
        canvas?.drawCircle(90f,90f, 6f, paint1) //centro
        canvas?.drawCircle(90f,125f, 6f, paint1) //centro
        canvas?.drawCircle(125f,75f, 6f, paint1) //destra
        canvas?.drawCircle(125f,107f, 6f, paint1) //destra

        val paint2: Paint = Paint() //linee
        paint2.setColor(Color.parseColor("#1E90FF"))
        paint2.strokeWidth = 10f
        paint2.style = Paint.Style.FILL
        canvas?.drawLine(90f, 35f, 90f, 15f, paint2) //sopra
        canvas?.drawLine(90f, 145f, 90f, 165f, paint2) //sotto
        canvas?.drawLine(55f, 55f, 40f, 40f, paint2) //alto sinistra
        canvas?.drawLine(35f, 91f, 20f, 91f, paint2) //sinistra
        canvas?.drawLine(55f, 125f, 40f, 140f, paint2) //basso sinistra
        canvas?.drawLine(125f, 55f, 140f, 40f, paint2) // alto destra
        canvas?.drawLine(145f, 91f, 160f, 91f, paint2) //destra
        canvas?.drawLine(125f, 125f, 140f, 140f, paint2) //basso destra

        val paint3: Paint = Paint() //cerchi piccoli
        paint3.setColor(Color.parseColor("#1E90FF"))
        paint3.strokeWidth = 3f
        paint3.style = Paint.Style.FILL
        canvas?.drawCircle(90f,15f,10f, paint3) //sopra
        canvas?.drawCircle(90f,165f,10f, paint3) //sotto
        canvas?.drawCircle(40f,40f,10f, paint3) //alto sinistra
        canvas?.drawCircle(20f,91f,10f, paint3) //sinistra
        canvas?.drawCircle(40f,140f,10f, paint3) //basso sinistra
        canvas?.drawCircle(140f,40f,10f, paint3) //alto destra
        canvas?.drawCircle(160f,91f,10f, paint3) //destra
        canvas?.drawCircle(140f,140f,10f, paint3) //basso destra
    }
}