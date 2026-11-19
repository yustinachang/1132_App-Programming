package com.example.finalapplication

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPage1 = findViewById<Button>(R.id.btnPage1)
        val btnPage2 = findViewById<Button>(R.id.btnPage2)
        val editText = findViewById<EditText>(R.id.editText)
        val titleImage = findViewById<ImageView>(R.id.titleImage)
        titleImage.setImageResource(getImageResource("title_image"))

        btnPage1.setOnClickListener{
            val intent = Intent(this, MainActivity3::class.java)
            val playNumber = editText.text.toString().toIntOrNull() ?: 1
            intent.putExtra("Key", playNumber)
            startActivity(intent)
        }

        btnPage2.setOnClickListener{
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    // 動態找 drawable 圖片當找不到都回傳 ic_unknown
    private fun getImageResource(resourceName: String): Int {
        val resId = resources.getIdentifier(resourceName, "drawable", packageName)
        return if (resId == 0) R.drawable.ic_unknown else resId
    }
}