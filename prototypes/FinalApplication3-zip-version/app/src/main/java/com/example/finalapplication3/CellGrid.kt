package com.example.finalapplication3

import android.content.Context
import android.graphics.Color
import android.widget.GridLayout
import android.widget.ImageView

// 狀態顏色集中管理
enum class CellStatus(val colorHex: String) {
    DEFAULT("#D3D3D3"),
    FRONT("#FFEFC062"),
    SIDE("#FFEA7E73"),
    TOP("#FF61CCCC"),
    ERROR("#FF0000")
}

data class Cell(
    var isFilled: Boolean = false,
    var status: CellStatus = CellStatus.DEFAULT
)

/**
 * CellGrid 封裝一個三視圖格子區域的操作與顯示
 * name - 名稱 (如 "Front")
 * fillStatus - 填色時顯示的顏色
 */
class CellGrid(private val fillStatus: CellStatus) {
    lateinit var grid: Array<Array<Cell>>
    lateinit var views: Array<Array<ImageView>>

    /**
     * 初始化格子內容與點擊行為，掛載於指定 GridLayout
     */
    fun setup(context: Context, size: Int, layout: GridLayout) {
        grid = Array(size) { Array(size) { Cell() } }
        views = Array(size) { Array(size) { ImageView(context) } }
        layout.removeAllViews()
        layout.rowCount = size
        layout.columnCount = size
        val cellSize =
            (100 * context.resources.displayMetrics.density / (1.2f * size - 0.2f)).toInt()
        val margin = (cellSize * 0.1f).toInt()

        for (j in 0 until size) for (i in 0 until size) {
            val cell = ImageView(context)
            cell.setBackgroundColor(Color.LTGRAY)
            cell.setOnClickListener {
                val c = grid[i][j]
                c.isFilled = !c.isFilled
                c.status = if (c.isFilled) fillStatus else CellStatus.DEFAULT
                updateCellColor(cell, c.status)
            }
            val param = GridLayout.LayoutParams()
            param.width = cellSize
            param.height = cellSize
            param.setMargins(margin, margin, margin, margin)
            cell.layoutParams = param
            layout.addView(cell)
            views[i][j] = cell
        }
        refreshAll()
    }

    /**
     * 依照目前格子狀態刷新顏色
     */
    fun refreshAll() {
        for (i in grid.indices) for (j in grid[i].indices) {
            updateCellColor(views[i][j], grid[i][j].status)
        }
    }

    /**
     * 檢查玩家填答與正確答案並標記錯誤格，回傳是否全對
     */
    fun checkWithAnswer(ans: Array<BooleanArray>): Boolean {
        var ok = true
        for (i in grid.indices) for (j in grid[i].indices) {
            val correct = ans[i][j]
            val c = grid[i][j]
            if (c.isFilled != correct) {
                c.status = CellStatus.ERROR
                ok = false
            } else {
                c.status = if (c.isFilled) fillStatus else CellStatus.DEFAULT
            }
        }
        refreshAll()
        return ok
    }

    private fun updateCellColor(cell: ImageView, status: CellStatus) {
        cell.setBackgroundColor(Color.parseColor(status.colorHex))
    }
}