package com.example.finalapplication3

import kotlin.random.Random

/**
 * CubeState
 * - 表示單一小立方體的狀態
 * @property visible 是否顯示/存在
 * @property hovered 是否為目前 hover 狀態（高亮）
 */
data class CubeStatus(
    var visible: Boolean = true,
    var hovered: Boolean = false,
    var highlight: Boolean = false
)

/**
 * CubeGrid
 * - 負責管理 NxNxN 立方體陣列的狀態
 * - 提供隨機產生、查詢及操作各 cube 狀態的方法
 */
class CubeGrid(val size: Int) {
    // 3D 狀態陣列，每格存一個 CubeState
    val grid: Array<Array<Array<CubeStatus>>> =
        Array(size) {
            Array(size) {
                Array(size) {
                    CubeStatus()
                }
            }
        }

    var fillRatio: Float = (6-size)*0.1f  // 隨機產生時，每格被填滿的機率

    // 隨機填滿 grid（依照 fillRatio）
    fun randomizeGrid() {
        for (x in 0 until size)
            for (y in 0 until size)
                for (z in 0 until size)
                    grid[x][y][z].visible = Random.nextFloat() < fillRatio
    }

    // 切換指定小 cube 的 visible 狀態
    fun toggleVisible(x: Int, y: Int, z: Int) {
        getCube(x, y, z)?.let { it.visible = !it.visible }
    }

    // 設定指定小 cube 的 visible 狀態
    fun setVisible(x: Int, y: Int, z: Int, visible: Boolean) {
        getCube(x, y, z)?.visible = visible
    }

    // 查詢指定小 cube 是否 visible
    fun isVisible(x: Int, y: Int, z: Int): Boolean {
        return getCube(x, y, z)?.visible ?: false
    }

    // 設定指定小 cube 的 hovered 狀態
    fun setHovered(x: Int, y: Int, z: Int, hovered: Boolean) {
        getCube(x, y, z)?.hovered = hovered
    }

    // 查詢指定小 cube 是否 hovered
    fun isHovered(x: Int, y: Int, z: Int): Boolean {
        return getCube(x, y, z)?.hovered ?: false
    }

    // 將全部小 cube 的 hovered 狀態清空
    fun clearAllHover() {
        for (x in 0 until size)
            for (y in 0 until size)
                for (z in 0 until size)
                    grid[x][y][z].hovered = false
    }

    // 設定指定小 cube 的 highlight 狀態
    fun setHighlight(x: Int, y: Int, z: Int, highlight: Boolean) {
        getCube(x, y, z)?.highlight = highlight
    }

    // 查詢指定小 cube 是否 highlight
    fun isHighlight(x: Int, y: Int, z: Int): Boolean {
        return getCube(x, y, z)?.highlight ?: false
    }

    fun clearAllHighlight() {
        for (x in 0 until size)
            for (y in 0 until size)
                for (z in 0 until size)
                    grid[x][y][z].highlight = false
    }

    // 取得座標 (x, y, z) 的 CubeState，超出範圍則回傳 null
    private fun getCube(x: Int, y: Int, z: Int): CubeStatus? {
        return grid.getOrNull(x)?.getOrNull(y)?.getOrNull(z)
    }
}