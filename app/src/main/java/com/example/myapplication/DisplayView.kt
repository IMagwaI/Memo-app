package com.example.myapplication

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import com.example.myapplication.R

/**
 * check PaintView.kt
 */
private lateinit var extraCanvas: Canvas
lateinit var extraBitmapDisplay: Bitmap
private const val STROKE_WIDTH = 12f // has to be float

class DisplayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)
    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)

    }
    private var path = Path()


    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        if (::extraBitmapDisplay.isInitialized){
            val mutableBitmap: Bitmap = extraBitmapDisplay.copy(Bitmap.Config.ARGB_8888, true)
            extraCanvas = Canvas(mutableBitmap)
        }else{
            extraBitmapDisplay.recycle()
            extraBitmapDisplay = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            extraCanvas = Canvas(extraBitmapDisplay)
        }
        //extraBitmap.recycle()
        extraCanvas.drawColor(backgroundColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmapDisplay, 0f, 0f, null)
    }
}