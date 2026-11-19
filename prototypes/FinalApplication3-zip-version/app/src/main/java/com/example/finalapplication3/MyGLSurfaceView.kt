package com.example.finalapplication3

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.KeyEvent
import android.view.MotionEvent

/**
 * MyGLSurfaceView
 * - 提供 3D 立方體互動的 GLSurfaceView
 * - 支援觸控旋轉、鍵盤選擇與操作單一 cube
 */
class MyGLSurfaceView(context: Context, val cubeGrid: CubeGrid) : GLSurfaceView(context) {
    private val renderer: MyGLRenderer
    private var previousX = 0f   // 上次觸控位置 X
    private var previousY = 0f   // 上次觸控位置 Y
    private var focusedCube = Triple(0, 0, 0) // 目前鍵盤聚焦的 cube 座標

    init {
        setEGLContextClientVersion(2)      // 使用 OpenGL ES 2.0
        renderer = MyGLRenderer(cubeGrid)  // 建立渲染器
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY // 只在需要時重繪（效能佳）
        isFocusable = true                 // 允許鍵盤聚焦
        isFocusableInTouchMode = true      // 允許觸控聚焦
        requestFocus()
    }

    // 旋轉整個立方體（由主畫面三視圖點擊呼叫）
    fun setRotation(x: Float, y: Float){
        renderer.angleX = x
        renderer.angleY = y
        requestRender()
    }

    // 處理觸控事件：拖曳旋轉 3D 視角
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY
                renderer.angleX += dy * 0.5f
                renderer.angleY += dx * 0.5f
                requestRender()
            }
        }

        previousX = x
        previousY = y
        return true
    }

    // 處理鍵盤事件：方向鍵/WASD/EQ 導覽、空白/Enter/中心鍵切換 cube 狀態
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val size = cubeGrid.size
        var (x, y, z) = focusedCube

        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_A -> if (x < size - 1) x++
            KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_D -> if (x > 0) x--
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_W -> if (y < size - 1) y++
            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_S -> if (y > 0) y--
            KeyEvent.KEYCODE_PAGE_UP, KeyEvent.KEYCODE_E -> if (z < size - 1) z++
            KeyEvent.KEYCODE_PAGE_DOWN, KeyEvent.KEYCODE_Q -> if (z > 0) z--
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_SPACE -> {
                // 切換目前聚焦 cube 的 visible 狀態
                renderer.handleTouch(
                    MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 0f, 0f, 0),
                    Triple(x, y, z)
                )
                requestRender()
                return true
            }
            else -> return super.onKeyDown(keyCode, event)
        }
        // 更新聚焦位置並高亮顯示
        focusedCube = Triple(x, y, z)
        renderer.cubeGrid.clearAllHover()
        renderer.cubeGrid.setHovered(x, y, z, true)
        requestRender()
        return true
    }
}