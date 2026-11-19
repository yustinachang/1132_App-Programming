package com.example.finalapplication3

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

enum class ViewDirection(val color: Int) {
    FRONT(0xFFEFC062.toInt()), // 黃色
    TOP(0xFF61CCCC.toInt()),   // 藍色
    SIDE(0xFFEA7E73.toInt())   // 紅色
}

/**
 * OrthographicViewGenerator
 * 用於生成立方體 3D 結構的三視圖 (側視圖、前視圖、俯視圖)
 * - 將 3D cubeGrid 以 2D Bitmap 呈現
 * - 可產生三視圖對應的 BooleanArray，供答題比對
 */
class OrthographicViewGenerator(private val cubeGrid: CubeGrid) {

    val size = cubeGrid.size        // 立方體邊長
    private val cellSize = 40       // 單格像素
    private val margin = 10         // 邊界留白

    fun getBitmap(view: ViewDirection): Bitmap {
        val bitmap = Bitmap.createBitmap(
            size * cellSize + margin * 2,
            size * cellSize + margin * 2,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        val paint = Paint().apply {
            style = Paint.Style.FILL
            color = view.color
        }
        for (i in 0 until size) for (j in 0 until size) {
            if (isCellVisible(view, i, j)) {
                val left = margin + i * cellSize
                val top = margin + j * cellSize
                canvas.drawRect(
                    left.toFloat(),
                    top.toFloat(),
                    (left + cellSize).toFloat(),
                    (top + cellSize).toFloat(),
                    paint
                )
            }
        }
        return bitmap
    }

    fun getBoolArray(view: ViewDirection): Array<BooleanArray> =
        Array(size) { i ->
            BooleanArray(size) { j ->
                isCellVisible(view, i, j)
            }
        }

    fun isCellVisible(view: ViewDirection, i: Int, j: Int): Boolean =
        (0 until size).any { depth ->
            when (view) {
                ViewDirection.FRONT -> cubeGrid.isVisible(size - 1 - i, size - 1 - j, depth)
                ViewDirection.TOP   -> cubeGrid.isVisible(size - 1 - i, depth, size - 1 - j)
                ViewDirection.SIDE  -> cubeGrid.isVisible(depth, size - 1 - j, i)
            }
        }
}
