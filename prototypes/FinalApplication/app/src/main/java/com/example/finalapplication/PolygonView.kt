package com.example.finalapplication

import android.content.Context
import android.graphics.*
import android.view.View
import kotlin.random.Random

class PolygonView(context: Context, private val points: List<List<Double>>) : View(context) {

    private val paint = Paint().apply {
        color = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    companion object {
        const val SCALE = 60f // 調整尺寸
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val path = Path()
        points.forEachIndexed { i, pt ->
            val x = pt[0].toFloat() * SCALE
            val y = pt[1].toFloat() * SCALE
            if (i == 0) path.moveTo(x, y)
            else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, paint)
    }
}
