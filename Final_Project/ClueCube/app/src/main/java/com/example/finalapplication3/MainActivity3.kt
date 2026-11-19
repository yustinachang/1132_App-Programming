package com.example.finalapplication3

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ScrollView
import android.widget.FrameLayout
import android.widget.GridLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * 3D結構三視圖挑戰主畫面
 * - 展示 3D 立體結構，玩家於三個格子區(前/側/俯)自由填寫
 * - 檢查答案並自動標色提示正確/錯誤
 */
class MainActivity3 : AppCompatActivity() {
    private var size = 2
    private var sizeIndex = 0

    private lateinit var glSurfaceView: MyGLSurfaceView
    private lateinit var targetGrid: CubeGrid

    private lateinit var textView: TextView
    private lateinit var glContainer: FrameLayout
    private lateinit var gridFront: GridLayout
    private lateinit var gridSide: GridLayout
    private lateinit var gridTop: GridLayout

    private lateinit var orthoFront: CellGrid
    private lateinit var orthoSide: CellGrid
    private lateinit var orthoTop: CellGrid

    private lateinit var ansFront: Array<BooleanArray>
    private lateinit var ansSide: Array<BooleanArray>
    private lateinit var ansTop: Array<BooleanArray>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        // 取得 view
        textView = findViewById(R.id.textView)
        glContainer = findViewById(R.id.glSurfaceView)
        gridFront = findViewById(R.id.gridFront)
        gridSide = findViewById(R.id.gridSide)
        gridTop = findViewById(R.id.gridTop)
        val frontView = findViewById<TextView>(R.id.frontView)
        val sideView = findViewById<TextView>(R.id.sideView)
        val topView = findViewById<TextView>(R.id.topView)
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
            val okFront = orthoFront.checkWithAnswer(ansFront)
            val okSide = orthoSide.checkWithAnswer(ansSide)
            val okTop = orthoTop.checkWithAnswer(ansTop)
            submitStage(okFront && okSide && okTop)
        }
        // 適應系統狀態列、導航列安全區
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * 產生/重設隨機立體結構與正確三視圖
     */
    private fun newTargetGrid() {
        size = if (sizeIndex == 4) (2..5).random() else sizeIndex+2
        textView.text = "－ Level $size －"
        // 產生新的 cubeGridMap 與三視圖
        targetGrid = CubeGrid(size)
        targetGrid.randomizeGrid()
        glContainer.removeAllViews()
        glSurfaceView = MyGLSurfaceView(this, targetGrid)
        glSurfaceView.isFocusable = false
        glSurfaceView.renderMode = android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY
        glContainer.addView(glSurfaceView)

        ansFront = OrthographicViewGenerator(targetGrid).getBoolArray(ViewDirection.FRONT)
        ansSide = OrthographicViewGenerator(targetGrid).getBoolArray(ViewDirection.SIDE)
        ansTop = OrthographicViewGenerator(targetGrid).getBoolArray(ViewDirection.TOP)
    }

    /**
     * 建立三個可互動三視圖輸入格
     */
    private fun setupStage() {
        orthoFront = CellGrid(CellStatus.FRONT)
        orthoSide = CellGrid(CellStatus.SIDE)
        orthoTop = CellGrid(CellStatus.TOP)

        orthoFront.setup(this, size, gridFront)
        orthoSide.setup(this, size, gridSide)
        orthoTop.setup(this, size, gridTop)
    }

    /**
     * 顯示答題結果，並依需求提供 Replay/Next
     */
    private fun submitStage(isEqual: Boolean) {
        val scrollView = ScrollView(this)
        val msgView = TextView(this).apply {
            text = if (isEqual) "\uD83C\uDF89 Congratulations!" else "\uD83E\uDEE4 Better luck next time."
            textSize = 24f
            setPadding(100, 100, 100, 100)
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        }
        scrollView.addView(msgView)

        AlertDialog.Builder(this)
            .setTitle("ClueCube Level $size")
            .setView(scrollView)
            .setNeutralButton("Back", null)
            .setNegativeButton("Replay") { _, _ -> setupStage() }
            .setPositiveButton("Next") { _, _ ->
                showCubeSizePicker { selected ->
                    sizeIndex = selected
                    newTargetGrid()
                    setupStage()
                }
            }.show()
    }
}