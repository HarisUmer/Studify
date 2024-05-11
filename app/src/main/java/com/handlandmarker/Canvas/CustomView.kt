package com.handlandmarker.Canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

var flag: Boolean = false

class CustomView(context: Context,attrs: AttributeSet?) : View(context,attrs) {

    private var paint: Paint = Paint()
    private var paint1: Paint = Paint()
    private var path: Path = Path()

    private var pointerX: Float =10F
    private var pointerY: Float =10F
    private var lastTouchX: Float = 0F
    private var lastTouchY: Float = 0F

    private var pathPoints: MutableList<Pair<Float, Float>> = mutableListOf()


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
        // Update the path to draw a line from the last touch position to the current cursor position
        paint.color = Color.GREEN
        if (flag==false)
        {
            lastTouchX = x*width
            lastTouchY = y*height
            flag= true
        }
        //path.reset() // Clear the previous path
        path.moveTo(lastTouchX, lastTouchY) // Move to the last touch position
        path.lineTo(x * width, y * height) // Draw a line to the current cursor position

        // Update the last touch position
        lastTouchX = x * width
        lastTouchY = y * height

        pointerX = x*width
        pointerY = y*height

        // Invalidate the view to trigger onDraw
        invalidate()
    }
    fun removeAtCursor(x: Float, y: Float){
        // Update the path to draw a line from the last touch position to the current cursor position
        paint.color = Color.YELLOW
        if (flag==false)
        {
            lastTouchX = x*width
            lastTouchY = y*height
            flag= true
        }
        //path.reset() // Clear the previous path
        path.moveTo(lastTouchX, lastTouchY) // Move to the last touch position
        path.lineTo(x * width, y * height) // Draw a line to the current cursor position

        // Update the last touch position
        lastTouchX = x * width
        lastTouchY = y * height

        pointerX = x*width
        pointerY = y*height
        //paint.color = Color.GREEN
        // Invalidate the view to trigger onDraw
        invalidate()
    }


    fun moveCursor(x: Float, y: Float) {
        // Convert x and y to view coordinates
        val xInView = x * width
        val yInView = y * height

        // Update pointer position
        pointerX = xInView
        pointerY = yInView

        flag = false

        // Invalidate the view to trigger onDraw
        invalidate()
    }


    fun clearCanvas() {
        path.reset()
        invalidate()
    }
}
