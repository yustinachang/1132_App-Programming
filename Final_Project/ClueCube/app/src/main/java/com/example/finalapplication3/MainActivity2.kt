package com.example.finalapplication3

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * MainActivity2
 * 3D 立方體拼圖遊戲主畫面
 * - 提供三視圖提示與 3D 編輯操作，支援 Replay/Next 關卡機制
 */
class MainActivity2 : AppCompatActivity() {
    // 關卡尺寸
    private var size = 2
    private var sizeIndex = 0
    // 3D 互動區
    private lateinit var glSurfaceView: MyGLSurfaceView
    private lateinit var targetGrid: CubeGrid      // 目標答案（隨機）
    private lateinit var cubeGrid: CubeGrid        // 玩家操作的立方體
    private lateinit var targetView: OrthographicViewGenerator
    private lateinit var cubeView: OrthographicViewGenerator
    // UI 元件
    private lateinit var textView: TextView
    private lateinit var glContainer: FrameLayout
    private lateinit var frontView: ImageView
    private lateinit var sideView: ImageView
    private lateinit var topView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        // 取得 view
        textView = findViewById(R.id.textView)
        glContainer = findViewById(R.id.glSurfaceView)
        frontView = findViewById(R.id.frontView)
        sideView = findViewById(R.id.sideView)
        topView = findViewById(R.id.topView)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        // 初始化遊戲區與目標提示
        sizeIndex = intent.getIntExtra("sizeIndex", 0)
        newTargetGrid()
        setupStage()
        // 三視圖點擊時，旋轉立方體到對應視角
        frontView.setOnClickListener { glSurfaceView.setRotation(0f, 0f) }
        sideView.setOnClickListener { glSurfaceView.setRotation(0f, -90f) }
        topView.setOnClickListener { glSurfaceView.setRotation(-90f, 0f) }
        // 提交答案
        btnSubmit.setOnClickListener {
            // val allMatched = ViewDirection.values().all { dir ->
            //     targetView.getBitmap(dir).sameAs(cubeView.getBitmap(dir))
            // }
            submitStage(checkWithAnswer())
        }
        // 適應系統狀態列、導航列安全區
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // 新目標提示: 隨機產生新目標三視圖
    private fun newTargetGrid() {
        size = if (sizeIndex == 4) (2..5).random() else sizeIndex+2
        textView.text = "－ Level $size －"
        // 產生新的 cubeGridMap 與三視圖
        targetGrid = CubeGrid(size)
        targetGrid.randomizeGrid()
        targetView = OrthographicViewGenerator(targetGrid)
        frontView.setImageBitmap(targetView.getBitmap(ViewDirection.FRONT))
        sideView.setImageBitmap(targetView.getBitmap(ViewDirection.SIDE))
        topView.setImageBitmap(targetView.getBitmap(ViewDirection.TOP))
    }

    // 初始化遊戲區: 重製 cubeGrid 與 glSurfaceView
    private fun setupStage() {
        cubeGrid = CubeGrid(size)
        cubeView = OrthographicViewGenerator(cubeGrid)
        glContainer.removeAllViews()
        glSurfaceView = MyGLSurfaceView(this, cubeGrid)
        glSurfaceView.renderMode = android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY
        glContainer.addView(glSurfaceView)
    }

    // 結果 Dialog，提供 Back/Replay/Next 選項
    private fun submitStage(isEqual: Boolean) {
        val scrollView = ScrollView(this)
        val msgView = TextView(this).apply {
            text =
                if (isEqual) "\uD83C\uDF89 Congratulations!" else "\uD83E\uDEE4 Better luck next time."
            textSize = 24f
            setPadding(100, 100, 100, 100)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }
        scrollView.addView(msgView)

        AlertDialog.Builder(this)
            .setTitle("ClueCube Level $size")
            .setView(scrollView)
            .setNeutralButton("Back", null)
            .setNegativeButton("Replay") { _, _ -> setupStage() }
            .setPositiveButton("Next") { _, _ ->
                showCubeSizePicker{ selected ->
                    sizeIndex = selected
                    newTargetGrid()
                    setupStage()
                }
            }.show()
    }

    private fun checkWithAnswer():Boolean{
        var allMatched = true
        for (dir in ViewDirection.values()) for (i in 0 until size) for (j in 0 until size) {
            if (cubeView.isCellVisible(dir, i, j) != targetView.isCellVisible(dir, i, j)) {
                allMatched = false
                for (a in 0 until size) {
                    val (x, y, z) = when (dir) {
                        ViewDirection.FRONT -> Triple(size - 1 - i, size - 1 - j, a)
                        ViewDirection.SIDE -> Triple(a, size - 1 - j, i)
                        ViewDirection.TOP -> Triple(size - 1 - i, a, size - 1 - j)
                    }
                    val shouldHighlight = if (targetView.isCellVisible(dir, i, j)) {
                        targetGrid.isVisible(x, y, z)
                    } else {
                        cubeGrid.isVisible(x, y, z)
                    }
                    cubeGrid.setHighlight(x, y, z, shouldHighlight)
                }
            }
        }
        if (allMatched) { cubeGrid.clearAllHighlight() }
        glSurfaceView.requestRender()
        return allMatched
    }
}