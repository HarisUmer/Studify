package com.handlandmarker.Canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class CustomView(context: Context,attrs: AttributeSet?) : View(context,attrs) {

    private var paint: Paint = Paint()
    private var paint1: Paint = Paint()
    private var path: Path = Path()

    private var pointerX: Float =10F
    private var pointerY: Float =10F
    init {

        paint.apply {
            color = Color.GREEN
            isAntiAlias = true
            strokeWidth = 10f
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.BEVEL
            strokeCap = Paint.Cap.SQUARE
        }
        paint1.apply {
            color = Color.BLACK
            isAntiAlias = true
            strokeWidth = 20f
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.BEVEL
            strokeCap = Paint.Cap.SQUARE
        }
    }

    private fun drawPointer(canvas: Canvas) {
        // Draw a circle or any shape to represent the pointer at the current position
        val pointerRadius = 20f
        canvas.drawCircle(pointerX, pointerY, pointerRadius, paint1)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas?.drawPath(path, paint)
        drawPointer(canvas)
    }

    fun drawWithCoordinates(x: Float, y: Float) {
        var x1 = x * 400
        var y1 =y* 400
        path.lineTo(x1, y1)
        pointerX = x1
        pointerY = y1
        invalidate()

    }

    fun clearCanvas() {
        path.reset()
        invalidate()
    }
}
