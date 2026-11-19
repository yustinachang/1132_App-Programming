package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val button9 = findViewById<Button>(R.id.button9)
        button9.setOnClickListener{
            startActivity(Intent(this,MainActivity2::class.java))
        }

        //ProgressBar1
        val button6 = findViewById<Button>(R.id.button6)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        button6.setOnClickListener{
            progressBar.visibility = View.VISIBLE
        }

        val button7 = findViewById<Button>(R.id.button7)
        button7.setOnClickListener{
            progressBar.visibility = View.INVISIBLE
        }

        //ProgressBar2
        var i = 0
        val button8 = findViewById<Button>(R.id.button8)
        val progressBar2 = findViewById<ProgressBar>(R.id.progressBar2)
        val txtView = findViewById<TextView>(R.id.textView)
        val handler = android.os.Handler()

        button8.setOnClickListener {
            progressBar2.visibility = View.VISIBLE
            i = progressBar.progress
            Thread({
                while (i < 100) {
                    i += 1
                    handler.post({
                        progressBar2.progress = i
                        txtView!!.text = i.toString()+"/" + progressBar2.max
                    })
                    Thread.sleep(100)
                    println("目前進度:${i}")
                }
                progressBar2.visibility = View.INVISIBLE
            }).start()

            /*
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
             */
        }
    }
}