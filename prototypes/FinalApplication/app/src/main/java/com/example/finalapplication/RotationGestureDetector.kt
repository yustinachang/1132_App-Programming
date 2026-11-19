package com.example.finalapplication

import android.view.MotionEvent
import kotlin.math.atan2

class RotationGestureDetector(private val listener: OnRotationGestureListener) {
    private var fX = 0f
    private var fY = 0f
    private var sX = 0f
    private var sY = 0f
    private var angle = 0f

    interface OnRotationGestureListener {
        fun onRotation(rotationDetector: RotationGestureDetector, angle: Float)
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                fX = event.getX(0)
                fY = event.getY(0)
                sX = event.getX(1)
                sY = event.getY(1)
            }
            MotionEvent.ACTION_MOVE -> if (event.pointerCount >= 2) {
                val nfX = event.getX(0)
                val nfY = event.getY(0)
                val nsX = event.getX(1)
                val nsY = event.getY(1)

                angle = angleBetweenLines(fX, fY, sX, sY, nfX, nfY, nsX, nsY)

                listener.onRotation(this, angle)
            }
        }
        return true
    }

    private fun angleBetweenLines(fx1: Float, fy1: Float, fx2: Float, fy2: Float,
                                  sx1: Float, sy1: Float, sx2: Float, sy2: Float): Float {
        val angle1 = atan2((fy1 - fy2), (fx1 - fx2))
        val angle2 = atan2((sy1 - sy2), (sx1 - sx2))

        var angle = Math.toDegrees((angle1 - angle2).toDouble()).toFloat() % 360
        if (angle < -180f) angle += 360.0f
        if (angle > 180f) angle -= 360.0f
        return angle
    }
}
