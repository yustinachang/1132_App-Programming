package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        val textView2 = findViewById<TextView>(R.id.textView2)
        val editText3 = findViewById<EditText>(R.id.editText3) // 尺寸
        val editText4 = findViewById<EditText>(R.id.editText4) // 顏色
        val button2 = findViewById<Button>(R.id.button2)

        // 檢查來自 MainActivity 的數據
        val name = intent.getStringExtra("NAME") ?: "未知"
        val gender = intent.getStringExtra("GENDER") ?: "未知"
        val size = intent.getStringExtra("SIZE") ?: "未知"
        val color = intent.getStringExtra("COLOR") ?: "未知"
        textView2.text = "姓名: $name\n性別: $gender\n尺寸: $size\n顏色: $color"

        button2.setOnClickListener {
            val size = editText3.text.toString()
            val color = editText4.text.toString()

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("NAME", name)
                putExtra("GENDER", gender)
                putExtra("SIZE", size)
                putExtra("COLOR", color)
            }
            startActivity(intent)
        }
    }
}