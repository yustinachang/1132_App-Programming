package com.example.finalapplication3

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * UnitCube
 * OpenGL ES 2.0 立方體元件
 * - 可自訂邊長並繪製六面不同顏色
 * - 支援外部傳入 MVP 矩陣與面色
 */
class UnitCube(val unitSize: Float) {
    private val vertexBuffer: FloatBuffer    // 頂點座標緩衝
    private val drawListBuffer: ShortBuffer  // 頂點索引緩衝
    private val h = unitSize / 2f  // 根據給定邊長計算半邊長 (以中心為原點)
    // 立方體八個頂點座標（前4+後4）
    private val vertexCoords = floatArrayOf(
        // Front face (z = +h)
        -h,  h,  h,
        -h, -h,  h,
        h, -h,  h,
        h,  h,  h,
        // Back face (z = -h)
        -h,  h, -h,
        -h, -h, -h,
        h, -h, -h,
        h,  h, -h
    )
    // 每面兩個三角形，共36個 short index，順序：前後左右上下
    private val drawOrder = shortArrayOf(
        0, 1, 2, 0, 2, 3,     // Front
        4, 5, 6, 4, 6, 7,     // Back
        4, 5, 1, 4, 1, 0,     // Left
        3, 2, 6, 3, 6, 7,     // Right
        4, 0, 3, 4, 3, 7,     // Top
        1, 5, 6, 1, 6, 2      // Bottom
    )
    // 頂點著色器程式
    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
        }
    """
    // 片段著色器程式
    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
    """

    private var program: Int                 // OpenGL shader program
    private val COORDS_PER_VERTEX = 3        // 每個頂點3個座標
    private val vertexStride = COORDS_PER_VERTEX * 4 // 每個頂點間隔(bytes)

    init {
        // 初始化頂點緩衝
        val bb = ByteBuffer.allocateDirect(vertexCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer().apply {
            put(vertexCoords)
            position(0)
        }

        // 初始化索引緩衝
        val dlb = ByteBuffer.allocateDirect(drawOrder.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        drawListBuffer = dlb.asShortBuffer().apply {
            put(drawOrder)
            position(0)
        }

        // 編譯 shader 並建立 program
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    /**
     * 繪製立方體（六面可指定不同顏色）
     * @param mvpMatrix MVP矩陣（model-view-projection）由外部傳入
     * @param faceColors 六面顏色，順序 [前, 後, 左, 右, 上, 下]，每面 floatArrayOf(r,g,b,a)
     */
    fun draw(mvpMatrix: FloatArray, faceColors: Array<FloatArray>) {
        GLES20.glUseProgram(program)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        // 傳入頂點資料
        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        // 傳入顏色、矩陣 uniform
        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // 每面6個索引，依序繪製六面
        val FACE_INDEX_COUNT = 6
        for (face in 0 until 6) {
            GLES20.glUniform4fv(colorHandle, 1, faceColors[face], 0)
            drawListBuffer.position(face * FACE_INDEX_COUNT)  // 設定 drawListBuffer 的 position
            GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                FACE_INDEX_COUNT,
                GLES20.GL_UNSIGNED_SHORT,
                drawListBuffer
            )
        }
        // 還原 position
        drawListBuffer.position(0)

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    /**
     * 編譯 shader
     * @param type GLES20.GL_VERTEX_SHADER or GLES20.GL_FRAGMENT_SHADER
     * @param shaderCode shader 程式字串
     * @return shader 物件 id
     */
    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}