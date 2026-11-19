package com.example.myapplication

import android.content.Intent
import android.widget.Button
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        /*
        //課堂練習1
        val intent = Intent()
        intent.setClass(this, MainActivity2::class.java)
        startActivity(intent)
         */

        /*
        //課堂練習2
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            startActivity(Intent(this,MainActivity2::class.java))
        }
         */

        //課堂練習3
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            val intent = Intent(this, MainActivity2::class.java)
            intent.putExtra("Key", 123)
            startActivity(intent)
        }

        //setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}