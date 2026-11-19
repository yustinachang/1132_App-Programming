package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textView)
        val editText = findViewById<EditText>(R.id.editText) // 姓名
        val editText2 = findViewById<EditText>(R.id.editText2) // 性別
        val button = findViewById<Button>(R.id.button)

        // 檢查來自 MainActivity2 的數據
        val name = intent.getStringExtra("NAME") ?: "未知"
        val gender = intent.getStringExtra("GENDER") ?: "未知"
        val size = intent.getStringExtra("SIZE") ?: "未知"
        val color = intent.getStringExtra("COLOR") ?: "未知"
        textView.text = "姓名: $name\n性別: $gender\n尺寸: $size\n顏色: $color"

        button.setOnClickListener{
            val inputName  = editText.text.toString()
            val inputGender  = editText2.text.toString()

            val intent = Intent(this, MainActivity2::class.java).apply {
                putExtra("NAME", inputName)
                putExtra("GENDER", inputGender )
            }
            startActivity(intent)
        }
    }
}