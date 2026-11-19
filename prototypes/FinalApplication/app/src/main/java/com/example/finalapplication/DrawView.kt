package com.example.finalapplication

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.hypot

class DrawView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private val points = mutableListOf<PointF>()
    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
    }
    private val paintLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.selfgray)
        strokeWidth = 12f
    }

    private var draggingIndex: Int = -1
    private val circleRadius = 20f
    private val touchSlop = 30f
    private var downX = 0f
    private var downY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.coerceIn(0f, width.toFloat())
        val y = event.y.coerceIn(0f, height.toFloat())

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = x
                downY = y
                // 檢查是否點到現有點（拖曳或刪除）
                draggingIndex = points.indexOfFirst {
                    hypot(it.x - x, it.y - y) < circleRadius * 1.5
                }
                // 如果點到點，不馬上刪除，要等 ACTION_UP 判斷是拖移還是點擊
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (draggingIndex != -1) {
                    // 限制拖曳範圍在 View 內
                    points[draggingIndex].set(x, y)
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP -> {
                val distance = hypot(x - downX, y - downY)
                // 點到了現有點
                if (draggingIndex != -1) {
                    // 視為輕點 → 刪除
                    if (distance < touchSlop) {
                        points.removeAt(draggingIndex)
                    }
                    // 移動 → 拖移完成，不刪除
                    draggingIndex = -1
                } else {
                    // 沒點到點 → 新增點
                    points.add(PointF(x, y))
                }
                invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 畫封閉線條
        for (i in 0 until points.size - 1) {
            val p1 = points[i]
            val p2 = points[i + 1]
            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paintLine)
        }
        if (points.size > 2) {
            val first = points.first()
            val last = points.last()
            canvas.drawLine(last.x, last.y, first.x, first.y, paintLine)
        }
        // 畫點
        for (p in points) {
            canvas.drawCircle(p.x, p.y, circleRadius, paintCircle)
        }
    }

    fun clearPoints() {
        points.clear()
        invalidate()
    }

    fun getPoints(): List<PointF> {
        return points.toList() // 傳回一份複製，不會讓外部直接修改原資料
    }
}