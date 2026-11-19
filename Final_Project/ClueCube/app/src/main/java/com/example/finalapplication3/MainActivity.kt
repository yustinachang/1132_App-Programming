package com.example.finalapplication3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 導航頁面2
        val btnPlay2 = findViewById<Button>(R.id.btnPlay2)
        btnPlay2.setOnClickListener{
            showCubeSizePicker { selected ->
                val intent = Intent(this, MainActivity2::class.java)
                intent.putExtra("sizeIndex", selected)
                startActivity(intent)
            }
        }
        // 導航頁面3
        val btnPlay3 = findViewById<Button>(R.id.btnPlay3)
        btnPlay3.setOnClickListener{
            showCubeSizePicker { selected ->
                val intent = Intent(this, MainActivity3::class.java)
                intent.putExtra("sizeIndex", selected)
                startActivity(intent)
            }

        }
        // 適應系統狀態列、導航列安全區
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}