package com.example.finalapplication

import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity1 : AppCompatActivity() {

    private lateinit var frameContainer: FrameLayout
    private lateinit var imgPuzzle: ImageView
    private lateinit var displayMetrics: DisplayMetrics
    private var shapeCount = 0
    private val placedViews = mutableSetOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)
        // 標題Number
        val playNumber = intent.getIntExtra("Key", 1)
        val textTitle = findViewById<TextView>(R.id.textTitle)
        textTitle.text = "Play Puzzle $playNumber"
        // TODO: database
        var shapeLimit = intent.getIntExtra("Key", 1)
        val textPuzzle = findViewById<TextView>(R.id.textPuzzle)
        textPuzzle.text = "x ${shapeLimit - shapeCount} / $shapeLimit shape"
        // 答案預覽功能
        var isAnswerShown = false
        val btnAnswer = findViewById<Button>(R.id.btnAnswer)
        btnAnswer.setOnClickListener {
            if (!isAnswerShown) {
                showAnswer()
                btnAnswer.text = "Close the Answer"
                isAnswerShown = true
            } else {
                hideAnswer()
                btnAnswer.text = "Check the Answer"
                isAnswerShown = false
            }
        }
        // 放入拼圖
        displayMetrics = resources.displayMetrics
        frameContainer = findViewById(R.id.frameContainer)
        imgPuzzle = findViewById(R.id.imgPuzzle)
        imgPuzzle.setImageResource(getImageResource("puzzle_image"))
        imgPuzzle.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && shapeCount < shapeLimit) {
                createDraggableShape()
                textPuzzle.text = "x ${shapeLimit - shapeCount} / $shapeLimit shape"
            }
            true
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showAnswer() {
        Toast.makeText(this, "答案顯示中 (範例)", Toast.LENGTH_SHORT).show()
        // TODO: 可繪製正確擺放位置等
    }

    private fun hideAnswer() {
        Toast.makeText(this, "已隱藏答案", Toast.LENGTH_SHORT).show()
        // TODO: 清除答案標示
    }

    private fun createDraggableShape() {
        val newShape = ImageView(this).apply {
            val shape_width = 1.5f
            val shape_height = shape_width / imgPuzzle.width * imgPuzzle.height
            // TODO: 抓取資料庫數值長寬
            setImageResource(getImageResource("puzzle_image"))
            layoutParams = FrameLayout.LayoutParams(cmToPx(shape_width), cmToPx(shape_height))
            x = 0f
            y = 0f
        }

        val rotationDetector = RotationGestureDetector(object :
            RotationGestureDetector.OnRotationGestureListener {
            override fun onRotation(rotationDetector: RotationGestureDetector, angle: Float) {
                newShape.rotation += angle
            }
        })

        newShape.setOnTouchListener { v, event ->
            rotationDetector.onTouchEvent(event)
            ShapeTouchListener(v).onTouch(v, event)
            true
        }

        frameContainer.addView(newShape)
        shapeCount++
    }

    inner class ShapeTouchListener(private val view: View) : View.OnTouchListener {
        private var dX = 0f
        private var dY = 0f
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            if (event.pointerCount == 1) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        placedViews.remove(v)
                        dX = event.x
                        dY = event.y
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val newX = v.x + event.x - dX
                        val newY = v.y + event.y - dY
                        v.x = newX.coerceIn(0f, frameContainer.width - v.width.toFloat())
                        v.y = newY.coerceIn(0f, frameContainer.height - v.height.toFloat())
                    }
                    MotionEvent.ACTION_UP -> {
                        if (isOverlapping(v)) {
                            Toast.makeText(this@MainActivity1, "無效擺放", Toast.LENGTH_SHORT).show()
                            v.x = 0f
                            v.y = 0f
                        } else {
                            placedViews.add(v) // 加入或已存在，不會重複
                        }
                    }
                }
            }
            return true
        }
    }
    // 重疊偵測
    private fun isOverlapping(currentView: View): Boolean {
        val currentRect = getRectFromView(currentView)
        return placedViews.any { otherView ->
            otherView != currentView && Rect.intersects(getRectFromView(otherView), currentRect)
        }
    }
    private fun getRectFromView(view: View): Rect {
        return Rect(
            (view.x).toInt(),
            (view.y).toInt(),
            (view.x + view.width).toInt(),
            (view.y + view.height).toInt()
        )
    }
    // puzzle 長寬換算
    fun cmToPx(cm: Float): Int {
        val px = cm / 2.54f * displayMetrics.xdpi  // xdpi: 每英吋有多少像素
        return px.toInt()
    }
    // 動態找 drawable 圖片當找不到都回傳 ic_unknown
    private fun getImageResource(resourceName: String): Int {
        val resId = resources.getIdentifier(resourceName, "drawable", packageName)
        return if (resId == 0) R.drawable.ic_unknown else resId
    }
}