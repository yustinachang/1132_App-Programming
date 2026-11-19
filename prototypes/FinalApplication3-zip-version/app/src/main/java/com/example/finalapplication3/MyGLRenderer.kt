package com.example.finalapplication3

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.MotionEvent
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * MyGLRenderer
 * 用於 CubeGrid 3D 視覺化和互動的 OpenGL ES 2.0 Renderer
 * - 支援旋轉 (angleX, angleY), hover/visible 狀態顯示, 和 touch/keyboard 事件處理。
 */
class MyGLRenderer(val cubeGrid: CubeGrid) : GLSurfaceView.Renderer {
    // 旋轉角度（由外部控制）
    var angleX = -15f
    var angleY = -30f
    // 上次觸控位置，用於拖曳旋轉
    private var previousX = 0f
    private var previousY = 0f
    // OpenGL 需要的矩陣
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)
    // OpenGL Surface 創建時初始化參數
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1f) // 背景色：深灰
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)     // 啟用深度測試
    }
    // 每一幀繪製 3D 場景
    override fun onDrawFrame(gl: GL10?) {
        // 清空畫面與深度緩衝
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        // 設定攝影機（固定位置）
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -10f, 0f, 0f, 0f, 0f, 1f, 0f)
        // 設定旋轉矩陣（根據目前 angleX/angleY）
        Matrix.setRotateM(rotationMatrix, 0, angleX, 1f, 0f, 0f)
        Matrix.rotateM(rotationMatrix, 0, angleY, 0f, 1f, 0f)
        // 立方體配置與單位尺寸
        val size = cubeGrid.size     // 大立方體的單邊格子數
        val bigCubeLength = 3.0f     // 大立方體的邊長
        val unitSize = bigCubeLength / (1.1f * size - 0.1f) // 小 cube 的邊長
        val spacing = unitSize * 1.1f // 小 cube 之間的中心間距
        val offset = (bigCubeLength - unitSize) / 2f     // 將最邊緣的小 cube 置於大立方體邊界
        // 依序繪製每個小 cube
        for (x in 0 until size) {
            for (y in 0 until size) {
                for (z in 0 until size) {
                    val cube = cubeGrid.grid[x][y][z]
                    // 只要 visible 或 hovered 就繪製
                    if (!(cube.visible || cube.hovered || cube.highlight)) continue
                    // 計算此 cube 的 3D 位置
                    val tx = x * spacing - offset
                    val ty = y * spacing - offset
                    val tz = z * spacing - offset
                    // 建立模型矩陣（先平移再旋轉）
                    Matrix.setIdentityM(modelMatrix, 0)
                    Matrix.translateM(modelMatrix, 0, tx, ty, tz)
                    Matrix.multiplyMM(modelMatrix, 0, rotationMatrix, 0, modelMatrix, 0)
                    // MVP = 投影 * 視角 * 模型
                    Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
                    Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)
                    // 根據狀態決定顏色
                    val faceColors = Array(6) { floatArrayOf(0.94f, 0.94f, 0.94f, 1f) }   // 其它面 white
                    if (z == 0) faceColors[1] = floatArrayOf(0.94f, 0.75f, 0.38f, 1f)          // frontview 黃 #FFEFC062
                    if (x == 0) faceColors[2] = floatArrayOf(0.92f, 0.49f, 0.45f, 1f)          // sideview #FFEA7E73
                    if (y == size - 1) faceColors[4] = floatArrayOf(0.38f, 0.8f, 0.8f, 1f)     // topview 藍 #FF61CCCC
                    when {
                        cube.hovered && cube.visible -> for (i in 0..5) faceColors[i] = floatArrayOf(0f, 1f, 0f, 1f)     // hover 綠
                        cube.hovered && !cube.visible -> for (i in 0..5) faceColors[i] = floatArrayOf(0f, 0.5f, 0f, 1f)  // hover 暗綠
                        cube.highlight ->  for (i in 0..5) faceColors[i] = floatArrayOf(1f, 0f, 0f, 1f)
                    }
                    // 繪製小立方體
                    UnitCube(unitSize).draw(mvpMatrix, faceColors)
                }
            }
        }
    }

    // 畫面大小改變時，重設投影矩陣
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 20f)
    }

    /**
     * 處理觸控事件（由 SurfaceView 呼叫）
     * @param event 觸控事件
     * @param hitCube 若有指定，代表哪一顆 cube 被點擊
     */
    fun handleTouch(event: MotionEvent, hitCube: Triple<Int, Int, Int>? = null): Boolean {
        when (event.action) {
            // 點擊時，設定 hover
            MotionEvent.ACTION_DOWN -> {
                cubeGrid.clearAllHover()
                hitCube?.let { (x, y, z) -> cubeGrid.setHovered(x, y, z, true) }
            }
            // 放開時，切換 visible 狀態並同步 hover
            MotionEvent.ACTION_UP -> {
                hitCube?.let { (x, y, z) ->
                    cubeGrid.toggleVisible(x, y, z)
                    cubeGrid.setHovered(x, y, z, cubeGrid.isVisible(x, y, z))
                    cubeGrid.setHighlight(x, y, z, false)
                }
            }
            // 拖曳旋轉視角
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - previousX
                val dy = event.y - previousY
                angleX += dy * 0.5f
                angleY += dx * 0.5f
            }
        }
        previousX = event.x
        previousY = event.y
        return true
    }
}